package com.startup.eventsearcher.main.ui.map;

import android.content.Context;
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
import com.startup.eventsearcher.main.ui.events.model.EventsList;
import com.startup.eventsearcher.main.ui.map.utils.MapHandler;
import com.startup.eventsearcher.utils.Config;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MapsFragment extends Fragment implements IMapHandler{

    private static final String TAG = "myMap";

    private FragmentMapsBinding bind;

    private GoogleMap map;
    private boolean flgFirstEnterToMapFragment = true;
    private MapHandler mapHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        bind = FragmentMapsBinding.inflate(inflater, container, false);

        bind.mapSearchView.setOnQueryTextListener(onQueryTextListenerSearchView);
        mapHandler = new MapHandler(this);

        //TODO Получаем с сервера список эвентов

        EventsList.getEventArrayListFromJSON(getContext());

        return bind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null){
            LatLng savedLatLng = savedInstanceState.getParcelable("latLng");
            float savedZoom = savedInstanceState.getFloat("zoom");
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(savedLatLng, savedZoom));
        }

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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState():");

        //TODO Не работает так как onSaveInstanceState вызывается несколько раз
        /*CameraPosition cameraPosition = map.getCameraPosition();
        outState.putParcelable("latLng", cameraPosition.target);
        outState.putFloat("zoom", cameraPosition.zoom);*/
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        if (checkMapServices()) {
            if (mapHandler.isLocationPermissionGranted()) {
                initMap();
            } else {
                mapHandler.getLocationPermission(getContext());
            }
        }
        Log.d(TAG, "onStart: end progressbar");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
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
        EventsList.saveEventArrayListInJSON(getContext());
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

    private boolean checkMapServices() {
        if (mapHandler.isServicesOK(getContext())) {
            if (mapHandler.isMapsEnabled(requireContext())) {
                return true;
            }else {
                mapHandler.buildAlertMessageNoGps(getContext());
            }
        }
        return false;
    }

    //Ответ после PERMISSIONS_REQUEST_ENABLE_GPS, CREATE_EVENT и при выходе из просмотра эвента
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + " resultCode=" + resultCode);
        switch (resultCode){
            case RESULT_OK: {
                switch (requestCode) {
                    case Config.PERMISSIONS_REQUEST_ENABLE_GPS: {
                        Log.d(TAG, "onActivityResult: PERMISSIONS_REQUEST_ENABLE_GPS");
                        if (mapHandler.isLocationPermissionGranted()) {
                            Log.d(TAG, "onActivityResult: Доступ к геолокации предоставлен");
                            initMap();
                        } else {
                            Log.d(TAG, "onActivityResult: Доступ к геолокации НЕ предоставлен");
                            mapHandler.getLocationPermission(getContext());
                        }
                        break;
                    }
                    case Config.CREATE_EVENT: {
                        //Получаем координаты и стаавим метку на карте
                        //Либо это можно не делать, так как при создании эвент будет грузиться на
                        //сервер и после перехода на карту будут подгружаться с сервера все эвенты
                        Log.d(TAG, "onActivityResult: CREATE_EVENT");
                        Event event = (Event) data.getSerializableExtra("Event");
                        Log.d(TAG, "onActivityResult: event = " + Objects.requireNonNull(event).toString());
                        break;
                    }
                }
                break;
            }
            case RESULT_CANCELED: {
                switch (requestCode) {
                    case Config.SHOW_EVENT: {
                        Log.d(TAG, "onActivityResult: EventList: " + EventsList.getEventArrayList().toString());
                        Log.d(TAG, "onActivityResult: Пользователь вышел из подробного просмотра эвента");
                        break;
                    }
                }
                break;
            }
        }
    }

    //Добавление эвента / вызывать только после инициализаци map в onMapReady
    private void addPlace() {
        //Нажатие на кнопку "Дбоавить эвент" - режим добавление эвента
        bind.mapFabShowMarker.setOnClickListener(view -> {
            if (bind.mapAddEventMarker.getVisibility() == View.INVISIBLE){
                bind.mapAddEventMarker.setVisibility(View.VISIBLE);
                bind.mapAddEvent.setVisibility(View.VISIBLE);
                bind.mapFabShowMarker.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_remove));
                bind.mapFabShowMarker.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(view.getContext(), R.color.warning)));
            }else{
                bind.mapAddEventMarker.setVisibility(View.INVISIBLE);
                bind.mapAddEvent.setVisibility(View.GONE);
                bind.mapFabShowMarker.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_add_location));
                bind.mapFabShowMarker.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(view.getContext(), R.color.primaryColor)));
            }
        });

        //Добавить эвент с координатой взятой с центра экрана
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
    }

    //Вызов после запроса доступа к геолокации
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Config.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Доступ к геолокации предоставлен");
                    mapHandler.setLocationPermissionGranted(true);
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Доступ к геолокации отклонен");
                    mapHandler.setLocationPermissionGranted(false);
                }
                break;
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

    //Переход в системные настройки для включения доступа локации
    @Override
    public void startActivity(Intent intent, int permissionsRequest) {
        Log.d(TAG, "startActivity: callback");
        startActivityForResult(intent, permissionsRequest);
    }

    //Запрос разрешения на геолокацию
    @Override
    public void requestPermissionsHandler(String[] strings, int permissionsRequest) {
        Log.d(TAG, "requestPermissionsHandler: callback");
        requestPermissions(strings, permissionsRequest);
    }

    @Override
    public void returnDeviceLocation(LatLng latLng) {
        Log.d(TAG, "returnDeviceLocation: callback");
        if (flgFirstEnterToMapFragment){
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.DEFAULT_ZOOM));
            flgFirstEnterToMapFragment = false;
        }
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            if (map != null){
                map.clear();
            }
            map = googleMap;

            map.setOnMarkerClickListener(callbackMarkerClickListener);
            //Инициализация добавления места
            addPlace();
            //Инициализация интерфейса (локация, зум)
            initUiMap();
            //Установка отступа для элементов GoogleMap
            map.setPadding(0, 90, 0, 0);
            //Загрузка эвентов из json
            EventsList.getEventArrayListFromJSON(getContext());
            //Расставляем все эвент-маркеры на карте
            mapHandler.setEventMarkerOnMap(map);
            try {
                if (mapHandler.isLocationPermissionGranted()) {
                    mapHandler.getDeviceLocation(getContext());
                    Log.d(TAG, "OnMapReadyCallback: Показ интерфейса геолокации");
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    bind.mapLocation.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "OnMapReadyCallback: Скрытие интерфейса геолокации");
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    bind.mapLocation.setVisibility(View.GONE);
                }
            } catch (SecurityException e) {
                Log.d(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
    };

    private void initUiMap() {
        bind.mapLocation.setOnClickListener(view -> {
            flgFirstEnterToMapFragment = true;  //при false локация устройства не будет показана
            mapHandler.getDeviceLocation(getContext());
        });

        bind.mapZoomIn.setOnClickListener(view -> map.animateCamera(CameraUpdateFactory.zoomIn()));

        bind.mapZoomOut.setOnClickListener(view -> map.animateCamera(CameraUpdateFactory.zoomOut()));
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