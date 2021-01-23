package com.startup.eventsearcher.main.ui.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.event.LocationEventFragment;
import com.startup.eventsearcher.main.ui.events.filter.FilterActivity;
import com.startup.eventsearcher.main.ui.events.filter.FilterHandler;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.events.model.EventsList;
import com.startup.eventsearcher.main.ui.events.model.ExtraDate;
import com.startup.eventsearcher.main.ui.events.model.Subscriber;
import com.startup.eventsearcher.main.ui.map.createEvent.EventCreatorActivity;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;
import com.startup.eventsearcher.utils.Config;

import java.util.Arrays;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/*При отписке отправляется инфо на сервер что пользователь отписался,
 * если отправка прошла успешно, то опустошаем сердчеко в UI
 * При подписке вызывается окно подписки. При подтверждении отправляется запрос на сервер, если
 * он прошел упешно, то из окна подписки возвращаются параметры: index, startTime, comment. Затем
 * идет обновление глобального списка эвентов и обновление адаптера*/

public class EventFragment extends Fragment {

    private static final String TAG = "myEventList";

    @BindView(R.id.event_list_search)
    SearchView searchView;
    @BindView(R.id.event_list)
    RecyclerView eventRecyclerView;
    @BindView(R.id.event_list_gone)
    TextView eventEventsGone;
    @BindView(R.id.event_list_tag)
    RecyclerView tagRecyclerView;
    @BindView(R.id.event_list_btn_filter)
    ImageView imageViewFilter;

    private EventRecyclerViewAdapter eventRecyclerViewAdapter;
    private TagRecyclerViewAdapter tagRecyclerViewAdapter;

    private static FragmentManager fragmentManager;

    public static EventFragment newInstance(String param1, String param2) {
        EventFragment fragment = new EventFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_list, container, false);
        ButterKnife.bind(this, view);
        fragmentManager = getParentFragmentManager();
        Toolbar toolbar = view.findViewById(R.id.event_list_toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        //TODO Получаем с сервера список эвентов

        eventRecyclerViewAdapter = new EventRecyclerViewAdapter(
                this,
                eventRecyclerView,
                EventsList.getEventArrayListFromJSON(getContext()),
                eventEventsGone);
        eventRecyclerView.setAdapter(eventRecyclerViewAdapter);

        tagRecyclerViewAdapter = new TagRecyclerViewAdapter(
                Arrays.asList(requireContext().getResources().getStringArray(R.array.category)),
                eventRecyclerViewAdapter);
        tagRecyclerView.setAdapter(tagRecyclerViewAdapter);

        componentListener();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated():");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach() :" +this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated()");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState():");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
        EventsList.saveEventArrayListInJSON(getContext());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult(): requestCode=" + requestCode + " resultCode=" + resultCode);
        switch (resultCode){
            case RESULT_OK:{
                switch (requestCode) {
                    case Config.SUBSCRIBE:{
                        int indexOfEvent = Objects.requireNonNull(data).getIntExtra("index", 0);
                        String time = data.getStringExtra("time");
                        String comment = data.getStringExtra("comment");

                        //По индексу ищем эвент и добавляем туда пользователя
                        EventsList.getEventArrayList().get(indexOfEvent).getSubscribers().add(new Subscriber(CurrentPerson.getPerson(),
                                new ExtraDate(time, comment)));

                        //В адаптере два разных списка, но находящийся в них элементы одни и те же объекты,
                        //поэтому изменение объекта одного списка отражается на изменении объекта другого списка
                        eventRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onActivityResult: Пользователь подписался");
                        break;
                    }
                    case Config.CREATE_EVENT:{
                        //При создании нового эвента он помещается только в один список, являющийся
                        //основным списокм на все приложния, а фильтрованный список не изменяется
                        //поэтому в фильтрованный список необходимо добавить созданный эвент либо
                        //пересобрать фильтрованный список
                        eventRecyclerViewAdapter.resetFilterList();
                        eventRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onActivityResult: Пользователь создал эвент из меню");
                        break;
                    }
                    case Config.SHOW_FILTER:{
                        eventRecyclerViewAdapter.filter();
                        break;
                    }
                }
                break;
            }
            case RESULT_CANCELED:{
                switch (requestCode){
                    case Config.SUBSCRIBE:{
                        eventRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onActivityResult: Пользователь отменил этап подписки");
                        break;
                    }
                    case Config.SHOW_EVENT:{
                        eventRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onActivityResult: Пользователь вышел из подробного просмотра эвента");
                        break;
                    }
                    case Config.SHOW_FILTER:{
                        eventRecyclerViewAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onActivityResult: Пользователь вышел из фильтра");
                        break;
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.event_list_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_event:
                //Создание эвента без начального выбора адреса
                Intent intent = new Intent(getContext(), EventCreatorActivity.class);
                startActivityForResult(intent, Config.CREATE_EVENT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void componentListener() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                FilterHandler.setSearchText(newText);
                eventRecyclerViewAdapter.filter();
                return true;
            }
        });

        //Показ подробного фильтра
        imageViewFilter.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), FilterActivity.class);
            startActivityForResult(intent, Config.SHOW_FILTER);
        });
    }

    //Вызывается из адаптера
    public void myStartActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode);
    }

    public static void showEventLocation(Fragment fragment, Event event) {
        LocationEventFragment locationEventFragment = LocationEventFragment.newInstance(event);
        locationEventFragment.setTargetFragment(fragment, 300);
        locationEventFragment.show(fragmentManager, "fragment_location_event");
    }
}