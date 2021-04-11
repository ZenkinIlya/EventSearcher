package com.startup.eventsearcher.views.events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.jakewharton.rxbinding4.appcompat.RxSearchView;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.models.event.Subscriber;
import com.startup.eventsearcher.presenters.firestore.EventSubscribeFireStorePresenter;
import com.startup.eventsearcher.views.events.adapters.EventRecyclerViewListener;
import com.startup.eventsearcher.views.events.adapters.EventsListAdapter;
import com.startup.eventsearcher.views.events.adapters.TagRecyclerViewListener;
import com.startup.eventsearcher.views.events.adapters.TypeEventList;
import com.startup.eventsearcher.views.events.createEvent.EventCreatorActivity;
import com.startup.eventsearcher.views.events.event.EventActivity;
import com.startup.eventsearcher.views.events.event.LocationEventFragment;
import com.startup.eventsearcher.views.events.filter.FilterActivity;
import com.startup.eventsearcher.views.events.filter.FilterHandler;
import com.startup.eventsearcher.databinding.FragmentEventListBinding;
import com.startup.eventsearcher.views.events.adapters.TagRecyclerViewAdapter;
import com.startup.eventsearcher.models.event.Event;
import com.startup.eventsearcher.presenters.firestore.EventGetterFireStorePresenter;
import com.startup.eventsearcher.views.map.IEventGetterFireStoreView;
import com.startup.eventsearcher.views.subscribe.ISubscribeFireStoreView;
import com.startup.eventsearcher.views.subscribe.SubscribeActivity;
import com.startup.eventsearcher.utils.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/*При отписке отправляется инфо на сервер что пользователь отписался,
 * если отправка прошла успешно, то опустошаем сердчеко в UI
 * При подписке вызывается окно подписки. При подтверждении отправляется запрос на сервер, если
 * он прошел упешно, то из окна подписки возвращаются параметры: index, startTime, comment. Затем
 * идет обновление глобального списка эвентов и обновление адаптера*/

