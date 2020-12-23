package com.startup.eventsearcher.main.ui.events.event;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.startup.eventsearcher.App;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.model.Category;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.ExtraDate;
import com.startup.eventsearcher.main.ui.profile.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventActivity extends AppCompatActivity {

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
    @BindView(R.id.event_comment)
    TextView textViewComment;
    @BindView(R.id.event_members)
    RecyclerView recyclerViewMembers;

    private Event event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        ButterKnife.bind(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Эвент");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        event = (Event) getIntent().getSerializableExtra("Event");
        if (event != null) {
            fillFields();
            RecyclerView recyclerView = findViewById(R.id.event_members);
            List<Map.Entry<Person, ExtraDate>> personExtraDateList = new ArrayList<>(event.getHashMapSubscribersPerson().entrySet());
            PersonRecyclerViewAdapter personRecyclerViewAdapter = new PersonRecyclerViewAdapter(personExtraDateList, this, recyclerView);
            recyclerView.setAdapter(personRecyclerViewAdapter);
        }
    }

    //Заполняем поля
    private void fillFields() {
        textViewEventTitle.setText(event.getHeader());
        textViewEventAddress.setText(event.getEventAddress().getAddress());
        textViewCountPeople.setText(String.valueOf(event.getHashMapSubscribersPerson().keySet().size()));
        textViewEventTime.setText(event.getStartTime());

        int resourceId = getResourceIdImage(event);
        Glide.with(this).load(resourceId).into(imageViewCategory);

        textViewComment.setText(event.getComment());
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