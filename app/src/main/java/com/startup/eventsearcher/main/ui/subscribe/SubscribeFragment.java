package com.startup.eventsearcher.main.ui.subscribe;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.startup.eventsearcher.databinding.FragmentSubscribeBinding;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.EventsList;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;
import com.startup.eventsearcher.utils.Config;

import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class SubscribeFragment extends Fragment {

    private static final String TAG = "mySubscribeList";

    private FragmentSubscribeBinding bind;

    private SubscribeEventsRecyclerViewAdapter subscribeEventsRecyclerViewAdapter;
    private final ArrayList<Event> subscribeEvents = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentSubscribeBinding.inflate(inflater, container, false);

        //TODO Получаем с сервера список эвентов

        EventsList.getEventArrayListFromJSON(getContext());

        getSubscribeEvents(EventsList.getEventArrayList());

        subscribeEventsRecyclerViewAdapter = new SubscribeEventsRecyclerViewAdapter(
                this,
                bind.subscribeEventList,
                subscribeEvents,
                bind.subscribeEventListGone);
        bind.subscribeEventList.setAdapter(subscribeEventsRecyclerViewAdapter);

        return bind.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated():");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach()");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState():");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        EventsList.saveEventArrayListInJSON(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + " resultCode=" + resultCode);
        switch (resultCode){
            case RESULT_OK:{
                break;
            }
            case RESULT_CANCELED:{
                switch (requestCode){
                    case Config.SHOW_EVENT:{
                        getSubscribeEvents(EventsList.getEventArrayList());
                        subscribeEventsRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onActivityResult: Пользователь вышел из подробного просмотра эвента");
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
                if (subscriber.getPerson().equals(CurrentPerson.getPerson())){
                    subscribeEvents.add(event);
                }
            }
        }
    }

    public void myStartActivityForResult(Intent intent, int showEvent) {
        startActivityForResult(intent, showEvent);
    }
}