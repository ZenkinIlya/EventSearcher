package com.startup.eventsearcher.main.ui.events;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.model.EventContent;

import java.util.Arrays;
import java.util.Objects;

public class EventFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        RecyclerView eventRecyclerView = view.findViewById(R.id.eventList);
        eventRecyclerView.setAdapter(new EventRecyclerViewAdapter(getContext(), EventContent.ITEMS));

        RecyclerView tagRecyclerView = view.findViewById(R.id.eventTagList);
        tagRecyclerView.setAdapter(new TagRecyclerViewAdapter(
                Arrays.asList(requireContext().getResources().getStringArray(R.array.category))));

        return view;
    }
}