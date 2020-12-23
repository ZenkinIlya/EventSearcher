package com.startup.eventsearcher.main.ui.events;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.startup.eventsearcher.App;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.event.EventActivity;
import com.startup.eventsearcher.main.ui.events.model.Category;
import com.startup.eventsearcher.main.ui.events.model.Event;

import java.util.ArrayList;
import java.util.List;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private final List<Event> listEvents;
    private Context context;
    private RecyclerView recyclerView;

    public EventRecyclerViewAdapter(Context context, RecyclerView recyclerView, List<Event> items) {
        this.context = context;
        this.recyclerView = recyclerView;
        listEvents = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_list, parent, false);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                Intent intent = new Intent(parent.getContext(), EventActivity.class);
                intent.putExtra("Event", listEvents.get(itemPosition));
                parent.getContext().startActivity(intent);
            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.eventTitle.setText(listEvents.get(position).getHeader());
        holder.eventAddress.setText(listEvents.get(position).getEventAddress().getAddress());

        int countPeople = listEvents.get(position).getHashMapSubscribersPerson().entrySet().size();
        holder.eventCountPeople.setText(String.valueOf(countPeople));
        holder.eventTime.setText(listEvents.get(position).getStartTime());

        int resourceId = getResourceIdImage(listEvents.get(position));
        Glide.with(context).load(resourceId).into(holder.eventImage);
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
        public final ImageView eventImage;

        public ViewHolder(View view) {
            super(view);
            View = view;
            eventTitle = (TextView) view.findViewById(R.id.list_events_title);
            eventAddress = (TextView) view.findViewById(R.id.list_events_address);
            eventCountPeople = (TextView) view.findViewById(R.id.list_events_count_people);
            eventTime = (TextView) view.findViewById(R.id.list_events_time);
            eventImage = view.findViewById(R.id.list_events_image_category);
        }
    }
}