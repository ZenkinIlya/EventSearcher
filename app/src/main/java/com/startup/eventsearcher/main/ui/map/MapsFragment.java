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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.event.EventActivity;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.EventsList;
import com.startup.eventsearcher.main.ui.map.createEvent.EventCreatorActivity;
import com.startup.eventsearcher.main.ui.map.utils.MapHandler;
import com.startup.eventsearcher.utils.Config;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class MapsFragment extends Fragment implements MapHandler.Callback{

    private static final String TAG = "myMap";

    private GoogleMap map;
    private boolean flgFirstEnterToMapFragment = true;

    @BindView(R.id.map_search_view)
    SearchView searchView;
    @BindView(R.id.map_fab_show_marker)
    FloatingActionButton fabAddEvent;
    @BindView(R.id.map_add_event)
    Button btnAddEvent;
    @BindView(R.id.map_add_event_marker)
    ImageView imageViewAddEventMarker;
    @BindView(R.id.map_zoom_in)
    FloatingActionButton fabZoomIn;
    @BindView(R.id.map_zoom_out)
    FloatingActionButton fabZoomOut;
    @BindView(R.id.map_location)
    FloatingActionButton fabLocation;

    public static MapsFragment newInstance(String param1, String param2) {
        Log.d(TAG, "newInstance:");
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView()");
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, view);
        searchView.setOnQueryTextListener(onQueryTextListenerSearchView);
        MapHandler.registerMapHandlerCallBack(this);

        //TODO Получаем с сервера список эвентов

        EventsList.getEventArrayListFromJSON(getContext());

        return view;
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState():");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        if (checkMapServices()) {
            if (MapHandler.isLocationPermissionGranted()) {
                initMap();
            } else {
                MapHandler.getLocationPermission(getContext());
            }
        }
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
        if (MapHandler.isServicesOK(getContext())) {
            if (MapHandler.isMapsEnabled(requireContext())) {
                return true;
            }else {
                MapHandler.buildAlertMessageNoGps(getContext());
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + " resultCode=" + resultCode);
        switch (resultCode){
            case RESULT_OK: {
                switch (requestCode) {
                    case Config.PERMISSIONS_REQUEST_ENABLE_GPS: {
                        Log.d(TAG, "onActivityResult: PERMISSIONS_REQUEST_ENABLE_GPS");
                        if (MapHandler.isLocationPermissionGranted()) {
                            Log.d(TAG, "onActivityResult: Доступ к геолокации предоставлен");
                            initMap();
                        } else {
                            Log.d(TAG, "onActivityResult: Доступ к геолокации НЕ предоставлен");
                            MapHandler.getLocationPermission(getContext());
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
        fabAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageViewAddEventMarker.getVisibility() == View.INVISIBLE){
                    imageViewAddEventMarker.setVisibility(View.VISIBLE);
                    btnAddEvent.setVisibility(View.VISIBLE);
                    fabAddEvent.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_remove));
                    fabAddEvent.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(view.getContext(), R.color.warning)));
                }else{
                    imageViewAddEventMarker.setVisibility(View.INVISIBLE);
                    btnAddEvent.setVisibility(View.GONE);
                    fabAddEvent.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_add_location));
                    fabAddEvent.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(view.getContext(), R.color.primaryColor)));
                }
            }
        });

        btnAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng target = map.getCameraPosition().target;
                try {
                    Address address = MapHandler.getAddress(getContext(), target);
                    Intent intent = new Intent(getContext(), EventCreatorActivity.class);
                    intent.putExtra("Address", address);
                    startActivityForResult(intent, Config.CREATE_EVENT);
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Не удалось получить адрес геопозиции. " +
                            "Ошибка: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Config.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Доступ к геолокации предоставлен");
                    MapHandler.setLocationPermissionGranted(true);
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Доступ к геолокации отклонен");
                    MapHandler.setLocationPermissionGranted(false);
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

    @Override
    public void startActivity(Intent intent, int permissionsRequest) {
        Log.d(TAG, "startActivity: callback");
        startActivityForResult(intent, permissionsRequest);
    }

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


    private OnMapReadyCallback callback = new OnMapReadyCallback() {
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
            MapHandler.setEventMarkerOnMap(map);
            try {
                if (MapHandler.isLocationPermissionGranted()) {
                    MapHandler.getDeviceLocation(getContext());
                    Log.d(TAG, "OnMapReadyCallback: Показ интерфейса геолокации");
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    fabLocation.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "OnMapReadyCallback: Скрытие интерфейса геолокации");
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    fabLocation.setVisibility(View.GONE);
                }
            } catch (SecurityException e) {
                Log.d(TAG, Objects.requireNonNull(e.getMessage()));
            }
        }
    };

    private void initUiMap() {
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flgFirstEnterToMapFragment = true;  //при false локация устройства не будет показана
                MapHandler.getDeviceLocation(getContext());
            }
        });

        fabZoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        fabZoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
    }

    //Поиск
    private androidx.appcompat.widget.SearchView.OnQueryTextListener onQueryTextListenerSearchView =
            new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    String location = searchView.getQuery().toString();
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

    private GoogleMap.OnMarkerClickListener callbackMarkerClickListener = new GoogleMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {
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
        }
    };
}