package com.startup.eventsearcher.main.ui.events;

import android.content.Intent;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.eventsearcher.App;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.event.EventActivity;
import com.startup.eventsearcher.main.ui.events.filter.Filter;
import com.startup.eventsearcher.main.ui.events.filter.FilterHandler;
import com.startup.eventsearcher.main.ui.events.model.Category;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;
import com.startup.eventsearcher.main.ui.subscribe.SubscribeActivity;
import com.startup.eventsearcher.utils.Config;
import com.startup.eventsearcher.utils.DateParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "myEventAdapter";

    private final List<Event> listEvents;
    private List<Event> listEventsFilter;
    private final Fragment context;
    private final RecyclerView recyclerView;
    private final TextView eventEventsGone;


    public EventRecyclerViewAdapter(Fragment context, RecyclerView recyclerView, List<Event> items, TextView eventEventsGone) {
        this.context = context;
        this.recyclerView = recyclerView;
        listEvents = items;
        listEventsFilter = new ArrayList<>();
        listEventsFilter.addAll(items);
        this.eventEventsGone = eventEventsGone;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_list, parent, false);

        //Вызов подробной информации об эвенте
        view.setOnClickListener(view1 -> {
            int itemPosition = recyclerView.getChildLayoutPosition(view1);
            Intent intent = new Intent(parent.getContext(), EventActivity.class);
            intent.putExtra("Event", listEventsFilter.get(itemPosition));
            ((EventFragment)context).myStartActivityForResult(intent, Config.SHOW_EVENT);
        });

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
//        Glide.with(context).load(resourceId).into(holder.eventImage);

        if (currentPersonIsSubscribe(event) != null){
            holder.eventSubscribe.setImageDrawable(ContextCompat.getDrawable(context.requireContext(), R.drawable.ic_favorite));
        }else {
            holder.eventSubscribe.setImageDrawable(ContextCompat.getDrawable(context.requireContext(), R.drawable.ic_unfavorite));
        }

        //Подписка
        holder.eventSubscribe.setOnClickListener(view -> {
            Subscriber subscriber = currentPersonIsSubscribe(event);
            //Если пользователь не подписан
            if (subscriber == null){
                Intent intent = new Intent(context.requireContext(), SubscribeActivity.class);
                intent.putExtra("Event", event);
                ((EventFragment)context).myStartActivityForResult(intent, Config.SUBSCRIBE);
            }else {
                //Убираем подписчика и обновляем список эвентов
                //TODO Отправка запроса на сервер об отписке
                event.getSubscribers().remove(subscriber);
                notifyDataSetChanged();
            }
        });

        //Показ местоположения эвента
        holder.eventLocationMarker.setOnClickListener(view -> EventFragment.showEventLocation(context, event));
    }

    //Сброс фильтрованного списка
    public void resetFilterList(){
        listEventsFilter.clear();
        listEventsFilter.addAll(listEvents);
    }

    //Проверка подписан ли пользователь на эвент
    private Subscriber currentPersonIsSubscribe(Event event){
        for (Subscriber subscriber: event.getSubscribers()){
            if (subscriber.getPerson().equals(CurrentPerson.getPerson())){
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View View;
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
            View = view;
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
    }
}