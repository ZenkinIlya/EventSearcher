package com.startup.eventsearcher.main.ui.subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.databinding.ActivitySubscribeBinding;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.utils.dateTimeMaterialPicker.DateTimeMaterialPicker;
import com.startup.eventsearcher.utils.dateTimeMaterialPicker.IDateTimeMaterialPicker;

import java.util.Objects;

public class SubscribeActivity extends AppCompatActivity implements IDateTimeMaterialPicker {

    private ActivitySubscribeBinding bind;

    private Event event;
    private DateTimeMaterialPicker dateTimeMaterialPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySubscribeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle("Подписаться");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateTimeMaterialPicker = new DateTimeMaterialPicker(this, getSupportFragmentManager());

        event = (Event) getIntent().getSerializableExtra("Event");

        componentsListener();
    }

    private void componentsListener() {

        bind.subscribeBtnSubscribe.setOnClickListener(view -> {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });

        Objects.requireNonNull(bind.subscribeStartTime.getEditText())
                .setOnClickListener(view -> dateTimeMaterialPicker.getMaterialTimePicker());
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

    @Override
    public void onGetDate(String date) {

    }

    @Override
    public void onGetTime(String time) {
        Objects.requireNonNull(bind.subscribeStartTime.getEditText()).setText(time);
    }
}