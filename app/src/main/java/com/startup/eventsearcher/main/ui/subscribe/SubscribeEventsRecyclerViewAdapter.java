package com.startup.eventsearcher.main.ui.subscribe;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.startup.eventsearcher.App;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.event.EventActivity;
import com.startup.eventsearcher.main.ui.events.model.Category;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;

import java.util.ArrayList;

public class SubscribeEventsRecyclerViewAdapter extends RecyclerView.Adapter<SubscribeEventsRecyclerViewAdapter.ViewHolder> {

    private Fragment context;
    private RecyclerView subscribeRecyclerView;
    private ArrayList<Event> subscribeEventsArrayList;
    private TextView textViewSubscribeEventListGone;

    public SubscribeEventsRecyclerViewAdapter(SubscribeFragment subscribeFragment,
                                              RecyclerView subscribeRecyclerView,
                                              ArrayList<Event> subscribeEventsArrayList,
                                              TextView textViewSubscribeEventListGone) {
        this.context = subscribeFragment;
        this.subscribeRecyclerView = subscribeRecyclerView;
        this.subscribeEventsArrayList = subscribeEventsArrayList;
        this.textViewSubscribeEventListGone = textViewSubscribeEventListGone;
    }

    @NonNull
    @Override
    public SubscribeEventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_list, parent, false);

        //Вызов подробной информации об эвенте
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = subscribeRecyclerView.getChildLayoutPosition(view);
                Intent intent = new Intent(parent.getContext(), EventActivity.class);
                intent.putExtra("Event", subscribeEventsArrayList.get(itemPosition));
                parent.getContext().startActivity(intent);
            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscribeEventsRecyclerViewAdapter.ViewHolder holder, int position) {
        Event event = subscribeEventsArrayList.get(position);
        holder.eventTitle.setText(event.getHeader());
        holder.eventAddress.setText(event.getEventAddress().getAddress());

        int countPeople = event.getSubscribers().size();
        holder.eventCountPeople.setText(String.valueOf(countPeople));
        holder.eventTime.setText(event.getStartTime());

        int resourceId = getResourceIdImage(event);
        holder.eventImage.setImageResource(resourceId);
//        Glide.with(context).load(resourceId).into(holder.eventImage);

        holder.eventSubscribe.setImageDrawable(ContextCompat.getDrawable(context.requireContext(), R.drawable.ic_favorite));

        //Отписка
        holder.eventSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeCurrentPersonFromSubscribers(event);
                subscribeEventsArrayList.remove(event);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });

    }

    //Удаление текущего пользователя из подписчиков выбранного эвента
    private void removeCurrentPersonFromSubscribers(Event event) {
        ArrayList<Subscriber> subscriberArrayList = event.getSubscribers();
        for (Subscriber subscriber: event.getSubscribers()){
            if (subscriber.getPerson().equals(CurrentPerson.getPerson())){
                subscriberArrayList.remove(subscriber);
                break;
            }
        }
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
        textViewSubscribeEventListGone.setVisibility(subscribeEventsArrayList.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        return subscribeEventsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View View;
        public final TextView eventTitle;
        public final TextView eventAddress;
        public final TextView eventCountPeople;
        public final TextView eventTime;
        public final ImageView eventImage;
        public final ImageView eventSubscribe;

        public ViewHolder(View view) {
            super(view);
            View = view;
            eventTitle = view.findViewById(R.id.list_events_title);
            eventAddress = view.findViewById(R.id.list_events_address);
            eventCountPeople = view.findViewById(R.id.list_events_count_people);
            eventTime = view.findViewById(R.id.list_events_time);
            eventImage = view.findViewById(R.id.list_events_image_category);
            eventSubscribe = view.findViewById(R.id.list_events_subscribe);
        }
    }
}
