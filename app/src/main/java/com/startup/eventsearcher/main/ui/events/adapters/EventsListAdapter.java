package com.startup.eventsearcher.main.ui.events.adapters;

import android.os.Build;
import android.util.Log;
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
import com.startup.eventsearcher.main.ui.events.filter.Filter;
import com.startup.eventsearcher.main.ui.events.filter.FilterHandler;
import com.startup.eventsearcher.main.ui.events.model.Category;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.utils.DateParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.ViewHolder> {

    private static final String TAG = "myEventAdapter";

    private List<Event> listEvents = new ArrayList<>();
    private List<Event> listEventsFilter = new ArrayList<>();
    private final TextView eventEventsGone;

    private final EventRecyclerViewListener eventRecyclerViewListener;

    public EventsListAdapter(TextView eventEventsGone,
                             EventRecyclerViewListener eventRecyclerViewListener) {
        this.eventRecyclerViewListener = eventRecyclerViewListener;
        this.eventEventsGone = eventEventsGone;
    }

    public void setListEvents(List<Event> listEvents) {
        this.listEvents = listEvents;
        listEventsFilter.clear();
        listEventsFilter.addAll(listEvents);
    }

    public List<Event> getListEvents() {
        return listEvents;
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
        Event event = listEventsFilter.get(position);

        holder.eventTitle.setText(event.getHeader());
        holder.eventDateNumber.setText(DateParser.getDateFormatDay(event.getDate()));
        holder.eventDateMonth.setText(DateParser.getDateFormatMonth(event.getDate()));

        holder.eventAddress.setText(event.getEventAddress().getAddress());

        int countPeople = event.getSubscribers().size();
        holder.eventCountPeople.setText(String.valueOf(countPeople));
        holder.eventTime.setText(DateParser.getDateFormatTime(event.getDate()));

        int resourceId = getResourceIdImage(event);
        holder.eventImage.setImageResource(resourceId);

        if (currentPersonIsSubscribe(event) != null){
            holder.eventSubscribe.setImageDrawable(ContextCompat.getDrawable(holder.eventView.getContext(), R.drawable.ic_favorite));
        }else {
            holder.eventSubscribe.setImageDrawable(ContextCompat.getDrawable(holder.eventView.getContext(), R.drawable.ic_unfavorite));
        }

        holder.onBindSuccess(event, eventRecyclerViewListener);
    }

    //Сброс фильтрованного списка
    public void resetFilterList(){
        listEventsFilter.clear();
        listEventsFilter.addAll(listEvents);
    }

    //Проверка подписан ли пользователь на эвент
    private Subscriber currentPersonIsSubscribe(Event event){
        for (Subscriber subscriber: event.getSubscribers()){
            if (subscriber.getUser().equals(FirebaseAuthUserGetter.getUserFromFirebaseAuth())){
                return subscriber;
            }
        }
        return null;
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
        eventEventsGone.setVisibility(listEvents.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        return listEventsFilter.size();
    }

    public void filter() {
        Filter filter = FilterHandler.getFilter();
        String searchText = FilterHandler.getSearchText();
        ArrayList<String> arrayListCategory = FilterHandler.getArrayListCategory();

        Log.d(TAG, "filter: " + filter.toString() +
                "; searchText = " +searchText +
                "; arrayListCategory = " + arrayListCategory.toString());

        String searchDate = filter.getDate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            listEventsFilter = listEvents.stream()
                    .filter(event -> {
                        int countMembers = event.getSubscribers().size();
                        return (searchText.isEmpty() || event.getHeader().equals(searchText)) &&
                                (arrayListCategory.isEmpty() || arrayListCategory.contains(event.getCategory().toLowerCase())) &&
                                (filter.getCity().isEmpty() || filter.getCity().toLowerCase().equals(event.getEventAddress().getCity().toLowerCase())) &&
                                ((filter.getEndCountMembers() == -1 && filter.getStartCountMembers() <= countMembers) ||
                                        (filter.getStartCountMembers() <= countMembers && countMembers <= filter.getEndCountMembers())) &&
                                (searchDate.isEmpty() || DateParser.getDateFormatDate(event.getDate()).equals(searchDate));
                    })
                    .collect(Collectors.toList());
        }
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

        public void onBindSuccess(Event event, EventRecyclerViewListener eventRecyclerViewListener) {

            //Вызов подробной информации об эвенте
            eventView.setOnClickListener(view -> eventRecyclerViewListener.onEventClick(event));

            //Подписка
            eventSubscribe.setOnClickListener(view -> {
                Subscriber subscriber = currentPersonIsSubscribe(event);
                if (subscriber == null) {
                    eventRecyclerViewListener.onSubscribe(event);
                }else {
                    //TODO Отправка запроса на сервер об отписке
                    event.getSubscribers().remove(subscriber);
                    notifyDataSetChanged();
                }
            });

            //Показ местоположения
            eventLocationMarker.setOnClickListener(view -> eventRecyclerViewListener.onMarkerClick(event));
        }
    }
}