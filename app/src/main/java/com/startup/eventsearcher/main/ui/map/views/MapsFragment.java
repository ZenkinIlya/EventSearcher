package com.startup.eventsearcher.main.ui.map.views;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.databinding.FragmentMapsBinding;
import com.startup.eventsearcher.main.ui.events.createEvent.EventCreatorActivity;
import com.startup.eventsearcher.main.ui.events.event.EventActivity;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.map.presenters.EventFireStorePresenter;
import com.startup.eventsearcher.main.ui.map.utils.IMapHandler;
import com.startup.eventsearcher.main.ui.map.utils.IPermissionMapProvider;
import com.startup.eventsearcher.main.ui.map.utils.MapHandler;
import com.startup.eventsearcher.main.ui.map.utils.PermissionMapProvider;
import com.startup.eventsearcher.utils.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MapsFragment extends Fragment implements IMapHandler, IPermissionMapProvider, IFireStoreView {

    private static final String TAG = "tgMapsFragment";

    private FragmentMapsBinding bind;

    private MapHandler mapHandler;
    private PermissionMapProvider permissionMapProvider;
    private EventFireStorePresenter eventFireStorePresenter;

    private GoogleMap map;
    private boolean flgFirstEnterToMapFragment = true;
    private final ArrayList<Event> eventArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        bind = FragmentMapsBinding.inflate(inflater, container, false);

        bind.mapSearchView.setOnQueryTextListener(onQueryTextListenerSearchView);
        mapHandler = new MapHandler(this);
        permissionMapProvider = new PermissionMapProvider(this, getContext());

        eventFireStorePresenter = new EventFireStorePresenter(this);

        return bind.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

        permissionMapProvider.getPermissions();
        eventFireStorePresenter.startEventsListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        eventFireStorePresenter.endEventsListener();
    }

    @Override
    public void onGetEvents(ArrayList<Event> eventArrayList) {
        this.eventArrayList.clear();
        this.eventArrayList.addAll(eventArrayList);
        if (map != null){
            mapHandler.setEventMarkerOnMap(map, eventArrayList);
        }
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


    //Ответ после PERMISSIONS_REQUEST_ENABLE_GPS, CREATE_EVENT и при выходе из просмотра эвента
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK: {
                switch (requestCode) {
                    case Config.PERMISSIONS_REQUEST_ENABLE_GPS: {
                        Log.d(TAG, "onActivityResult: Выход из настроек (включение геолокации на устройстве)");
                        permissionMapProvider.checkLocationPermission();
                        break;
                    }
                    case Config.CREATE_EVENT: {
                        Log.d(TAG, "onActivityResult: (RESULT_OK, CREATE_EVENT) Эвент создан");
                        setBtnAddEventInvisible();
                        break;
                    }
                }
                break;
            }
            case RESULT_CANCELED: {
                if (requestCode == Config.SHOW_EVENT) {
                    Log.d(TAG, "onActivityResult: (RESULT_CANCELED, SHOW_EVENT) Пользователь вышел из подробного просмотра эвента");
                }
                break;
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
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (map != null){
                map.clear();
            }
            map = googleMap;

            //Прослушка нажатия на маркер эвента
            map.setOnMarkerClickListener(callbackMarkerClickListener);
            //Инициализация добавления места, определение локации, зум
            initializeListeners();
            //Расставляем все эвент-маркеры на карте
            mapHandler.setEventMarkerOnMap(map, eventArrayList);
            //Установка отступа для элементов GoogleMap
            map.setPadding(0, 90, 0, 0);
            try {
                if (permissionMapProvider.isLocationPermissionGranted()) {
                    mapHandler.getDeviceLocation(getContext());
                    Log.d(TAG, "initMap: Показ интерфейса геолокации");
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    bind.mapLocation.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "initMap: Скрытие интерфейса геолокации");
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    bind.mapLocation.setVisibility(View.GONE);
                }
            } catch (SecurityException e) {
                Log.d(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
    };

    private void setBtnAddEventInvisible(){
        bind.mapAddEventMarker.setVisibility(View.INVISIBLE);
        bind.mapAddEvent.setVisibility(View.GONE);
        bind.mapFabShowMarker.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_add_location));
        bind.mapFabShowMarker.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(getContext(), R.color.primaryColor)));
    }

    private void initializeListeners() {
        //Нажатие на кнопку "Добавить эвент" - режим добавление эвента
        bind.mapFabShowMarker.setOnClickListener(view -> {
            if (bind.mapAddEventMarker.getVisibility() == View.INVISIBLE){
                bind.mapAddEventMarker.setVisibility(View.VISIBLE);
                bind.mapAddEvent.setVisibility(View.VISIBLE);
                bind.mapFabShowMarker.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_remove));
                bind.mapFabShowMarker.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(getContext(), R.color.warning)));
            }else{
                setBtnAddEventInvisible();
            }
        });

        //Добавить эвент с координатой, взятой с центра экрана
        bind.mapAddEvent.setOnClickListener(view -> {
            LatLng target = map.getCameraPosition().target;
            try {
                Address address = mapHandler.getAddress(getContext(), target);
                Intent intent = new Intent(getContext(), EventCreatorActivity.class);
                intent.putExtra("Address", address);
                startActivityForResult(intent, Config.CREATE_EVENT);
            } catch (IOException e) {
                Toast.makeText(getContext(), "Не удалось получить адрес геопозиции. " +
                        "Ошибка: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        bind.mapLocation.setOnClickListener(view -> {
            flgFirstEnterToMapFragment = true;  //при false локация устройства не будет показана
            mapHandler.getDeviceLocation(getContext());
        });

        bind.mapZoomIn.setOnClickListener(view -> map.animateCamera(CameraUpdateFactory.zoomIn()));

        bind.mapZoomOut.setOnClickListener(view -> map.animateCamera(CameraUpdateFactory.zoomOut()));
    }

    @Override
    public void returnDeviceLocation(LatLng latLng) {
        Log.d(TAG, "returnDeviceLocation: callback");
        if (flgFirstEnterToMapFragment){
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.DEFAULT_ZOOM));
            flgFirstEnterToMapFragment = false;
        }
    }

    //Поиск
    private final androidx.appcompat.widget.SearchView.OnQueryTextListener onQueryTextListenerSearchView =
            new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String location = bind.mapSearchView.getQuery().toString();
                    Log.d(TAG, "onQueryTextSubmit: location = " + location);
                    List<Address> addressList = null;

                    Geocoder geocoder = new Geocoder(getContext());
                    try {
                        //Получаем список адресов (1 адрес)
                        addressList = geocoder.getFromLocationName(location, 1);
                        Log.d(TAG, "onQueryTextSubmit: addressList = " + addressList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.DEFAULT_ZOOM));
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            };

    private final GoogleMap.OnMarkerClickListener callbackMarkerClickListener = marker -> {
        //Получаем тег маркера, т.е. получаем эвент который привязан к маркеру
        Event event = (Event) marker.getTag();

        Log.d(TAG, "onMarkerClick: event = " + Objects.requireNonNull(event).toString());

        Intent intent = new Intent(getContext(), EventActivity.class);
        intent.putExtra("Event", event);
        startActivityForResult(intent, Config.SHOW_EVENT);

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    };
}