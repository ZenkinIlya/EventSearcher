package com.startup.eventsearcher.main.ui.events;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.startup.eventsearcher.main.ui.events.model.Category;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;
import com.startup.eventsearcher.main.ui.subscribe.SubscribeActivity;
import com.startup.eventsearcher.utils.Config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<Event> listEvents;
    private List<Event> listEventsFilter;
    private Fragment context;
    private RecyclerView recyclerView;
    private TextView eventEventsGone;


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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = recyclerView.getChildLayoutPosition(view);
                Intent intent = new Intent(parent.getContext(), EventActivity.class);
                intent.putExtra("Event", listEventsFilter.get(itemPosition));
                ((EventFragment)context).myStartActivityForResult(intent, Config.SHOW_EVENT);
            }
        });

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Event event = listEventsFilter.get(position);

        holder.eventTitle.setText(event.getHeader());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        holder.eventDateNumber.setText(event.getDateFormatDay(simpleDateFormat));
        SimpleDateFormat simpleDateFormatMonth = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        holder.eventDateMonth.setText(event.getDateFormatMonth(simpleDateFormatMonth));

        holder.eventAddress.setText(event.getEventAddress().getAddress());

        int countPeople = event.getSubscribers().size();
        holder.eventCountPeople.setText(String.valueOf(countPeople));
        holder.eventTime.setText(event.getStartTime());

        int resourceId = getResourceIdImage(event);
        holder.eventImage.setImageResource(resourceId);
//        Glide.with(context).load(resourceId).into(holder.eventImage);

        if (currentPersonIsSubscribe(event) != null){
            holder.eventSubscribe.setImageDrawable(ContextCompat.getDrawable(context.requireContext(), R.drawable.ic_favorite));
        }else {
            holder.eventSubscribe.setImageDrawable(ContextCompat.getDrawable(context.requireContext(), R.drawable.ic_unfavorite));
        }

        //Подписка
        holder.eventSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        //Показ местоположения эвента
        holder.eventLocationMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventFragment.showEventLocation(context, event);
            }
        });
    }

    //Обновление фильтрованного списка
    public void addAllItemsToFilterList(){
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
        eventEventsGone.setVisibility(listEvents.isEmpty() ? View.VISIBLE : View.INVISIBLE);
        return listEventsFilter.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                charSequence = charSequence.toString().toLowerCase();
                FilterResults results = new FilterResults();
                if (charSequence.length() == 0){
                    synchronized(this)
                    {
                        results.values = listEvents;
                    }
                }else {
                    results.values = getFilteredResults(charSequence.toString());
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                listEventsFilter = (List<Event>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    protected List<Event> getFilteredResults(String constraint) {
        List<Event> results = new ArrayList<>();

        for (Event event : listEvents) {
            if (event.getCategory().toLowerCase().contains(constraint)){
                results.add(event);
            }
        }
        return results;
    }

    //На вход фильтра приходят: категории, город, ренж участников, дата проведения
    //Если изменился один из параматреов фильтрации, то фильтрация должна проходить и по остальным параметрам
    //категория - город - ренж - дата
    public void filterByCategory(ArrayList<String> arrayListFilterCategory){
        if (arrayListFilterCategory.size() != 0){
            List<Event> results = new ArrayList<>();
            for (Event event : listEvents) {
                for (String category: arrayListFilterCategory){
                    if (event.getCategory().toLowerCase().contains(category.toLowerCase())){
                        results.add(event);
                        break;
                    }
                }
            }
            listEventsFilter = results;
        }else{
            listEventsFilter = listEvents;
        }

        notifyDataSetChanged();
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