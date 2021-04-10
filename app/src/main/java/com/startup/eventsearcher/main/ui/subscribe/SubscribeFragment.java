package com.startup.eventsearcher.main.ui.subscribe;

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

import com.startup.eventsearcher.authentication.utils.user.FirebaseAuthUserGetter;
import com.startup.eventsearcher.databinding.FragmentSubscribeBinding;
import com.startup.eventsearcher.main.ui.events.adapters.EventRecyclerViewListener;
import com.startup.eventsearcher.main.ui.events.event.EventActivity;
import com.startup.eventsearcher.main.ui.events.event.LocationEventFragment;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.main.ui.map.presenters.EventFireStorePresenter;
import com.startup.eventsearcher.main.ui.map.views.IFireStoreView;
import com.startup.eventsearcher.utils.Config;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SubscribeFragment extends Fragment implements EventRecyclerViewListener, IFireStoreView {

    private static final String TAG = "mySubscribeList";

    private FragmentSubscribeBinding bind;

    private SubscribeEventsRecyclerViewAdapter subscribeEventsRecyclerViewAdapter;
    private final ArrayList<Event> subscribeEvents = new ArrayList<>();
    private static FragmentManager fragmentManager;
    private EventFireStorePresenter eventFireStorePresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentSubscribeBinding.inflate(inflater, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();
        eventFireStorePresenter = new EventFireStorePresenter(this);
        bind.subscribeEventListSwipeRefresh.setOnRefreshListener(() -> eventFireStorePresenter.getAllEventsFromFireBase());

        initSubscribeEventListAdapter();

        eventFireStorePresenter.getAllEventsFromFireBase();

        return bind.getRoot();
    }

    private void initSubscribeEventListAdapter() {
        subscribeEventsRecyclerViewAdapter = new SubscribeEventsRecyclerViewAdapter(
                bind.subscribeEventListGone,
                this);
        bind.subscribeEventList.setAdapter(subscribeEventsRecyclerViewAdapter);
    }

    @Override
    public void onGetEvents(ArrayList<Event> eventArrayList) {
        getSubscribeEvents(eventArrayList);
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
    public void onEventClick(Event event) {
        Intent intent = new Intent(getContext(), EventActivity.class);
        intent.putExtra("Event", event);
        startActivityForResult(intent, Config.SHOW_EVENT);
    }

    @Override
    public void onSubscribe(Event event) {
        //nothing
    }

    @Override
    public void onMarkerClick(Event event) {
        LocationEventFragment locationEventFragment = LocationEventFragment.newInstance(event);
        locationEventFragment.setTargetFragment(getParentFragment(), 300);
        locationEventFragment.show(fragmentManager, "fragment_location_event");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:{
                break;
            }
            case RESULT_CANCELED:{
                switch (requestCode){
                    case Config.SHOW_EVENT:{
                        //Обновляем список в случае если пользователь отписался при просмотре эвента
                        eventFireStorePresenter.getAllEventsFromFireBase();
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
        subscribeEventsRecyclerViewAdapter.setSubscribeEventsArrayList(subscribeEvents);
        subscribeEventsRecyclerViewAdapter.notifyDataSetChanged();
    }
}