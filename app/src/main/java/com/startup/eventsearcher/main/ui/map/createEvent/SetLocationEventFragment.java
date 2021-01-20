package com.startup.eventsearcher.main.ui.map.createEvent;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.startup.eventsearcher.main.ui.map.utils.MapHandler;
import com.startup.eventsearcher.utils.Config;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SetLocationEventFragment extends DialogFragment implements OnMapReadyCallback, MapHandler.Callback {

    private static final String TAG = "SetLocationEvent";

    interface Callback{
        void returnAddress(Address address);
    }

    static SetLocationEventFragment.Callback callback;

    public static void registerSetLocationEventFragmentCallback(Callback callback){
        SetLocationEventFragment.callback = callback;
    }

    private GoogleMap map;
    private Address address;

    @BindView(R.id.fragment_set_location_apply)
    Button buttonApply;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_set_location_event, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        MapHandler.registerMapHandlerCallBack(this);

        address = requireArguments().getParcelable("address");
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_set_location_event_map);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }

        componentListener();

    }

    private void componentListener() {

        buttonApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng target = map.getCameraPosition().target;
                try {
                    Address address = MapHandler.getAddress(getContext(), target);
                    callback.returnAddress(address);
                } catch (IOException e) {
                    Toast.makeText(getContext(), "Не удалось получить адрес геопозиции. " +
                            "Ошибка: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                dismiss();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        //Создание эвента без предварительного выбора адреса
        if (address == null){
            //Получаем геолокацию пользователя
            MapHandler.getDeviceLocation(getContext());
        }else {
            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            map.addMarker(markerOptions);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.DEFAULT_ZOOM));
        }

    }

    @Override
    public void initMap() {

    }

    @Override
    public void startActivity(Intent intent, int permissionsRequest) {

    }

    @Override
    public void requestPermissionsHandler(String[] strings, int permissionsRequest) {

    }

    @Override
    public void returnDeviceLocation(LatLng latLng) {
        Log.d(TAG, "returnDeviceLocation: callback");
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.DEFAULT_ZOOM));
    }
}
