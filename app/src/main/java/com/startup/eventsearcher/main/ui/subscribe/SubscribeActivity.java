package com.startup.eventsearcher.main.ui.subscribe;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.EventsList;

import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubscribeActivity extends AppCompatActivity {

    @BindView(R.id.subscribe_start_time)
    TextInputLayout textFieldTime;
    @BindView(R.id.subscribe_comment)
    TextInputLayout textFieldComment;
    @BindView(R.id.subscribe_subscribe)
    Button buttonSubscribe;

    private Event event;
    private Calendar calendar;
    private int lastSelectedHour = -1;
    private int lastSelectedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        ButterKnife.bind(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Подписаться");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        event = (Event) getIntent().getSerializableExtra("Event");
        calendar = Calendar.getInstance();

        componentsListener();
    }

    private void componentsListener() {

        buttonSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                //Получаем индекс выбранного эвента в списке
                int indexOfEvent = EventsList.getEventArrayList().indexOf(event);

                //TODO Отправка запрооса на сервер о подписке

                intent.putExtra("index", indexOfEvent);
                intent.putExtra("time", Objects.requireNonNull(textFieldTime.getEditText()).getText().toString());
                intent.putExtra("comment", Objects.requireNonNull(textFieldComment.getEditText()).getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        Objects.requireNonNull(textFieldTime.getEditText()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lastSelectedHour == -1)  {
                    lastSelectedHour = calendar.get(Calendar.HOUR_OF_DAY);
                    lastSelectedMinute = calendar.get(Calendar.MINUTE);
                }

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String time = hourOfDay + ":" + minute;
                        textFieldTime.getEditText().setText(time);
                        lastSelectedHour = hourOfDay;
                        lastSelectedMinute = minute;
                    }
                };

                TimePickerDialog timePickerDialog = null;
                timePickerDialog = new TimePickerDialog(view.getContext(),
                        android.R.style.Theme_DeviceDefault_Dialog,
                        timeSetListener, lastSelectedHour, lastSelectedMinute, true);
                timePickerDialog.show();
            }
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