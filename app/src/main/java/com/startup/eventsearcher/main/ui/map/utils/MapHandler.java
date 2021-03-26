package com.startup.eventsearcher.main.ui.map.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.startup.eventsearcher.main.ui.events.model.Event;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapHandler {

    private static final String TAG = "tgMapHandler";

    private final IMapHandler iMapHandler;

    public MapHandler(IMapHandler iMapHandler) {
        this.iMapHandler = iMapHandler;
    }

    //Получение адреса метки пользователя
    public Address getAddress(Context context, LatLng latLng) throws IOException {
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
    public void setEventMarkerOnMap(GoogleMap map, ArrayList<Event> eventArrayList) {
        Log.d(TAG, "setEventMarkerOnMap: Установка эвент-маркеров на карте");
        Log.d(TAG, "setEventMarkerOnMap: eventArrayList = " + eventArrayList.toString());
        for (Event event: eventArrayList){
            LatLng latLng = new LatLng(event.getEventAddress().getLatitude(),
                    event.getEventAddress().getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            markerOptions.title(event.getHeader());
            Marker marker = map.addMarker(markerOptions);
            marker.setTag(event);
        }
    }

    //Получение геолокации устройства - callback необходим
    public void getDeviceLocation(Context context) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        try {
            Task<Location> location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, "getDeviceLocation: Task получения геолокации выполнена успешно");
                    Location currentLocation = task.getResult();
                    if (currentLocation != null) {
                        Log.d(TAG, "getDeviceLocation: Текущая геолокация определена");
                        LatLng latLng = new LatLng(currentLocation.getLatitude(),
                                currentLocation.getLongitude());
                        iMapHandler.returnDeviceLocation(latLng);
                    } else {
                        Log.d(TAG, "getDeviceLocation: Текущая геолокация НЕ определена");
                        Toast.makeText(context, "Текущая геолокация не определена", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d(TAG, "getDeviceLocation: Task получения геолокации НЕ выполнена");
                    Toast.makeText(context, "Не удалось получить геолокацию", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(e -> Log.d(TAG, "getDeviceLocation: addOnFailureListener: " + e.getMessage()));
        } catch (SecurityException e) {
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

}
