package com.startup.eventsearcher.main.ui.events.event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.eventsearcher.App;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.model.Category;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.EventsList;
import com.startup.eventsearcher.main.ui.events.model.ExtraDate;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;
import com.startup.eventsearcher.main.ui.subscribe.SubscribeActivity;
import com.startup.eventsearcher.utils.Config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/*Активити отображения эвента
* На вход принимает эвент для отображения
* При нажатии на "подписаться" появляется окно подписки. После успешного завершение подписки
* файл json перезаписывается в onActivityResult. При снятии подписки json тут же обновляется*/

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "myEvent";

    @BindView(R.id.list_events_title)
    TextView textViewEventTitle;
    @BindView(R.id.list_events_date_number)
    TextView textViewEventDateDay;
    @BindView(R.id.list_events_date_month)
    TextView textViewEventDateMonth;
    @BindView(R.id.list_events_address)
    TextView textViewEventAddress;
    @BindView(R.id.list_events_count_people)
    TextView textViewCountPeople;
    @BindView(R.id.list_events_time)
    TextView textViewEventTime;
    @BindView(R.id.list_events_image_category)
    ImageView imageViewCategory;
    @BindView(R.id.list_events_layout_location)
    LinearLayout imageViewLocation;
    @BindView(R.id .list_events_subscribe)
    ImageView imageViewSubscribe;
    @BindView(R.id.event_comment)
    TextView textViewComment;
    @BindView(R.id.event_members)
    RecyclerView recyclerViewMembers;

    private Event event;
    private PersonRecyclerViewAdapter personRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Эвент");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);  //удаление тени

        //Принимаем эвент для подробного просмотра
        event = (Event) getIntent().getSerializableExtra("Event");
        if (event != null) {
            fillFields();
            RecyclerView recyclerView = findViewById(R.id.event_members);
            personRecyclerViewAdapter = new PersonRecyclerViewAdapter(event.getSubscribers(), this, recyclerView);
            recyclerView.setAdapter(personRecyclerViewAdapter);
        }

        componentListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + " resultCode=" + resultCode);
        switch (resultCode){
            case RESULT_OK:{
                switch (requestCode) {
                    case Config.SUBSCRIBE: {
                        int indexOfEvent = data.getIntExtra("index", 0);
                        String time = data.getStringExtra("time");
                        String comment = data.getStringExtra("comment");
                        Log.d(TAG, "onActivityResult: index = " + indexOfEvent +
                                "; time = " + time + "; comment = " + comment);

                        //По индексу ищем эвент и добавляем туда пользователя-подписчика
                        Subscriber subscriber = new Subscriber(CurrentPerson.getPerson(),
                                new ExtraDate(time, comment));
                        EventsList.getEventArrayList().get(indexOfEvent).getSubscribers().add(subscriber);
                        //Обновляем текущий эвент
                        event.getSubscribers().add(subscriber);
                        //Сохраняем список эвентов в JSON
                        EventsList.saveEventArrayListInJSON(this);

                        fillFields();
                        personRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onActivityResult: EventList = " + EventsList.getEventArrayList().toString());
                        Log.d(TAG, "onActivityResult: Пользователь подписался");
                        break;
                    }
                }
                break;
            }
            case RESULT_CANCELED:{
                switch (requestCode){
                    case Config.SUBSCRIBE:{
                        personRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onActivityResult: Пользователь отменил этап подписки");
                        break;
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void componentListener() {
        imageViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationEventFragment locationEventFragment = LocationEventFragment.newInstance(event);
                FragmentManager fragmentManager = getSupportFragmentManager();
                locationEventFragment.show(fragmentManager, "fragment_location_event");
            }
        });

        //Подписка
        imageViewSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Если пользователь не подписан
                Subscriber subscriber = currentPersonIsSubscribe(event);
                if (subscriber == null){
                    //Осуществляется подписка
                    Intent intent = new Intent(view.getContext(), SubscribeActivity.class);
                    intent.putExtra("Event", event);
                    startActivityForResult(intent, Config.SUBSCRIBE);
                }else {
                    //Убрать сабскрайбера в эвенте глобального списка
                    for (Event eventIterate: EventsList.getEventArrayList()){
                        if (eventIterate.equals(event)){
                            eventIterate.getSubscribers().remove(subscriber);
                            break;
                        }
                    }
                    //Получаем позицию пользователя в списке
                    int index = event.getSubscribers().indexOf(subscriber);
                    event.getSubscribers().remove(subscriber);
                    //Сохраняем список эвентов в JSON
                    EventsList.saveEventArrayListInJSON(view.getContext());

                    fillFields();  //Убираем сердечко
                    personRecyclerViewAdapter.notifyItemRemoved(index);
                }
            }
        });
    }

    //Заполняем поля
    private void fillFields() {
        textViewEventTitle.setText(event.getHeader());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        textViewEventDateDay.setText(event.getDateFormatDay(simpleDateFormat));
        SimpleDateFormat simpleDateFormatMonth = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        textViewEventDateMonth.setText(event.getDateFormatMonth(simpleDateFormatMonth));

        textViewEventAddress.setText(event.getEventAddress().getAddress());
        textViewCountPeople.setText(String.valueOf(event.getSubscribers().size()));
        textViewEventTime.setText(event.getStartTime());

        setImageSubscribe();

        int resourceId = getResourceIdImage(event);
        imageViewCategory.setImageResource(resourceId);
//        Glide.with(this).load(resourceId).into(imageViewCategory);

        textViewComment.setText(event.getComment());
    }

    //Установка иконки подписки
    private void setImageSubscribe() {
        if (currentPersonIsSubscribe(event) != null){
            imageViewSubscribe.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite));
        }else {
            imageViewSubscribe.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_unfavorite));
        }
    }

    //Проверка подписан ли пользователь на эвент
    private Subscriber currentPersonIsSubscribe(Event event){
        for (Subscriber subscriber: event.getSubscribers()){
            if (subscriber.getPerson().equals(CurrentPerson.getPerson())){
                return subscriber;
            }
        }
        return null;
    }

    //Получаем картинку в зависимости от категории эвента
    private int getResourceIdImage(Event event) {
        ArrayList<Category> categoryArrayList = App.getCategoryArrayList();
        for (Category category: categoryArrayList){
            if (category.getCategoryName().equals(event.getCategory())){
                return category.getCategoryImage();
            }
        }
        return 0;
    }
}