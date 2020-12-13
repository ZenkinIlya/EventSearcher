package com.startup.eventsearcher.main.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.startup.eventsearcher.R;

import java.util.Objects;

public class MapsFragment extends Fragment {

    private static final String TAG = "myMap";

    public static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;

    private boolean locationPermissionGranted = false;

    FusedLocationProviderClient fusedLocationProviderClient;
    SupportMapFragment mapFragment;
    private static final float DEFAULT_ZOOM = 15f;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated()");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach()");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        super.onResume();
        if(checkMapServices()){
            if(locationPermissionGranted){
                initMap();
            }
            else{
                getLocationPermission();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }

    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
                return true;
            }
        }
        return false;
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: Проверка доступны ли google services устройству");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "isServicesOK: Устройство имеет доступ к google services");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: Обнаружена ошибка, но пользователь может решить её");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Log.d(TAG, "isServicesOK: Ваше устройтсво НЕ может работать с Google Maps");
            Toast.makeText(getContext(), "Ваше устройтсво не может работать с Google Maps", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //Включена ли на устройстве геолокация
    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "isMapsEnabled: На устройстве НЕ включена геолокация");
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    //
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Для работы приложения необоходимо включить геолокацию, хотите ли вы включить геолокацию?")
                .setCancelable(true)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(): requestCode=" +requestCode+ " resultCode=" +resultCode);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(locationPermissionGranted){
                    Log.d(TAG, "onActivityResult: Доступ к геолокации предоставлен");
                    initMap();
                }
                else{
                    Log.d(TAG, "onActivityResult: Доступ к геолокации НЕ предоставлен");
                    getLocationPermission();
                }
            }
        }
    }

    private void getLocationPermission() {
        //Проверка разрешения на геолокацию
        locationPermissionGranted = false;
        if (ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Если НЕТ разрешения, то спрашиваем разрешение
            Log.d(TAG, "getLocationPermission: Запрос разрешения геолокации");
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }else {
            //Если есть разрешение, то ставим флаг в true
            Log.d(TAG, "getLocationPermission: Доступ к геолокации был предоставлен ранее");
            locationPermissionGranted = true;
            initMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Доступ к геолокации предоставлен");
                    locationPermissionGranted = true;
                }else {
                    Log.d(TAG, "onRequestPermissionsResult: Доступ к геолокации отклонен");
                    locationPermissionGranted = false;
                }
                break;
            }
        }
        initMap();
    }

    private void initMap(){
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            try {
                if (locationPermissionGranted) {
                    getDeviceLocation(googleMap);
                    Log.d(TAG, "OnMapReadyCallback: Ппоказ интерфейса геолокации");
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    Log.d(TAG, "OnMapReadyCallback: Сскрытие интерфейса геолокации");
                    googleMap.setMyLocationEnabled(false);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            } catch (SecurityException e)  {
                Log.d(TAG, Objects.requireNonNull(e.getMessage()));
            }

            LatLng sydney = new LatLng(-34, 151);
            googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        }
    };

    private void getDeviceLocation(final GoogleMap googleMap) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        try {
            Task <Location> location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "getDeviceLocation: Task получения геолокации выполнена успешно");
                        Location currentLocation = task.getResult();
                        if (currentLocation != null){
                            Log.d(TAG, "getDeviceLocation: Текущая геолокация определена");
                            LatLng latLng = new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                        }else {
                            Log.d(TAG, "getDeviceLocation: Текущая геолокация НЕ определена");
                            Toast.makeText(getContext(), "Текущая геолокация не определена", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Log.d(TAG, "getDeviceLocation: Task получения геолокации НЕ выполнена");
                        Toast.makeText(getContext(), "Не удалось получить геолокацию", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch(SecurityException e)  {
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }
}