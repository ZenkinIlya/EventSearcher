package com.startup.eventsearcher.views.events.createEvent;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.App;
import com.startup.eventsearcher.models.user.User;
import com.startup.eventsearcher.utils.user.FirebaseAuthUserGetter;
import com.startup.eventsearcher.databinding.ActivityEventCreatorBinding;
import com.startup.eventsearcher.models.event.Category;
import com.startup.eventsearcher.models.event.Event;
import com.startup.eventsearcher.models.event.EventAddress;
import com.startup.eventsearcher.models.event.ExtraDate;
import com.startup.eventsearcher.models.event.Subscriber;
import com.startup.eventsearcher.utils.DateParser;
import com.startup.eventsearcher.utils.dateTimeMaterialPicker.DateTimeMaterialPicker;
import com.startup.eventsearcher.utils.dateTimeMaterialPicker.IDateTimeMaterialPicker;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class EventCreatorActivity extends AppCompatActivity implements SetLocationEventFragment.ISetLocationEvent,
        IDateTimeMaterialPicker {

    private static final String TAG = "myEventCreator";

    private ActivityEventCreatorBinding bind;

    private final ArrayList<Category> categoryArrayList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private Event event;
    private Address address;
    private DateTimeMaterialPicker dateTimeMaterialPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityEventCreatorBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setTitle("Создание эвента");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        address = getIntent().getParcelableExtra("Address");

        categoryAdapter = new CategoryAdapter(this, categoryArrayList);
        bind.eventCreatorCategory.setAdapter(categoryAdapter);
        bind.eventCreatorTabLayout.setupWithViewPager(bind.eventCreatorCategory);

        componentListener();
        initCategorySlider();

        if (address != null) {
            fillAddress(address);
        }

        dateTimeMaterialPicker = new DateTimeMaterialPicker(this,
                getSupportFragmentManager());
    }

    private void fillAddress(Address address){
        Objects.requireNonNull(bind.eventCreatorLocation.getEditText()).setText(address.getAddressLine(0));
    }

    private void initCategorySlider() {
        categoryArrayList.addAll(App.getCategoryArrayList());
        categoryAdapter.notifyDataSetChanged();
    }

    private void componentListener() {

        //Изменение местоположения эвента
        bind.eventCreatorRedactionLocation.setOnClickListener(view -> {
            SetLocationEventFragment setLocationEventFragment = SetLocationEventFragment.newInstance(address);
            setLocationEventFragment.show(getSupportFragmentManager(), "fragment_set_location_event");
        });

        //Изменение даты проведения эвента
        //Результат возвращается в onGetDate(String date);
        Objects.requireNonNull(bind.eventCreatorStartDate.getEditText())
                .setOnClickListener(view -> dateTimeMaterialPicker.getMaterialDatePicker());

        //Изменение времени начала проведения эвента
        //Результат возвращается в onGetTime(String time);
        Objects.requireNonNull(bind.eventCreatorStartTime.getEditText())
                .setOnClickListener(view -> dateTimeMaterialPicker.getMaterialTimePicker());

        //Подтвердить создание эвента
        bind.eventCreatorAcceptBtn.setOnClickListener(view -> {
            setVisibleProgressBar(View.VISIBLE);

            //Формирование эвента
            event = createEvent();

            //Добавление эвента в Cloud FireStore (полностью весь объект)
            saveEvent();

            setVisibleProgressBar(View.INVISIBLE);

            Log.d(TAG, "bind.eventCreatorAcceptBtn: event = " +event.toString());
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    private void saveEvent() {
        App.db.collection("events")
                .add(event)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(EventCreatorActivity.this, "Эвент создан", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EventCreatorActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error adding document", e);
                });
    }

    private Event createEvent(){
        //Получаем загаловок
        String header = Objects.requireNonNull(bind.eventCreatorHeader.getEditText()).getText().toString();

        //Получаем категорию
        int posCurrentCategory = bind.eventCreatorCategory.getCurrentItem();
        String category = App.getCategoryArrayList().get(posCurrentCategory).getCategoryName();

        //Получаю адрес и координаты местонахождения
        EventAddress eventAddress = new EventAddress(
                Objects.requireNonNull(bind.eventCreatorLocation.getEditText()).getText().toString(),
                address.getLocality(), //city
                address.getThoroughfare(),  //street
                address.getSubThoroughfare(), //house
                address.getLatitude(),
                address.getLongitude());

        //Получеам дату начала
        String startDate = Objects.requireNonNull(bind.eventCreatorStartDate.getEditText()).getText().toString();
        //Получаем время начала
        String startTime = Objects.requireNonNull(bind.eventCreatorStartTime.getEditText()).getText().toString();
        //Преобразуем дату и время в формат Date
        Date date = DateParser.parseDate(startDate, startTime);

        //получаем создателя эвента
        User user = FirebaseAuthUserGetter.getUserFromFirebaseAuth();

        //Получаем комментарий
        String comment = Objects.requireNonNull(bind.eventCreatorComment.getEditText()).getText().toString();

        //Ининциализация списка подписчиков
        ArrayList<Subscriber> subscribers = new ArrayList<>();
        subscribers.add(new Subscriber(user, new ExtraDate(date, "")));

        return new Event(header, category, eventAddress, date, user, subscribers, comment);
    }

    public void setVisibleProgressBar(int visible) {
        bind.eventCreatorProgressBar.setVisibility(visible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void returnAddress(Address address) {
        this.address = address;
        fillAddress(this.address);
    }

    @Override
    public void onGetDate(String date) {
        Objects.requireNonNull(bind.eventCreatorStartDate.getEditText()).setText(date);
    }

    @Override
    public void onGetTime(String time) {
        Objects.requireNonNull(bind.eventCreatorStartTime.getEditText()).setText(time);
    }
}
