package com.startup.eventsearcher.main.ui.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
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
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated()");
        getLocationPermission();
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

    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }

    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Доступ к геолокации предоставлен");
                    locationPermissionGranted = true;
                }else {
                    Log.d(TAG, "Доступ к геолокации отклонен");
                    locationPermissionGranted = false;
                }
                break;
            }
        }
        initMap();
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            try {
                if (locationPermissionGranted) {
                    getDeviceLocation(googleMap);
                    Log.d(TAG, "Ппоказ интерфейса геолокации");
                    googleMap.setMyLocationEnabled(true);
                    googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    Log.d(TAG, "Сскрытие интерфейса геолокации");
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

    //Запуск карты
    private void initMap(){
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void getLocationPermission() {
        //Проверка разрешения на геолокацию
        locationPermissionGranted = false;
        if (ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Если НЕТ разрешения, то спрашиваем разрешение
            Log.d(TAG, "Запрос разрешения геолокации");
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }else {
            //Если есть разрешение, то ставим флаг в true
            Log.d(TAG, "Доступ к геолокации был предоставлен ранее");
            locationPermissionGranted = true;
            initMap();
        }
    }

    private void getDeviceLocation(final GoogleMap googleMap) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        try {
            Task <Location> location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()){
                        Log.d(TAG, "Task получения геолокации выполнена успешно");
                        Location currentLocation = task.getResult();
                        if (currentLocation != null){
                            Log.d(TAG, "Текущая геолокация определена");
                            LatLng latLng = new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM));
                        }else {
                            Log.d(TAG, "Текущая геолокация НЕ определена");
                            Toast.makeText(getContext(), "Геолокация не определена, возможно " +
                                    "отключена локация на устройстве", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Log.d(TAG, "Task получения геолокации НЕ выполнена");
                        Toast.makeText(getContext(), "Не удалось получить геолокацию", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch(SecurityException e)  {
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }
}