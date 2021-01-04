package com.startup.eventsearcher.main.ui.map.createEvent;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.startup.eventsearcher.App;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.connectionToServer.test.TestRequester;
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
import com.startup.eventsearcher.utils.JsonHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventCreatorActivity extends AppCompatActivity {

    private static final String TAG = "myEventCreator";

    @BindView(R.id.event_creator_header)
    TextInputLayout textFieldHeader;
    @BindView(R.id.event_creator_category)
    ViewPager viewPagerCategory;
    @BindView(R.id.event_creator_tab_layout)
    TabLayout tabLayoutCategory;
    @BindView(R.id.event_creator_location)
    TextInputLayout textFieldLocation;
    @BindView(R.id.event_creator_start_date)
    TextInputLayout textFieldStartDate;
    @BindView(R.id.event_creator_start_time)
    TextInputLayout textFieldStartTime;
    @BindView(R.id.event_creator_comment)
    TextInputLayout textFieldComment;
    @BindView(R.id.event_creator_accept_btn)
    Button buttonAccept;
    @BindView(R.id.event_creator_progress_bar)
    FrameLayout frameLayoutProgressBar;

    private static ErrorServerHandler errorServer;
    private Handler handler;

    private ArrayList<Category> categoryArrayList = new ArrayList<>();
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
        setContentView(R.layout.activity_event_creator);
        ButterKnife.bind(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Создание эвента");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handler = new Handler(Looper.getMainLooper());

        address = getIntent().getParcelableExtra("Address");

        categoryAdapter = new CategoryAdapter(this, categoryArrayList);
        viewPagerCategory.setAdapter(categoryAdapter);
        tabLayoutCategory.setupWithViewPager(viewPagerCategory);

        componentListener();
        initCategorySlider();

        if (address != null) {
            fillAddress(address);
        }

        // Get Current Date
        calendar = Calendar.getInstance();
        lastSelectedYear = calendar.get(Calendar.YEAR);
        lastSelectedMonth = calendar.get(Calendar.MONTH);
        lastSelectedDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void fillAddress(Address address) {
        Objects.requireNonNull(textFieldLocation.getEditText()).setText(address.getAddressLine(0));
    }

    private void initCategorySlider() {
        categoryArrayList.addAll(App.getCategoryArrayList());
        categoryAdapter.notifyDataSetChanged();
    }

    private void componentListener() {

        Objects.requireNonNull(textFieldStartDate.getEditText()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        String date = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        textFieldStartDate.getEditText().setText(date);
                        lastSelectedYear = year;
                        lastSelectedMonth = monthOfYear;
                        lastSelectedDayOfMonth = dayOfMonth;
                    }
                };

                DatePickerDialog datePickerDialog = null;
                datePickerDialog = new DatePickerDialog(view.getContext(),
                        android.R.style.Theme_DeviceDefault_Dialog,
                        dateSetListener, lastSelectedYear, lastSelectedMonth, lastSelectedDayOfMonth);
                datePickerDialog.show();
            }
        });

        Objects.requireNonNull(textFieldStartTime.getEditText()).setOnClickListener(new View.OnClickListener() {
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
                        textFieldStartTime.getEditText().setText(time);
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

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibleProgressBar(View.VISIBLE);
                Thread threadSendEventToServer = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Поток, отвечающий за связь с сервером
                        Thread threadRequestToServer = new Thread(new Runnable() {
                            @Override
                            public void run() {
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
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(view.getContext(), errorServer.getDescription(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }else {
                            //Получаем загаловок
                            String header = Objects.requireNonNull(textFieldHeader.getEditText()).getText().toString();

                            //Получаем категорию
                            int posCurrentCategory = viewPagerCategory.getCurrentItem();
                            String category = App.getCategoryArrayList().get(posCurrentCategory).getCategoryName();

                            //Получаю адресс и координаты местонахождения
                            EventAddress eventAddress = new EventAddress(
                                    Objects.requireNonNull(textFieldLocation.getEditText()).getText().toString(),
                                    address.getLatitude(), address.getLongitude());

                            //Получаем время начала
                            String startTime = Objects.requireNonNull(textFieldStartTime.getEditText()).getText().toString();

                            //получаем создателя эвента
                            Person personCreator = CurrentPerson.getPerson();

                            //Получаем комментарий
                            String comment = Objects.requireNonNull(textFieldComment.getEditText()).getText().toString();

                            //Ининциализация списка подписчиков
                            ArrayList<Subscriber> subscribers = new ArrayList<>();
                            subscribers.add(new Subscriber(personCreator, new ExtraDate(startTime, "")));

                            event = new Event(header, category, eventAddress, startTime,
                                    CurrentPerson.getPerson(), subscribers, comment);

                            //Добавляем в общий список эвентов
                            EventsList.addEvent(event);

                            JsonHandler.saveObjectToSharedPreference(view.getContext(),
                                    "Events",
                                    "eventKey",
                                    EventsList.getEventArrayList());

                            Log.d(TAG, "buttonAccept: event = " +event.toString());
                            Intent intent = new Intent();
                            intent.putExtra("Event", event);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                });
                threadSendEventToServer.start();
            }
        });
    }

    public void setVisibleProgressBar(int visible) {
        frameLayoutProgressBar.setVisibility(visible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
