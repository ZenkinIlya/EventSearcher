package com.startup.eventsearcher.main.ui.events.filter;

import android.app.DatePickerDialog;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FilterActivity extends AppCompatActivity {

    private static final String TAG = "myFilter";

    private ActivityFilterBinding bind;

    private ArrayList<Event> eventArrayList = new ArrayList<>();
    private final Set<String> arrayListCities = new HashSet<>();
    private Filter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityFilterBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        filter = FilterHandler.getFilterFromJSON(this);
        Log.d(TAG, "onCreate: filter = " + filter.toString());

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
        String date;
        if (filter.getLastSelectedDayOfMonth() == 0 && filter.getLastSelectedMonth() == 0 &&
                filter.getLastSelectedYear() == 0){
            date = "";
        }else {
            date = filter.getLastSelectedDayOfMonth() + "." +
                    (filter.getLastSelectedMonth() + 1) +"." +
                    filter.getLastSelectedYear();
        }
        Objects.requireNonNull(bind.eventListFiltersStartDate.getEditText()).setText(date);
    }

    //Сброс фильтра
    private void resetFilter() {
        filter.setCity("");
        filter.setStartCountMembers(0);
        filter.setEndCountMembers(-1);
        filter.setLastSelectedDayOfMonth(0);
        filter.setLastSelectedMonth(0);
        filter.setLastSelectedYear(0);

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
        Objects.requireNonNull(bind.eventListFiltersStartDate.getEditText()).setOnClickListener(view -> {

            DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, monthOfYear, dayOfMonth) -> {
                String date = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                bind.eventListFiltersStartDate.getEditText().setText(date);
                filter.setLastSelectedYear(year);
                filter.setLastSelectedMonth(monthOfYear);
                filter.setLastSelectedDayOfMonth(dayOfMonth);
            };

            int year = filter.getLastSelectedYear();
            int month = filter.getLastSelectedMonth();
            int dayOfMonth = filter.getLastSelectedDayOfMonth();
            if (bind.eventListFiltersStartDate.getEditText().getText().toString().equals("")){
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            }
            DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                    android.R.style.Theme_DeviceDefault_Dialog,
                    dateSetListener, year, month, dayOfMonth);
            datePickerDialog.show();
        });
    }
}