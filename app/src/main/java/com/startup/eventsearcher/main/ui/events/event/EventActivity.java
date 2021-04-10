package com.startup.eventsearcher.main.ui.events.event;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.startup.eventsearcher.App;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.utils.user.FirebaseAuthUserGetter;
import com.startup.eventsearcher.databinding.ActivityEventBinding;
import com.startup.eventsearcher.main.ui.events.model.Category;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.main.ui.subscribe.SubscribeActivity;
import com.startup.eventsearcher.utils.Config;
import com.startup.eventsearcher.utils.DateParser;

import java.util.Objects;

/*Активити отображения эвента
* На вход принимает эвент для отображения
* При нажатии на "подписаться" появляется окно подписки. После успешного завершение подписки
* файл json перезаписывается в onActivityResult. При снятии подписки json тут же обновляется*/

public class EventActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "myEvent";

    private ActivityEventBinding bind;

    private Event event;
    private PersonRecyclerViewAdapter personRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityEventBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        Toolbar toolbar = bind.eventToolbar;
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);  //удаление тени

        //Принимаем эвент для подробного просмотра
        event = (Event) getIntent().getSerializableExtra("Event");
        if (event != null) {
            fillFields();
            RecyclerView recyclerView = findViewById(R.id.event_members);
            personRecyclerViewAdapter = new PersonRecyclerViewAdapter(event.getSubscribers(), this, recyclerView);
            recyclerView.setAdapter(personRecyclerViewAdapter);

            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.event_location_event_map);
            if (supportMapFragment != null) {
                supportMapFragment.getMapAsync(this);
            }
        }

        componentListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:{
                switch (requestCode) {
                    case Config.SUBSCRIBE: {
                        Log.d(TAG, "onActivityResult: (RESULT_OK, SUBSCRIBE) Пользователь подписался/отписался");
                        break;
                    }
                }
                break;
            }
            case RESULT_CANCELED:{
                switch (requestCode){
                    case Config.SUBSCRIBE:{
                        Log.d(TAG, "onActivityResult: (RESULT_CANCELED, SUBSCRIBE) Пользователь отменил этап подписки");
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

        //Подписка
        bind.eventSubscribe.setOnClickListener(view -> {
            //Если пользователь не подписан
            Subscriber subscriber = currentPersonIsSubscribe(event);
            if (subscriber == null){
                //Осуществляется подписка
                Intent intent = new Intent(view.getContext(), SubscribeActivity.class);
                intent.putExtra("Event", event);
                startActivityForResult(intent, Config.SUBSCRIBE);
            }else {
                //TODO Убрать сабскрайбера в эвенте глобального списка
            }
        });
    }

    //Заполняем поля
    private void fillFields() {
        bind.eventHeader.setText(event.getHeader());

        bind.eventDateNumber.setText(DateParser.getDateFormatDay(event.getDate()));
        bind.eventDateMonth.setText(DateParser.getDateFormatMonth(event.getDate()));

        bind.eventTextViewCreator.setText(event.getUser().getLogin());

        bind.eventAddress.setText(event.getEventAddress().getAddress());
        bind.eventCountPeople.setText(String.valueOf(event.getSubscribers().size()));
        bind.eventTime.setText(DateParser.getDateFormatTime(event.getDate()));

        setImageSubscribe();

        int resourceId = getResourceIdImage(event);
        bind.eventImageCategory.setImageResource(resourceId);
        Picasso.get()
                .load(resourceId)
                .into(bind.eventImageCategory);
        bind.eventComment.setText(event.getComment());
    }

    //Установка иконки подписки
    private void setImageSubscribe() {
        if (currentPersonIsSubscribe(event) != null){
            bind.eventSubscribe.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite));
        }else {
            bind.eventSubscribe.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_unfavorite));
        }
    }

    //Проверка подписан ли пользователь на эвент
    private Subscriber currentPersonIsSubscribe(Event event){
        for (Subscriber subscriber: event.getSubscribers()){
            if (subscriber.getUser().equals(FirebaseAuthUserGetter.getUserFromFirebaseAuth())){
                return subscriber;
            }
        }
        return null;
    }

    //Получаем картинку в зависимости от категории эвента
    private int getResourceIdImage(Event event) {
        for (Category category: App.getCategoryArrayList()){
            if (category.getCategoryName().equals(event.getCategory())){
                return category.getCategoryImage();
            }
        }
        return 0;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng latLng = new LatLng(event.getEventAddress().getLatitude(),
                event.getEventAddress().getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Config.DEFAULT_ZOOM));
        //Запрет на перемещение по карте
        googleMap.getUiSettings().setAllGesturesEnabled(false);

    }
}