package com.startup.eventsearcher.main.ui.events;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.dummy.DummyContent;

public class EventFragment extends Fragment {

    public EventFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.eventList);
        recyclerView.setAdapter(new MyEventRecyclerViewAdapter(DummyContent.ITEMS));
        return view;
    }
}