package com.startup.eventsearcher.main.ui.events.createEvent;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.databinding.FragmentSetLocationEventBinding;
import com.startup.eventsearcher.main.ui.map.utils.IMapHandler;
import com.startup.eventsearcher.main.ui.map.utils.IPermissionMapProvider;
import com.startup.eventsearcher.main.ui.map.utils.MapHandler;
import com.startup.eventsearcher.main.ui.map.utils.PermissionMapProvider;
import com.startup.eventsearcher.utils.Config;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class SetLocationEventFragment extends DialogFragment implements IMapHandler, IPermissionMapProvider {

    private static final String TAG = "tgSetLocationEventFrag";

    private FragmentSetLocationEventBinding bind;

    private MapHandler mapHandler;
    private PermissionMapProvider permissionMapProvider;
    private static ISetLocationEvent iSetLocationEvent;

    private GoogleMap map;
    private Address address;

    public interface ISetLocationEvent {
        void returnAddress(Address address);
    }

    public SetLocationEventFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static SetLocationEventFragment newInstance(Address address) {
        SetLocationEventFragment fragment = new SetLocationEventFragment();
        Bundle args = new Bundle();
        args.putParcelable("address", address);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        iSetLocationEvent = (ISetLocationEvent) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentSetLocationEventBinding.inflate(inflater, container, false);
        return bind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mapHandler = new MapHandler(this);
        permissionMapProvider = new PermissionMapProvider(this, getContext());

        address = requireArguments().getParcelable("address");

        componentListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        permissionMapProvider.getPermissions();
    }

    /********************PermissionMapProviderCallbacks************************/
    @Override
    public void onEndGetPermissions() {
        Log.d(TAG, "onEndGetPermissions: callback");
        initMap();
    }

    @Override
    public void onRequestLocationPermissions(String[] strings, int permissionsRequestAccessFineLocation) {
        Log.d(TAG, "onRequestLocationPermissions: callback");
        requestPermissions(strings, permissionsRequestAccessFineLocation);
    }

    @Override
    public void onStartDeviceSettingsActivity(Intent enableGpsIntent, int permissionsRequestEnableGps) {
        Log.d(TAG, "onStartDeviceSettingsActivity: callback");
        startActivityForResult(enableGpsIntent, permissionsRequestEnableGps);
    }
    /**************************************************************************/

    //Ответ после PERMISSIONS_REQUEST_ENABLE_GPS
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Config.PERMISSIONS_REQUEST_ENABLE_GPS) {
                Log.d(TAG, "onActivityResult: Выход из настроек (включение геолокации на устройстве)");
                permissionMapProvider.checkLocationPermission();
            }
        }
    }

    //Вызов после запроса доступа к геолокации
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Config.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: (PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) Доступ к геолокации предоставлен");
                permissionMapProvider.setLocationPermissionGranted(true);
            } else {
                Log.d(TAG, "onRequestPermissionsResult: (PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) Доступ к геолокации отклонен");
                permissionMapProvider.setLocationPermissionGranted(false);
            }
        }
        initMap();
    }

    public void initMap() {
        Log.d(TAG, "initMap: ");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_set_location_event_map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(callback);
        }
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;

            //Создание эвента без предварительного выбора адреса
            if (address == null){
                if (permissionMapProvider.isLocationPermissionGranted()){
                    //Получаем геолокацию пользователя
                    mapHandler.getDeviceLocation(getContext());
                }
            }else {
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                map.addMarker(markerOptions);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.DEFAULT_ZOOM));
            }
        }
    };

    private void componentListener() {

        bind.fragmentSetLocationApply.setOnClickListener(view -> {
            LatLng target = map.getCameraPosition().target;
            try {
                Address address = mapHandler.getAddress(getContext(), target);
                //Возврат адресса в вызвавшую активность/фрагмент
                iSetLocationEvent.returnAddress(address);
            } catch (IOException e) {
                Toast.makeText(getContext(), "Не удалось получить адрес геопозиции. " +
                        "Ошибка: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            dismiss();
        });
    }

    @Override
    public void returnDeviceLocation(LatLng latLng) {
        Log.d(TAG, "returnDeviceLocation: callback");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.DEFAULT_ZOOM));
    }
}
