package com.startup.eventsearcher.main.ui.map.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.EventsList;
import com.startup.eventsearcher.utils.Config;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapHandler {

    private static final String TAG = "MapHandler";

    public interface Callback{
        void initMap();
        void startActivity(Intent intent, int permissionsRequest);
        void requestPermissionsHandler(String[] strings, int permissionsRequest);
        void returnDeviceLocation(LatLng latLng);
    }

    static Callback callback;

    public static void registerMapHandlerCallBack(Callback callback){
        MapHandler.callback = callback;
    }

    private static boolean locationPermissionGranted = false;

    public static boolean isLocationPermissionGranted() {
        return locationPermissionGranted;
    }

    public static void setLocationPermissionGranted(boolean locationPermissionGranted) {
        MapHandler.locationPermissionGranted = locationPermissionGranted;
    }

    //Проверка доступны ли google services устройству
    public static boolean isServicesOK(Context context) {
        Log.d(TAG, "isServicesOK: Проверка доступны ли google services устройству");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Устройство имеет доступ к google services");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: Обнаружена ошибка, но пользователь может решить её");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, available, Config.ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Log.d(TAG, "isServicesOK: Ваше устройтсво НЕ может работать с Google Maps");
            Toast.makeText(context, "Ваше устройство не может работать с Google Maps", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //Включена ли на устройстве геолокация
    public static boolean isMapsEnabled(Context context) {
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "isMapsEnabled: На устройстве НЕ включена геолокация");
            return false;
        }
        return true;
    }

    //Диалоговое окно включения локации на устройстве
    public static void buildAlertMessageNoGps(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Для работы приложения необоходимо включить геолокацию, включить?")
                .setNegativeButton("Нет, спасибо", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        /*Отказ о включении геолокации.
                        В этом случае нет возможности работать с геолокацией пользователя,
                        но добавить эвент возможно*/
                        Log.d(TAG, "buildAlertMessageNoGps: Отказ о включении геолокации");
                        callback.initMap();
                    }
                })
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        callback.startActivity(enableGpsIntent, Config.PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public static void getLocationPermission(Context context) {
        //Проверка разрешения на геолокацию
        locationPermissionGranted = false;
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Если НЕТ разрешения, то спрашиваем разрешение
            Log.d(TAG, "getLocationPermission: Запрос разрешения геолокации");
            callback.requestPermissionsHandler(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    Config.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            //Если есть разрешение, то ставим флаг в true
            Log.d(TAG, "getLocationPermission: Доступ к геолокации был предоставлен ранее");
            locationPermissionGranted = true;
            callback.initMap();
        }
    }

    //Получение адреса метки пользователя
    public static Address getAddress(Context context, LatLng latLng) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        Address addressObject = addresses.get(0);

        //в addressObject лежит ближайшая координата к области, отмеченной пользователем
        //Нам нужна та координата которую поставил непосредсвенно пользователь
        addressObject.setLatitude(latLng.latitude);
        addressObject.setLongitude(latLng.longitude);

        return addressObject;
    }

    //Установка эвент-маркеров на карте
    public static void setEventMarkerOnMap(GoogleMap map) {
        Log.d(TAG, "setEventMarkerOnMap: ");
        Log.d(TAG, "setEventMarkerOnMap: eventArrayList = " + EventsList.getEventArrayList().toString());
        for (Event event: EventsList.getEventArrayList()){
            LatLng latLng = new LatLng(event.getEventAddress().getLatitude(),
                    event.getEventAddress().getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(event.getHeader());
            Marker marker = map.addMarker(markerOptions);
            marker.setTag(event);
        }
    }

    public static void getDeviceLocation(Context context) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        try {
            Task<Location> location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getDeviceLocation: Task получения геолокации выполнена успешно");
                        Location currentLocation = task.getResult();
                        if (currentLocation != null) {
                            Log.d(TAG, "getDeviceLocation: Текущая геолокация определена");
                            LatLng latLng = new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude());
                            callback.returnDeviceLocation(latLng);
                        } else {
                            Log.d(TAG, "getDeviceLocation: Текущая геолокация НЕ определена");
                            Toast.makeText(context, "Текущая геолокация не определена", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d(TAG, "getDeviceLocation: Task получения геолокации НЕ выполнена");
                        Toast.makeText(context, "Не удалось получить геолокацию", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(e -> Log.d(TAG, "getDeviceLocation: addOnFailureListener: " + e.getMessage()));
        } catch (SecurityException e) {
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

}
