package com.startup.eventsearcher.main.ui.map.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.startup.eventsearcher.utils.Config;

public class PermissionMapProvider {

    private static final String TAG = "tgPermissionMapProvider";
    private final IPermissionMapProvider iPermissionMapProvider;
    private final Context context;
    private boolean locationPermissionGranted = false;

    public PermissionMapProvider(IPermissionMapProvider iPermissionMapProvider, Context context) {
        this.iPermissionMapProvider = iPermissionMapProvider;
        this.context = context;
    }

    public boolean isLocationPermissionGranted() {
        return locationPermissionGranted;
    }

    public void setLocationPermissionGranted(boolean locationPermissionGranted) {
        this.locationPermissionGranted = locationPermissionGranted;
    }

    /*1. Проверка GoogleApiAvailable():
            - доступно: идем дальше
            - не доступно: пользователь может решить эту проблему (вызов диалога)
            - не доступно: пользователь НЕ может решить эту проблему (работа с картой не доступна)
    * 2. Проверка locationTurnOn() - включена ли геолокация на устройстве
            - да: переходим к проверке разрешения приложения на использования геолокации checkLocationPermission()
            - нет: buildAlertDialogTurnOnLocation() открывается диалог (Для работы приложения необоходимо включить геолокацию)
                - да: переход в НАСТРОЙКИ -> ответ прилетает в onActivityResult() (в нем вызывается checkLocationPermission())
                - нет: onEndGetPermissions() -> initMap() (геолокация не доступна)
    * 3. Проверка checkLocationPermission() - резрешен ли доступ к геолокации приложению
            - locationPermissionGranted = true: onEndGetPermissions() -> initMap()
            - locationPermissionGranted = false: вызов getLocationPermission()
                - разрешение на геолокацию у приложения есть: onEndGetPermissions() -> initMap()
                - разрешения на геолокацию у приложения нет: вызов requestPermission() -> requestPermission(),
                    ответет прилетает в onRequestPermissionsResult() где устанавливается флаг locationPermissionGranted и
                    происходит initMap(). */
    public void getPermissions() {
        if (isGoogleApiAvailable()) {
            if (isLocationTurnOn()) {
                checkLocationPermission();
            }else {
                buildAlertDialogTurnOnLocation();
            }
        }
    }

    //Также вызывается после выхода из НАСТРОЕК (включение локации)
    public void checkLocationPermission() {
        if (locationPermissionGranted) {
            iPermissionMapProvider.onEndGetPermissions();
        } else {
            getLocationPermission();
        }
    }

    //Проверка доступны ли google services устройству
    private boolean isGoogleApiAvailable() {
        Log.d(TAG, "isGoogleApiAvailable: Проверка доступны ли google services устройству");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isGoogleApiAvailable: Устройство имеет доступ к google services");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isGoogleApiAvailable: Обнаружена ошибка, но пользователь может решить её");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, available, Config.ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Log.d(TAG, "isGoogleApiAvailable: Устройтсво НЕ может работать с Google Maps");
            Toast.makeText(context, "Устройство не может работать с Google Maps", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //Включена ли локация на устройстве
    private boolean isLocationTurnOn() {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "isLocationTurnOn: На устройстве НЕ включена геолокация");
            return false;
        }
        Log.d(TAG, "isLocationTurnOn: На устройстве включена геолокация");
        return true;
    }

    //Проверка разрешения на геолокацию
    private void getLocationPermission() {
        locationPermissionGranted = false;
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Если НЕТ разрешения, то спрашиваем разрешение
            Log.d(TAG, "getLocationPermission: Запрос разрешения геолокации");
            iPermissionMapProvider.onRequestLocationPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Config.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            //Если есть разрешение, то ставим флаг в true
            Log.d(TAG, "getLocationPermission: Доступ к геолокации был предоставлен ранее");
            locationPermissionGranted = true;
            iPermissionMapProvider.onEndGetPermissions();
        }
    }

    //Диалоговое окно включения локации на устройстве
    private void buildAlertDialogTurnOnLocation() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Для работы приложения рекомендуется включить геолокацию, включить?")
                .setNegativeButton("Нет, спасибо", (dialog, id) -> {
                    /*Отказ о включении геолокации.
                    В этом случае нет возможности работать с геолокацией пользователя,
                    но добавить эвент возможно*/
                    Log.d(TAG, "buildAlertDialogTurnOnLocation: Отказ о включении геолокации");
                    iPermissionMapProvider.onEndGetPermissions();
                })
                .setPositiveButton("Да", (dialog, id) -> {
                    Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    iPermissionMapProvider.onStartDeviceSettingsActivity(enableGpsIntent, Config.PERMISSIONS_REQUEST_ENABLE_GPS);
                    Log.d(TAG, "buildAlertDialogTurnOnLocation: Пользователь перешел в настройки для включения геолокации");
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
