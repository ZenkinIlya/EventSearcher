package com.startup.eventsearcher.main.ui.map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.event.EventActivity;
import com.startup.eventsearcher.main.ui.events.model.EventsList;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.map.createEvent.EventCreatorActivity;
import com.startup.eventsearcher.utils.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;

public class MapsFragment extends Fragment {

    private static final String TAG = "myMap";

    public static final int ERROR_DIALOG_REQUEST = 9001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9002;
    private static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9003;
    private static final int CREATE_EVENT = 1;

    private boolean locationPermissionGranted = false;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private SupportMapFragment mapFragment;
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
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        super.onResume();
        if (checkMapServices()) {
            if (locationPermissionGranted) {
                initMap();
            } else {
                getLocationPermission();
            }
        }
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
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: Проверка доступны ли google services устройству");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if (available == ConnectionResult.SUCCESS) {
            Log.d(TAG, "isServicesOK: Устройство имеет доступ к google services");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: Обнаружена ошибка, но пользователь может решить её");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Log.d(TAG, "isServicesOK: Ваше устройтсво НЕ может работать с Google Maps");
            Toast.makeText(getContext(), "Ваше устройтсво не может работать с Google Maps", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    //Включена ли на устройстве геолокация
    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.d(TAG, "isMapsEnabled: На устройстве НЕ включена геолокация");
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    //
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Для работы приложения необоходимо включить геолокацию, включить?")
                .setNegativeButton("Нет, спасибо", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //Отказ о предоставлении приложению доступа к геолокации
                    }
                })
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + " resultCode=" + resultCode);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PERMISSIONS_REQUEST_ENABLE_GPS: {
                    Log.d(TAG, "onActivityResult: PERMISSIONS_REQUEST_ENABLE_GPS");
                    if (locationPermissionGranted) {
                        Log.d(TAG, "onActivityResult: Доступ к геолокации предоставлен");
                        initMap();
                    } else {
                        Log.d(TAG, "onActivityResult: Доступ к геолокации НЕ предоставлен");
                        getLocationPermission();
                    }
                }
                case CREATE_EVENT: {
                    //Получаю координаты и стаавлю метку на карте
                    //Либо это можно не делать, так как при создании эвент будет грузиться на
                    //сервер и после перехода на карту будут подгружаться с сервера все эвенты
                    //хотя это не точно
                    Log.d(TAG, "onActivityResult: CREATE_EVENT");
                    Event event = (Event) data.getSerializableExtra("Event");
                    Log.d(TAG, "buttonAccept: event = " + Objects.requireNonNull(event).toString());
                }
            }
        }
    }

    private void getAddress(LatLng latLng){
        Geocoder geocoder;
        List<Address> addresses;
        Address addressObject;
        geocoder = new Geocoder(requireContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            addressObject = addresses.get(0);

            //в addressObject лежит ближайшая координата к области, отмеченной пользователем
            //Нам нужна та координата которую поставил непосредсвенно пользователь
            addressObject.setLatitude(latLng.latitude);
            addressObject.setLongitude(latLng.longitude);

            Intent intent = new Intent(getContext(), EventCreatorActivity.class);
            intent.putExtra("Address", addressObject);
            startActivityForResult(intent, CREATE_EVENT);

        } catch (IOException e) {
            Toast.makeText(getContext(), "Ошибка: " +e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
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
                getAddress(target);
            }
        });
    }

    private void getLocationPermission() {
        //Проверка разрешения на геолокацию
        locationPermissionGranted = false;
        if (ActivityCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Если НЕТ разрешения, то спрашиваем разрешение
            Log.d(TAG, "getLocationPermission: Запрос разрешения геолокации");
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            //Если есть разрешение, то ставим флаг в true
            Log.d(TAG, "getLocationPermission: Доступ к геолокации был предоставлен ранее");
            locationPermissionGranted = true;
            initMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Доступ к геолокации предоставлен");
                    locationPermissionGranted = true;
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Доступ к геолокации отклонен");
                    locationPermissionGranted = false;
                }
                break;
            }
        }
        initMap();
    }

    private void initMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            map.setOnMarkerClickListener(callbackMarkerClickListener);
            //Инициализация добавления места
            addPlace();
            //Инициализация интерфейса (локация, зум)
            initUiMap();
            //Установка отступа для элементов GoogleMap
            map.setPadding(0, 90, 0, 0);
            //Расставляем все эвент-маркеры на карте
            setEventMarkerOnMap();
            try {
                if (locationPermissionGranted) {
                    getDeviceLocation();
                    Log.d(TAG, "OnMapReadyCallback: Ппоказ интерфейса геолокации");
                    map.setMyLocationEnabled(true);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    fabLocation.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "OnMapReadyCallback: Сскрытие интерфейса геолокации");
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                    fabLocation.setVisibility(View.GONE);
                }
            } catch (SecurityException e) {
                Log.d(TAG, Objects.requireNonNull(e.getMessage()));
            }

            LatLng sydney = new LatLng(-34, 151);
            map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        }
    };

    private void initUiMap() {
        fabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flgFirstEnterToMapFragment = true;
                getDeviceLocation();
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

    //Установка эвент-маркеров на карте
    private void setEventMarkerOnMap() {
        ArrayList<Event> eventArrayList = EventsList.getEventArrayList();
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

    private void getDeviceLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        try {
            Task<Location> location = fusedLocationProviderClient.getLastLocation();
            location.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "getDeviceLocation: Task получения геолокации выполнена успешно");
                        Location currentLocation = task.getResult();
                        if (currentLocation != null) {
                            Log.d(TAG, "getDeviceLocation: Текущая геолокация определена");
                            LatLng latLng = new LatLng(currentLocation.getLatitude(),
                                    currentLocation.getLongitude());
                            if (flgFirstEnterToMapFragment){
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.DEFAULT_ZOOM));
                                flgFirstEnterToMapFragment = false;
                            }
                        } else {
                            Log.d(TAG, "getDeviceLocation: Текущая геолокация НЕ определена");
                            Toast.makeText(getContext(), "Текущая геолокация не определена", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.d(TAG, "getDeviceLocation: Task получения геолокации НЕ выполнена");
                        Toast.makeText(getContext(), "Не удалось получить геолокацию", Toast.LENGTH_LONG).show();
                    }
                }
            }).addOnFailureListener(e -> Log.d(TAG, "getDeviceLocation: addOnFailureListener: " + e.getMessage()));
        } catch (SecurityException e) {
            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
        }
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
                        //Получаем список адрессов (1 адресс)
                        addressList = geocoder.getFromLocationName(location, 1);
                        Log.d(TAG, "onQueryTextSubmit: addressList = " + addressList);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        map.addMarker(new MarkerOptions().position(latLng).title(address.getSubLocality()));
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

            Intent intent = new Intent(getContext(), EventActivity.class);
            intent.putExtra("Event", event);
            startActivity(intent);

            // Return false to indicate that we have not consumed the event and that we wish
            // for the default behavior to occur (which is for the camera to move such that the
            // marker is centered and for the marker's info window to open, if it has one).
            return false;
        }
    };
}