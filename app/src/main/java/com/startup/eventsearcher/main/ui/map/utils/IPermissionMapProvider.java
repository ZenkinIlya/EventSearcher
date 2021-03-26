package com.startup.eventsearcher.main.ui.map.utils;

import android.content.Intent;

public interface IPermissionMapProvider {
    void onEndGetPermissions();

    void onRequestLocationPermissions(String[] strings, int permissionsRequestAccessFineLocation);

    void onStartDeviceSettingsActivity(Intent enableGpsIntent, int permissionsRequestEnableGps);
}
