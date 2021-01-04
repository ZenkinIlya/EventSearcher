package com.startup.eventsearcher.main.ui.events.event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

import java.util.ArrayList;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventActivity extends AppCompatActivity {

    private static final String TAG = "myEvent";

    @BindView(R.id.list_events_title)
    TextView textViewEventTitle;
    @BindView(R.id.list_events_address)
    TextView textViewEventAddress;
    @BindView(R.id.list_events_count_people)
    TextView textViewCountPeople;
    @BindView(R.id.list_events_time)
    TextView textViewEventTime;
    @BindView(R.id.list_events_image_category)
    ImageView imageViewCategory;
    @BindView(R.id.list_events_location)
    ImageView imageViewLocation;
    @BindView(R.id.list_events_subscribe)
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

                        //По индексу ищем эвент и добавляем туда пользователя
                        Subscriber subscriber = new Subscriber(CurrentPerson.getPerson(),
                                new ExtraDate(time, comment));
                        EventsList.getEventArrayList().get(indexOfEvent).getSubscribers().add(subscriber);
                        event.getSubscribers().add(subscriber);

                        fillFields();
                        personRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onActivityResult: Пользователь подписался");
                    }
                }
                break;
            }
            case RESULT_CANCELED:{
                switch (requestCode){
                    case Config.SUBSCRIBE:{
                        personRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onActivityResult: Пользователь отменил этап подписки");
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

            }
        });

        //Подписка
        imageViewSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Если пользователь подписан
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
                    event.getSubscribers().remove(subscriber);

                    fillFields();  //Убираем сердечко
                    personRecyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    //Заполняем поля
    private void fillFields() {
        textViewEventTitle.setText(event.getHeader());
        textViewEventAddress.setText(event.getEventAddress().getAddress());
        textViewCountPeople.setText(String.valueOf(event.getSubscribers().size()));
        textViewEventTime.setText(event.getStartTime());

        setImageSubscribe();

        int resourceId = getResourceIdImage(event);
        Glide.with(this).load(resourceId).into(imageViewCategory);

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