package com.startup.eventsearcher.views.subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.databinding.ActivitySubscribeBinding;
import com.startup.eventsearcher.models.event.Event;
import com.startup.eventsearcher.models.event.ExtraDate;
import com.startup.eventsearcher.models.event.Subscriber;
import com.startup.eventsearcher.presenters.firestore.EventSubscribeFireStorePresenter;
import com.startup.eventsearcher.utils.DateParser;
import com.startup.eventsearcher.utils.dateTimeMaterialPicker.DateTimeMaterialPicker;
import com.startup.eventsearcher.utils.dateTimeMaterialPicker.IDateTimeMaterialPicker;
import com.startup.eventsearcher.utils.user.FirebaseAuthUserGetter;

import java.util.Date;
import java.util.Objects;

public class SubscribeActivity extends AppCompatActivity implements IDateTimeMaterialPicker, ISubscribeFireStoreView {

    private ActivitySubscribeBinding bind;

    private Event event;
    private DateTimeMaterialPicker dateTimeMaterialPicker;
    private EventSubscribeFireStorePresenter eventSubscribeFireStorePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySubscribeBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle("Подписаться");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dateTimeMaterialPicker = new DateTimeMaterialPicker(this, getSupportFragmentManager());
        eventSubscribeFireStorePresenter = new EventSubscribeFireStorePresenter(this);

        event = (Event) getIntent().getSerializableExtra("Event");

        componentsListener();
    }

    private void componentsListener() {

        bind.subscribeBtnSubscribe.setOnClickListener(view ->
                eventSubscribeFireStorePresenter.subscribeToEventInFirebase(
                    event.getId(),
                    getCurrentUserAsSubscriber()));

        Objects.requireNonNull(bind.subscribeStartTime.getEditText())
                .setOnClickListener(view -> dateTimeMaterialPicker.getMaterialTimePicker());
    }

    private Subscriber getCurrentUserAsSubscriber(){
        String startTime = Objects.requireNonNull(bind.subscribeStartTime.getEditText()).getText().toString();
        String startDate = DateParser.getDateFormatDate(event.getDate());
        //Преобразуем дату и время в формат Date
        Date date = DateParser.parseDate(startDate, startTime);
        return new Subscriber(
                FirebaseAuthUserGetter.getUserFromFirebaseAuth(),
                new ExtraDate(
                        date,
                        bind.subscribeComment.getEditText().getText().toString()
                ));
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
        //не вызывается
    }

    @Override
    public void onGetTime(String time) {
        Objects.requireNonNull(bind.subscribeStartTime.getEditText()).setText(time);
    }

    @Override
    public void onSuccess() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onGetError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean show) {
        if (show){
            bind.subscribeLoading.setVisibility(View.VISIBLE);
        }else {
            bind.subscribeLoading.setVisibility(View.INVISIBLE);
        }
    }
}