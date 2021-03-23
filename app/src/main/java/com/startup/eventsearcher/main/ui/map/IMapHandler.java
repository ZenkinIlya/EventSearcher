package com.startup.eventsearcher.main.ui.map;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;

public interface IMapHandler {
    void initMap();
    void startActivity(Intent intent, int permissionsRequest);
    void requestPermissionsHandler(String[] strings, int permissionsRequest);
    void returnDeviceLocation(LatLng latLng);
}
