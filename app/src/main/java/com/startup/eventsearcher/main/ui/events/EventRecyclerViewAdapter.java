package com.startup.eventsearcher.main.ui.events;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.model.EventContent.EventItem;

import java.util.List;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private final List<EventItem> listEvents;

    public EventRecyclerViewAdapter(List<EventItem> items) {
        listEvents = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.eventTitle.setText(listEvents.get(position).title);
        holder.eventAddress.setText(listEvents.get(position).address);
        holder.eventCountPeople.setText(listEvents.get(position).countPeople);
        holder.eventTime.setText(listEvents.get(position).time);
    }

    @Override
    public int getItemCount() {
        return listEvents.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View View;
        public final TextView eventTitle;
        public final TextView eventAddress;
        public final TextView eventCountPeople;
        public final TextView eventTime;

        public ViewHolder(View view) {
            super(view);
            View = view;
            eventTitle = (TextView) view.findViewById(R.id.list_events_title);
            eventAddress = (TextView) view.findViewById(R.id.list_events_address);
            eventCountPeople = (TextView) view.findViewById(R.id.list_events_count_people);
            eventTime = (TextView) view.findViewById(R.id.list_events_time);
        }
    }
}