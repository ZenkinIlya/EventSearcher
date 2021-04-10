package com.startup.eventsearcher.utils.map;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.startup.eventsearcher.models.event.Event;
import com.startup.eventsearcher.utils.DateParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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

    //Создание MarkerOptions на основе даты проведения эвента
    private MarkerOptions getMarkerOptions(Date date, Date dateNow, LatLng latLng){
        Log.d(TAG, "getMarkerOptions: event.getDate() = " +date.toString());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        if (date.after(dateNow)){
            Log.i(TAG, "getMarkerOptions: Эвенту еще предстоит состояться");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }else if (date.after(DateParser.getDateWithMinusHours(dateNow, 2))){
            Log.i(TAG, "getMarkerOptions: Эвент начался в течении последних 2 часов");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }else if (date.after(DateParser.getDateWithMinusHours(dateNow, 12))){
            Log.i(TAG, "getMarkerOptions: Эвент начался в течении последних 10 - 12 часов");
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        }else {
            Log.i(TAG, "getMarkerOptions: Эвент давно прошел");
        }
        return markerOptions;
    }

    //Установка эвент-маркеров на карте
    public void setEventMarkerOnMap(GoogleMap map, ArrayList<Event> eventArrayList) {
        Date dateNow =new Date();
        map.clear();
        Log.d(TAG, "setEventMarkerOnMap: Установка эвент-маркеров на карте");
        for (Event event: eventArrayList){
            LatLng latLng = new LatLng(event.getEventAddress().getLatitude(),
                    event.getEventAddress().getLongitude());

            Marker marker = map.addMarker(getMarkerOptions(event.getDate(), dateNow, latLng));
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
