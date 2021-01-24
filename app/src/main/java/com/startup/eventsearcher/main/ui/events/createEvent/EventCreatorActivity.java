package com.startup.eventsearcher.main.ui.events.createEvent;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.App;
import com.startup.eventsearcher.authentication.connectionToServer.test.TestRequester;
import com.startup.eventsearcher.databinding.ActivityEventCreatorBinding;
import com.startup.eventsearcher.main.ui.events.model.Category;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.EventAddress;
import com.startup.eventsearcher.main.ui.events.model.EventsList;
import com.startup.eventsearcher.main.ui.events.model.ExtraDate;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;
import com.startup.eventsearcher.main.ui.profile.model.Person;
import com.startup.eventsearcher.utils.Config;
import com.startup.eventsearcher.utils.ErrorServerHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class EventCreatorActivity extends AppCompatActivity implements SetLocationEventFragment.Callback{

    private static final String TAG = "myEventCreator";

    private ActivityEventCreatorBinding bind;

    private static ErrorServerHandler errorServer;
    private Handler handler;

    private final ArrayList<Category> categoryArrayList = new ArrayList<>();
    private CategoryAdapter categoryAdapter;
    private Event event;
    private Address address;

    private Calendar calendar;
    private int lastSelectedYear;
    private int lastSelectedMonth;
    private int lastSelectedDayOfMonth;
    private int lastSelectedHour = -1;
    private int lastSelectedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityEventCreatorBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        SetLocationEventFragment.registerSetLocationEventFragmentCallback(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Создание эвента");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handler = new Handler(Looper.getMainLooper());

        address = getIntent().getParcelableExtra("Address");

        categoryAdapter = new CategoryAdapter(this, categoryArrayList);
        bind.eventCreatorCategory.setAdapter(categoryAdapter);
        bind.eventCreatorTabLayout.setupWithViewPager(bind.eventCreatorCategory);

        componentListener();
        initCategorySlider();

        if (address != null) {
            fillAddress(address);
        }

/*        MaterialTimePicker build = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .build();
        build.show(getSupportFragmentManager(), "tag");
        build.addOnPositiveButtonClickListener(view -> Toast.makeText(this, "qwerty", Toast.LENGTH_SHORT).show());*/

        // Get Current Date
        calendar = Calendar.getInstance();
        lastSelectedYear = calendar.get(Calendar.YEAR);
        lastSelectedMonth = calendar.get(Calendar.MONTH);
        lastSelectedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
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

        Objects.requireNonNull(bind.eventCreatorStartDate.getEditText()).setOnClickListener(view -> {

            DatePickerDialog.OnDateSetListener dateSetListener = (view12, year, monthOfYear, dayOfMonth) -> {
                String date = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
                bind.eventCreatorStartDate.getEditText().setText(date);
                lastSelectedYear = year;
                lastSelectedMonth = monthOfYear;
                lastSelectedDayOfMonth = dayOfMonth;
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(),
                    android.R.style.Theme_DeviceDefault_Dialog,
                    dateSetListener, lastSelectedYear, lastSelectedMonth, lastSelectedDayOfMonth);
            datePickerDialog.show();
        });

        Objects.requireNonNull(bind.eventCreatorStartTime.getEditText()).setOnClickListener(view -> {
            if(lastSelectedHour == -1)  {
                lastSelectedHour = calendar.get(Calendar.HOUR_OF_DAY);
                lastSelectedMinute = calendar.get(Calendar.MINUTE);
            }

            TimePickerDialog.OnTimeSetListener timeSetListener = (view1, hourOfDay, minute) -> {
                String time = hourOfDay + ":" + minute;
                bind.eventCreatorStartTime.getEditText().setText(time);
                lastSelectedHour = hourOfDay;
                lastSelectedMinute = minute;
            };

            TimePickerDialog timePickerDialog = new TimePickerDialog(view.getContext(),
                    android.R.style.Theme_DeviceDefault_Dialog,
                    timeSetListener, lastSelectedHour, lastSelectedMinute, true);
            timePickerDialog.show();
        });

        bind.eventCreatorAcceptBtn.setOnClickListener(view -> {
            setVisibleProgressBar(View.VISIBLE);
            Thread threadSendEventToServer = new Thread(() -> {
                //Поток, отвечающий за связь с сервером
                Thread threadRequestToServer = new Thread(() -> {
                    try {
                        //TODO Отправка на сервер нового эвента в отдельном потоке
                        TestRequester.testRequest();
/*                                        SignInMessage signInMessage = new SignInMessage(
                                    String.valueOf(editTextLogin.getText()), String.valueOf(editTextPassword.getText()));
                            answerServer = AuthenticationRequester.signInRequest(signInMessage);*/

                        Thread.sleep(Config.delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

                threadRequestToServer.start();
                try {
                    //ожидание завершения потока связи с сервером
                    threadRequestToServer.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                setVisibleProgressBar(View.INVISIBLE);

                errorServer = TestRequester.getErrorServerHandler();
                //Если сервер не ответил или ответил ошибкой
                if (errorServer.getCode() != 200) {
                    handler.post(() -> Toast.makeText(view.getContext(), errorServer.getDescription(), Toast.LENGTH_LONG).show());
                }else {
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
                    String startDate = bind.eventCreatorStartDate.getEditText().getText().toString();

                    //Получаем время начала
                    String startTime = Objects.requireNonNull(bind.eventCreatorStartTime.getEditText()).getText().toString();

                    //получаем создателя эвента
                    Person personCreator = CurrentPerson.getPerson();

                    //Получаем комментарий
                    String comment = Objects.requireNonNull(bind.eventCreatorComment.getEditText()).getText().toString();

                    //Ининциализация списка подписчиков
                    ArrayList<Subscriber> subscribers = new ArrayList<>();
                    subscribers.add(new Subscriber(personCreator, new ExtraDate(startTime, "")));

                    event = new Event(header, category, eventAddress, startDate, startTime,
                            CurrentPerson.getPerson(), subscribers, comment);

                    //Добавляем в общий список эвентов
                    EventsList.addEvent(event);

                    //Сохранение списка эвентов в JSON
                    //Сохранение необходимо выполнить тут, так как onStop Срабатывает после initMap
                    EventsList.saveEventArrayListInJSON(view.getContext());

                    Log.d(TAG, "bind.eventCreatorAcceptBtn: event = " +event.toString());
                    Intent intent = new Intent();
                    intent.putExtra("Event", event);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
            threadSendEventToServer.start();
        });
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
        fillAddress(address);
    }
}