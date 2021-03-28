package com.startup.eventsearcher.main.ui.events.filter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.startup.eventsearcher.databinding.ActivityFilterBinding;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.utils.dateTimeMaterialPicker.DateTimeMaterialPicker;
import com.startup.eventsearcher.utils.dateTimeMaterialPicker.IDateTimeMaterialPicker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FilterActivity extends AppCompatActivity implements IDateTimeMaterialPicker {

    private static final String TAG = "myFilter";

    private ActivityFilterBinding bind;

    private final ArrayList<Event> eventArrayList = new ArrayList<>();
    private final Set<String> arrayListCities = new HashSet<>();
    private Filter filter;
    private DateTimeMaterialPicker dateTimeMaterialPicker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityFilterBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        filter = FilterHandler.getFilterFromJSON(this);
        Log.d(TAG, "onCreate: filter = " + filter.toString());

        dateTimeMaterialPicker = new DateTimeMaterialPicker(this, getSupportFragmentManager());

        setCity();
        initCity();
        setCountMembers();
        setDate();

        componentListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        eventArrayList.clear();
        db.collection("Events").addSnapshotListener((value, error) -> {
            if (error != null){
                Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }else if (value != null) {
                for (QueryDocumentSnapshot documentSnapshot: value){
                    Event event = documentSnapshot.toObject(Event.class);
                    eventArrayList.add(event);
                }
            }
        });
    }

    private void setCity() {
        bind.eventListFiltersCitySpinner.setText(filter.getCity());
    }

    //Инициализация выпадающего списка с городами
    private void initCity() {
        for (Event event: eventArrayList){
            arrayListCities.add(event.getEventAddress().getCity());
        }
        bind.eventListFiltersCitySpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(arrayListCities)));
    }

    //Инициализация слайдера и показателей
    private void setCountMembers() {
        int endCount;
        if (filter.getEndCountMembers() == -1){
            endCount = 50;
            bind.eventListFiltersStartCountMembers.setText(String.valueOf(filter.getStartCountMembers()));
            bind.eventListFiltersEndCountMembers.setText("max");
        }else {
            endCount = filter.getEndCountMembers();
            bind.eventListFiltersStartCountMembers.setText(String.valueOf(filter.getStartCountMembers()));
            bind.eventListFiltersEndCountMembers.setText(String.valueOf(endCount));
        }
        bind.eventListFiltersRangeMembers.setValues(Arrays.asList((float) filter.getStartCountMembers(), (float) endCount));
    }

    private void setDate() {
        Objects.requireNonNull(bind.eventListFiltersStartDate.getEditText()).setText(filter.getDate());
    }

    //Сброс фильтра
    private void resetFilter() {
        filter.setCity("");
        filter.setStartCountMembers(0);
        filter.setEndCountMembers(-1);
        filter.setDate("");

        setCity();
        setCountMembers();
        setDate();
    }

    private void componentListener() {

        bind.eventListFiltersRangeMembers.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            filter.setStartCountMembers(Math.round(values.get(0)));
            filter.setEndCountMembers(Math.round(values.get(1)));

            if (filter.getEndCountMembers() == 50){
                filter.setEndCountMembers(-1);
                bind.eventListFiltersEndCountMembers.setText("max");
            }else {
                bind.eventListFiltersEndCountMembers.setText(String.valueOf(filter.getEndCountMembers()));
            }
            bind.eventListFiltersStartCountMembers.setText(String.valueOf(filter.getStartCountMembers()));
        });

        //Закрыть фильтр
        bind.eventListFiltersClose.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        bind.eventListFiltersReset.setOnClickListener(view -> resetFilter());

        //Применить фильтр
        bind.eventListFiltersApply.setOnClickListener(view -> {
            filter.setCity(bind.eventListFiltersCitySpinner.getText().toString());
            FilterHandler.saveFilterToJSON(this);
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });

        //Выбор даты
        Objects.requireNonNull(bind.eventListFiltersStartDate.getEditText())
                .setOnClickListener(view -> dateTimeMaterialPicker.getMaterialDatePicker());
    }

    @Override
    public void onGetDate(String date) {
        Objects.requireNonNull(bind.eventListFiltersStartDate.getEditText()).setText(date);
        filter.setDate(date);
    }

    @Override
    public void onGetTime(String time) {

    }
}