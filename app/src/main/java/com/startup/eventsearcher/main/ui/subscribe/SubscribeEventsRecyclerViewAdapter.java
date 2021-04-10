package com.startup.eventsearcher.main.ui.subscribe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.eventsearcher.App;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.utils.user.FirebaseAuthUserGetter;
import com.startup.eventsearcher.main.ui.events.adapters.EventRecyclerViewListener;
import com.startup.eventsearcher.main.ui.events.model.Category;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.utils.DateParser;

import java.util.ArrayList;

public class SubscribeEventsRecyclerViewAdapter extends RecyclerView.Adapter<SubscribeEventsRecyclerViewAdapter.ViewHolder> {

    private ArrayList<Event> subscribeEventsArrayList = new ArrayList<>();
    private final TextView textViewSubscribeEventListGone;
    private final EventRecyclerViewListener eventRecyclerViewListener;

    public SubscribeEventsRecyclerViewAdapter(TextView textViewSubscribeEventListGone,
                                              EventRecyclerViewListener eventRecyclerViewListener) {
        this.textViewSubscribeEventListGone = textViewSubscribeEventListGone;
        this.eventRecyclerViewListener = eventRecyclerViewListener;
    }

    public void setSubscribeEventsArrayList(ArrayList<Event> subscribeEventsArrayList) {
        this.subscribeEventsArrayList = subscribeEventsArrayList;
    }

    @NonNull
    @Override
    public SubscribeEventsRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscribeEventsRecyclerViewAdapter.ViewHolder holder, int position) {
        Event event = subscribeEventsArrayList.get(position);

        holder.eventTitle.setText(event.getHeader());
        holder.eventDateNumber.setText(DateParser.getDateFormatDay(event.getDate()));
        holder.eventDateMonth.setText(DateParser.getDateFormatMonth(event.getDate()));

        holder.eventAddress.setText(event.getEventAddress().getAddress());

        int countPeople = event.getSubscribers().size();
        holder.eventCountPeople.setText(String.valueOf(countPeople));
        holder.eventTime.setText(DateParser.getDateFormatTime(event.getDate()));

        int resourceId = getResourceIdImage(event);
        holder.eventImage.setImageResource(resourceId);

        holder.eventSubscribe.setImageDrawable(ContextCompat.getDrawable(holder.eventView.getContext(), R.drawable.ic_favorite));

        holder.onBindingSuccess(event, position, eventRecyclerViewListener);
    }

    //Удаление текущего пользователя из подписчиков выбранного эвента
    private void removeCurrentPersonFromSubscribers(Event event) {
        ArrayList<Subscriber> subscriberArrayList = event.getSubscribers();
        for (Subscriber subscriber: event.getSubscribers()){
            if (subscriber.getUser().equals(FirebaseAuthUserGetter.getUserFromFirebaseAuth())){
                subscriberArrayList.remove(subscriber);
                break;
            }
        }
    }

    private int getResourceIdImage(Event event) {
        for (Category category: App.getCategoryArrayList()){
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

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View eventView;
        public final TextView eventTitle;
        public final TextView eventAddress;
        public final TextView eventCountPeople;
        public final TextView eventTime;
        public final ImageView eventImage;
        public final ImageView eventSubscribe;
        private final LinearLayout eventLocationMarker;
        private final TextView eventDateNumber;
        private final TextView eventDateMonth;

        public ViewHolder(View view) {
            super(view);
            eventView = view;
            eventTitle = view.findViewById(R.id.list_events_title);
            eventAddress = view.findViewById(R.id.list_events_address);
            eventCountPeople = view.findViewById(R.id.list_events_count_people);
            eventTime = view.findViewById(R.id.list_events_time);
            eventImage = view.findViewById(R.id.list_events_image_category);
            eventSubscribe = view.findViewById(R.id.list_events_subscribe);
            eventLocationMarker = view.findViewById(R.id.list_events_layout_location);
            eventDateNumber = view.findViewById(R.id.list_events_date_number);
            eventDateMonth = view.findViewById(R.id.list_events_date_month);
        }

        public void onBindingSuccess(Event event, int position, EventRecyclerViewListener eventRecyclerViewListener) {

            //Вызов подробной информации об эвенте
            eventView.setOnClickListener(view -> eventRecyclerViewListener.onEventClick(event));

            //Подписка
            eventSubscribe.setOnClickListener(view -> {
                removeCurrentPersonFromSubscribers(event);
                subscribeEventsArrayList.remove(event);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                //TODO Отправка запроса на сервер об отписке
            });

            //Показ местоположения
            eventLocationMarker.setOnClickListener(view -> eventRecyclerViewListener.onMarkerClick(event));
        }
    }
}
