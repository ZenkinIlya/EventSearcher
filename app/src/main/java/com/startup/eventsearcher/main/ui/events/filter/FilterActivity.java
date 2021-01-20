package com.startup.eventsearcher.main.ui.events.filter;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.textfield.TextInputLayout;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.EventsList;
import com.startup.eventsearcher.main.ui.map.utils.MapHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterActivity extends AppCompatActivity {

    private static final String TAG = "myFilter";

    @BindView(R.id.event_list_filters_city_spinner)
    AutoCompleteTextView autoCompleteTextViewCitySpinner;
    @BindView(R.id.event_list_filters_start_count_members)
    TextView textViewStartCountMembers;
    @BindView(R.id.event_list_filters_end_count_members)
    TextView textViewEndCountMembers;
    @BindView(R.id.event_list_filters_range_members)
    RangeSlider rangeSliderMembers;
    @BindView(R.id.event_list_filters_start_date)
    TextInputLayout textInputLayoutStartDate;
    @BindView(R.id.event_list_filters_reset)
    AppCompatButton buttonReset;
    @BindView(R.id.event_list_filters_apply)
    AppCompatButton buttonApply;
    @BindView(R.id.event_list_filters_close)
    ImageButton imageButtonClose;

    private Set<String> arrayListCities = new HashSet<>();
    private Filter filter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);

        filter = FilterHandler.getFilterFromJSON(this);
        Log.d(TAG, "onCreate: filter = " + filter.toString());

        setCity();
        initCity();
        setCountMembers();
        setDate();

        componentListener();
    }

    private void setCity() {
        autoCompleteTextViewCitySpinner.setText(filter.getCity());
    }

    //Инициализация выпадающего списка с городами
    private void initCity() {
        for (Event event: EventsList.getEventArrayList()){
            LatLng latLng = new LatLng(event.getEventAddress().getLatitude(), event.getEventAddress().getLongitude());
            try {
                Address address = MapHandler.getAddress(this, latLng);
                arrayListCities.add(address.getLocality());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        autoCompleteTextViewCitySpinner.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(arrayListCities)));
    }

    //Инициализация слайдера и показателей
    private void setCountMembers() {
        int endCount;
        if (filter.getEndCountMembers() == -1){
            endCount = 50;
            textViewStartCountMembers.setText(String.valueOf(filter.getStartCountMembers()));
            textViewEndCountMembers.setText("max");
        }else {
            endCount = filter.getEndCountMembers();
            textViewStartCountMembers.setText(String.valueOf(filter.getStartCountMembers()));
            textViewEndCountMembers.setText(String.valueOf(endCount));
        }
        rangeSliderMembers.setValues(List.of((float) filter.getStartCountMembers(), (float) endCount));
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
        Objects.requireNonNull(textInputLayoutStartDate.getEditText()).setText(date);
    }

    //Сброс фильтра
    private void resetFilter() {
        filter.setCity("");
        filter.setStartCountMembers(1);
        filter.setEndCountMembers(-1);
        filter.setLastSelectedDayOfMonth(0);
        filter.setLastSelectedMonth(0);
        filter.setLastSelectedYear(0);

        setCity();
        setCountMembers();
        setDate();
    }

    private void componentListener() {

        rangeSliderMembers.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            filter.setStartCountMembers(Math.round(values.get(0)));
            filter.setEndCountMembers(Math.round(values.get(1)));

            if (filter.getEndCountMembers() == 50){
                filter.setEndCountMembers(-1);
                textViewEndCountMembers.setText("max");
            }else {
                textViewEndCountMembers.setText(String.valueOf(filter.getEndCountMembers()));
            }
            textViewStartCountMembers.setText(String.valueOf(filter.getStartCountMembers()));
        });

        //Закрыть фильтр
        imageButtonClose.setOnClickListener(view -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        buttonReset.setOnClickListener(view -> {
            resetFilter();
        });

        //Применить фильтр
        buttonApply.setOnClickListener(view -> {
            filter.setCity(autoCompleteTextViewCitySpinner.getText().toString());
            FilterHandler.saveFilterToJSON(this);

            Intent intent = new Intent();
            intent.putExtra("city", autoCompleteTextViewCitySpinner.getText().toString());
            intent.putExtra("startCountMembers", filter.getStartCountMembers());
            intent.putExtra("endCountMembers", filter.getEndCountMembers());
            intent.putExtra("date", Objects.requireNonNull(textInputLayoutStartDate.getEditText()).getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        });

        //Выбор даты
        Objects.requireNonNull(textInputLayoutStartDate.getEditText()).setOnClickListener(view -> {

            DatePickerDialog.OnDateSetListener dateSetListener = (view1, year, monthOfYear, dayOfMonth) -> {
                String date = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                textInputLayoutStartDate.getEditText().setText(date);
                filter.setLastSelectedYear(year);
                filter.setLastSelectedMonth(monthOfYear);
                filter.setLastSelectedDayOfMonth(dayOfMonth);
            };

            DatePickerDialog datePickerDialog = null;
            int year = filter.getLastSelectedYear();
            int month = filter.getLastSelectedMonth();
            int dayOfMonth = filter.getLastSelectedDayOfMonth();
            if (textInputLayoutStartDate.getEditText().getText().toString().equals("")){
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            }
            datePickerDialog = new DatePickerDialog(view.getContext(),
                    android.R.style.Theme_DeviceDefault_Dialog,
                    dateSetListener, year, month, dayOfMonth);
            datePickerDialog.show();
        });
    }
}