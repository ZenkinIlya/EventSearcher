package com.startup.eventsearcher.main.ui.events;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.dummy.DummyContent.DummyItem;

import java.util.List;

public class MyEventRecyclerViewAdapter extends RecyclerView.Adapter<MyEventRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> Values;

    public MyEventRecyclerViewAdapter(List<DummyItem> items) {
        Values = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.mItem = Values.get(position);
        holder.eventTitle.setText(Values.get(position).title);
        holder.eventAddress.setText(Values.get(position).address);
        holder.eventCountPeople.setText(Values.get(position).countPeople);
        holder.eventTime.setText(Values.get(position).time);
    }

    @Override
    public int getItemCount() {
        return Values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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