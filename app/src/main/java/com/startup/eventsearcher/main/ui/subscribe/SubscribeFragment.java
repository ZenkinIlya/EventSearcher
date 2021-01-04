package com.startup.eventsearcher.main.ui.subscribe;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.EventsList;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;
import com.startup.eventsearcher.utils.JsonHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SubscribeFragment extends Fragment {

    private static final String TAG = "mySubscribeList";

    @BindView(R.id.subscribe_event_list)
    RecyclerView subscribeRecyclerView;
    @BindView(R.id.subscribe_event_list_gone)
    TextView textViewSubscribeEventListGone;

    private SubscribeEventsRecyclerViewAdapter subscribeEventsRecyclerViewAdapter;

    public static SubscribeFragment newInstance(String param1, String param2) {
        SubscribeFragment fragment = new SubscribeFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subscribe, container, false);
        ButterKnife.bind(this, view);

        Type type = new TypeToken<ArrayList<Event>>(){}.getType();
        EventsList.setEventArrayList(JsonHandler.getSavedObjectFromPreference(requireContext(), "Events",
                "eventKey", type));

        //Берем эвенты из списка на которые подписан пользователь
        ArrayList<Event> subscribeEventsArrayList = getSubscribeEvents(EventsList.getEventArrayList());

        subscribeEventsRecyclerViewAdapter = new SubscribeEventsRecyclerViewAdapter(
                this,
                subscribeRecyclerView,
                subscribeEventsArrayList,
                textViewSubscribeEventListGone);
        subscribeRecyclerView.setAdapter(subscribeEventsRecyclerViewAdapter);

        return view;
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
        JsonHandler.saveObjectToSharedPreference(requireContext(),
                "Events",
                "eventKey",
                EventsList.getEventArrayList());
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

    private ArrayList<Event> getSubscribeEvents(ArrayList<Event> arrayList) {
        ArrayList<Event> subscribeEventsArrayList = new ArrayList<>();
        for (Event event : arrayList){
            for (Subscriber subscriber : event.getSubscribers()){
                if (subscriber.getPerson().equals(CurrentPerson.getPerson())){
                    subscribeEventsArrayList.add(event);
                }
            }
        }
        return subscribeEventsArrayList;
    }
}