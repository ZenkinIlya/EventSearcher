package com.startup.eventsearcher.views.subscribe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.startup.eventsearcher.presenters.firestore.EventSubscribeFireStorePresenter;
import com.startup.eventsearcher.utils.user.FirebaseAuthUserGetter;
import com.startup.eventsearcher.databinding.FragmentSubscribeBinding;
import com.startup.eventsearcher.views.events.adapters.EventRecyclerViewListener;
import com.startup.eventsearcher.views.events.adapters.EventsListAdapter;
import com.startup.eventsearcher.views.events.adapters.TypeEventList;
import com.startup.eventsearcher.views.events.event.EventActivity;
import com.startup.eventsearcher.views.events.event.LocationEventFragment;
import com.startup.eventsearcher.models.event.Event;
import com.startup.eventsearcher.models.event.Subscriber;
import com.startup.eventsearcher.presenters.firestore.EventGetterFireStorePresenter;
import com.startup.eventsearcher.views.map.IEventGetterFireStoreView;
import com.startup.eventsearcher.utils.Config;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SubscribeFragment extends Fragment implements EventRecyclerViewListener,
        IEventGetterFireStoreView, ISubscribeFireStoreView {

    private static final String TAG = "mySubscribeList";

    private FragmentSubscribeBinding bind;

    private EventsListAdapter eventsListAdapter;
    private final ArrayList<Event> subscribeEvents = new ArrayList<>();
    private static FragmentManager fragmentManager;
    private EventGetterFireStorePresenter eventGetterFireStorePresenter;
    private EventSubscribeFireStorePresenter eventSubscribeFireStorePresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentSubscribeBinding.inflate(inflater, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();
        bind.subscribeEventListSwipeRefresh.setOnRefreshListener(() -> eventGetterFireStorePresenter.getAllEventsFromFireBase());

        initEventsListAdapter();

        eventSubscribeFireStorePresenter = new EventSubscribeFireStorePresenter(this);
        eventGetterFireStorePresenter = new EventGetterFireStorePresenter(this);
        eventGetterFireStorePresenter.getAllEventsFromFireBase();

        return bind.getRoot();
    }

    private void initEventsListAdapter() {
        eventsListAdapter = new EventsListAdapter(
                TypeEventList.SUBSCRIBES,
                bind.subscribeEventListGone,
                this);
        bind.subscribeEventList.setAdapter(eventsListAdapter);
    }

    @Override
    public void onGetEvents(ArrayList<Event> eventArrayList) {
        getSubscribeEvents(eventArrayList);
    }

    @Override
    public void onSuccess() {
        //Вызывается только при удачной отписке
    }

    @Override
    public void onGetError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading(boolean show) {
        bind.subscribeEventListSwipeRefresh.setRefreshing(show);
    }

    @Override
    public void onEventClick(String eventId) {
        Intent intent = new Intent(getContext(), EventActivity.class);
        intent.putExtra("eventId", eventId);
        startActivityForResult(intent, Config.SHOW_EVENT);
    }

    @Override
    public void onSubscribe(Event event) {
        //Подписаться не возможно, так как отображаются только эвенты на которые пользователь уже подписан
    }

    @Override
    public void onMarkerClick(Event event) {
        LocationEventFragment locationEventFragment = LocationEventFragment.newInstance(event);
        locationEventFragment.setTargetFragment(getParentFragment(), 300);
        locationEventFragment.show(fragmentManager, "fragment_location_event");
    }

    @Override
    public void onUnSubscribe(Event event, Subscriber subscriber) {
        eventSubscribeFireStorePresenter.unSubscribeFormEventInFirebase(event.getId(), subscriber);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:{
                switch (requestCode) {
                }
                break;
            }
            case RESULT_CANCELED:{
                switch (requestCode){
                    case Config.SHOW_EVENT:{
                        //Обновляем список в случае если пользователь отписался при просмотре эвента
                        eventGetterFireStorePresenter.getAllEventsFromFireBase();
                        Log.d(TAG, "onActivityResult: (RESULT_CANCELED, SHOW_EVENT) Пользователь вышел из подробного просмотра эвента");
                        break;
                    }
                }
                break;
            }
        }
    }

    private void getSubscribeEvents(ArrayList<Event> arrayList) {
        subscribeEvents.clear();
        for (Event event : arrayList){
            for (Subscriber subscriber : event.getSubscribers()){
                if (subscriber.getUser().equals(FirebaseAuthUserGetter.getUserFromFirebaseAuth())){
                    subscribeEvents.add(event);
                }
            }
        }
        eventsListAdapter.setListEvents(subscribeEvents);
        eventsListAdapter.notifyDataSetChanged();
    }
}