package com.startup.eventsearcher.utils.map;

import android.content.Intent;

public interface IPermissionMapProvider {
    void onEndGetPermissions();

    void onRequestLocationPermissions(String[] strings, int permissionsRequestAccessFineLocation);

    void onStartDeviceSettingsActivity(Intent enableGpsIntent, int permissionsRequestEnableGps);
}