public class EventsListFragment extends Fragment implements EventRecyclerViewListener,
        IEventGetterFireStoreView, ISubscribeFireStoreView, TagRecyclerViewListener {

    private static final String TAG = "tgEventFrag";

    private FragmentEventListBinding bind;

    private EventsListAdapter eventsListAdapter;
    private EventGetterFireStorePresenter eventGetterFireStorePresenter;
    private EventSubscribeFireStorePresenter eventSubscribeFireStorePresenter;
    private static FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentEventListBinding.inflate(inflater, container, false);

        fragmentManager = getActivity().getSupportFragmentManager();
        ((AppCompatActivity) requireActivity()).setSupportActionBar(bind.eventListToolbar);
        setHasOptionsMenu(true);

        eventGetterFireStorePresenter = new EventGetterFireStorePresenter(this);
        eventSubscribeFireStorePresenter = new EventSubscribeFireStorePresenter(this);

        initEventListAdapter();
        initTagListAdapter();
        componentListener();

        eventGetterFireStorePresenter.getAllEventsFromFireBase();

        return bind.getRoot();
    }

    private void initTagListAdapter() {
        TagRecyclerViewAdapter tagRecyclerViewAdapter = new TagRecyclerViewAdapter(
                Arrays.asList(requireContext().getResources().getStringArray(R.array.category)),
                this);
        bind.eventListTag.setAdapter(tagRecyclerViewAdapter);
    }

    private void initEventListAdapter() {
        eventsListAdapter = new EventsListAdapter(
                TypeEventList.DEFAULT,
                bind.eventListGone,
                this);
        bind.eventList.setAdapter(eventsListAdapter);
    }


    @Override
    public void onEventClick(String eventId) {
        Intent intent = new Intent(getContext(), EventActivity.class);
        intent.putExtra("eventId", eventId);
        startActivityForResult(intent, Config.SHOW_EVENT);
    }

    @Override
    public void onSubscribe(Event event) {
        Intent intent = new Intent(getContext(), SubscribeActivity.class);
        intent.putExtra("Event", event);
        startActivityForResult(intent, Config.SUBSCRIBE);
    }

    @Override
    public void onMarkerClick(Event event) {
        LocationEventFragment locationEventFragment = LocationEventFragment.newInstance(event);
        locationEventFragment.setTargetFragment(getParentFragment(), 300);
        locationEventFragment.show(fragmentManager, "fragment_location_event");
    }

    @Override
    public void onUnSubscribe(Event event, Subscriber subscriber) {
        eventSubscribeFireStorePresenter.unSubscribeFormEventInFirebase(event.getId(), subscriber);
    }

    @Override
    public void onGetEvents(ArrayList<Event> eventArrayList) {
        //Получаем эвенты с сервера и фильтруем их
        eventsListAdapter.setListEvents(eventArrayList);
        eventsListAdapter.filter();
        eventsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccess() {
        //Вызывается при отписке
    }

    @Override
    public void onGetError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading(boolean show) {
        bind.eventSwipeRefresh.setRefreshing(show);
    }

    @Override
    public void onFilter() {
        eventsListAdapter.filter();
        eventsListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:{
                switch (requestCode) {
                    case Config.SUBSCRIBE:{
                        eventGetterFireStorePresenter.getAllEventsFromFireBase();
                        Log.d(TAG, "onActivityResult: (RESULT_OK, SUBSCRIBE) Пользователь подписался");
                        break;
                    }
                    case Config.CREATE_EVENT:{
                        eventGetterFireStorePresenter.getAllEventsFromFireBase();
                        Log.d(TAG, "onActivityResult: (RESULT_OK, CREATE_EVENT) Пользователь создал эвент из меню");
                        break;
                    }
                    case Config.SHOW_FILTER:{
                        eventsListAdapter.filter();
                        eventsListAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onActivityResult: (RESULT_OK, SHOW_FILTER) Фильтр применился");
                        break;
                    }
                }
                break;
            }
            case RESULT_CANCELED:{
                switch (requestCode){
                    case Config.SUBSCRIBE:{
                        Log.d(TAG, "onActivityResult: (RESULT_CANCELED, SUBSCRIBE) Пользователь вышел из подробного просмотра эвента");
                        break;
                    }
                    case Config.SHOW_EVENT:{
                        //Обновляем список в случае если пользователь отписался/подписался при просмотре эвента
                        eventGetterFireStorePresenter.getAllEventsFromFireBase();
                        Log.d(TAG, "onActivityResult: (RESULT_CANCELED, SHOW_EVENT) Пользователь вышел из подробного просмотра эвента");
                        break;
                    }
                    case Config.SHOW_FILTER:{
                        Log.d(TAG, "onActivityResult: (RESULT_CANCELED, SHOW_FILTER) Пользователь вышел из фильтра");
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
            case R.id.add_event: {
                //Создание эвента без начального выбора адреса
                Intent intent = new Intent(getContext(), EventCreatorActivity.class);
                startActivityForResult(intent, Config.CREATE_EVENT);
                return true;
            }
            case R.id.filter: {
                Intent intent = new Intent(getContext(), FilterActivity.class);
                startActivityForResult(intent, Config.SHOW_FILTER);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void componentListener() {

        /*Строка поиска
        * doOnNext() выполнется в io потоке*/
        Disposable disposable = RxSearchView.queryTextChanges(bind.eventListSearch)
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(charSequence -> {
                    Log.d(TAG, "componentListener io: " + charSequence);
                    FilterHandler.setSearchText(charSequence.toString());
                    eventsListAdapter.filter();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                            Log.d(TAG, "componentListener main: " + charSequence);
                            eventsListAdapter.notifyDataSetChanged();
                            },
                        throwable -> {
                            Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "componentListener: " + throwable.getMessage(), throwable);
                        });

        //Показ подробного фильтра
        bind.eventListBtnFilter.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), FilterActivity.class);
            startActivityForResult(intent, Config.SHOW_FILTER);
        });

        bind.eventSwipeRefresh.setOnRefreshListener(() -> eventGetterFireStorePresenter.getAllEventsFromFireBase());
    }
}