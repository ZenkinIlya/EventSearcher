package com.startup.eventsearcher.main.ui.subscribe;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.databinding.ActivitySubscribeBinding;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.EventsList;

import java.util.Calendar;
import java.util.Objects;

public class SubscribeActivity extends AppCompatActivity {

    private ActivitySubscribeBinding bind;

    private Event event;
    private Calendar calendar;
    private int lastSelectedHour = -1;
    private int lastSelectedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySubscribeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle("Подписаться");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        event = (Event) getIntent().getSerializableExtra("Event");
        calendar = Calendar.getInstance();

        componentsListener();
    }

    private void componentsListener() {

        bind.subscribeBtnSubscribe.setOnClickListener(view -> {
            Intent intent = new Intent();
            //Получаем индекс выбранного эвента в списке
            int indexOfEvent = EventsList.getEventArrayList().indexOf(event);

            //TODO Отправка запрооса на сервер о подписке

            intent.putExtra("index", indexOfEvent);
            intent.putExtra("time", Objects.requireNonNull(bind.subscribeStartTime.getEditText()).getText().toString());
            intent.putExtra("comment", Objects.requireNonNull(bind.subscribeComment.getEditText()).getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        });

        Objects.requireNonNull(bind.subscribeStartTime.getEditText()).setOnClickListener(view -> {
            if(lastSelectedHour == -1)  {
                lastSelectedHour = calendar.get(Calendar.HOUR_OF_DAY);
                lastSelectedMinute = calendar.get(Calendar.MINUTE);
            }

            TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
                String time = hourOfDay + ":" + minute;
                bind.subscribeStartTime.getEditText().setText(time);
                lastSelectedHour = hourOfDay;
                lastSelectedMinute = minute;
            };

            TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                    android.R.style.Theme_DeviceDefault_Dialog,
                    timeSetListener, lastSelectedHour, lastSelectedMinute, true);
            timePickerDialog.show();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //Отмена подписки
        if (id == android.R.id.home) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}