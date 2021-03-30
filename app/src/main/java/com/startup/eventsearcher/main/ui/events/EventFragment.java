package com.startup.eventsearcher.main.ui.events;

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
import com.startup.eventsearcher.databinding.FragmentEventListBinding;
import com.startup.eventsearcher.main.ui.events.createEvent.EventCreatorActivity;
import com.startup.eventsearcher.main.ui.events.event.LocationEventFragment;
import com.startup.eventsearcher.main.ui.events.filter.FilterActivity;
import com.startup.eventsearcher.main.ui.events.filter.FilterHandler;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.map.presenters.EventFireStorePresenter;
import com.startup.eventsearcher.main.ui.map.views.IFireStoreView;
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

public class EventFragment extends Fragment implements IFireStoreView {

    private static final String TAG = "tgEventFrag";

    private FragmentEventListBinding bind;

    private EventRecyclerViewAdapter eventRecyclerViewAdapter;
    private static FragmentManager fragmentManager;
    private EventFireStorePresenter eventFireStorePresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentEventListBinding.inflate(inflater, container, false);

        fragmentManager = getParentFragmentManager();
        ((AppCompatActivity) requireActivity()).setSupportActionBar(bind.eventListToolbar);
        setHasOptionsMenu(true);

        eventFireStorePresenter = new EventFireStorePresenter(this);

        TagRecyclerViewAdapter tagRecyclerViewAdapter = new TagRecyclerViewAdapter(
                Arrays.asList(requireContext().getResources().getStringArray(R.array.category)),
                eventRecyclerViewAdapter);
        bind.eventListTag.setAdapter(tagRecyclerViewAdapter);

        componentListener();

        return bind.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
        eventFireStorePresenter.startEventAddListener();
    }

    @Override
    public void onGetEvents(ArrayList<Event> eventArrayList) {
        eventRecyclerViewAdapter = new EventRecyclerViewAdapter(
                this,
                bind.eventList,
                eventArrayList,
                bind.eventListGone);
        bind.eventList.setAdapter(eventRecyclerViewAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        eventFireStorePresenter.endRegistrationListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:{
                switch (requestCode) {
                    case Config.SUBSCRIBE:{
                        Log.d(TAG, "onActivityResult: (RESULT_OK, SUBSCRIBE) Пользователь подписался");
                        break;
                    }
                    case Config.CREATE_EVENT:{
                        Log.d(TAG, "onActivityResult: (RESULT_OK, CREATE_EVENT) Пользователь создал эвент из меню");
                        break;
                    }
                    case Config.SHOW_FILTER:{
                        eventRecyclerViewAdapter.filter();
                        Log.d(TAG, "onActivityResult: (RESULT_OK, SHOW_FILTER) Фильтр применился");
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

        /*Строка поиска
        * doOnNext() выполнется в io потоке*/
        Disposable disposable = RxSearchView.queryTextChanges(bind.eventListSearch)
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(charSequence -> {
                    Log.d(TAG, "componentListener io: " + charSequence);
                    FilterHandler.setSearchText(charSequence.toString());
                    eventRecyclerViewAdapter.filter();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> {
                            Log.d(TAG, "componentListener main: " + charSequence);
                            eventRecyclerViewAdapter.notifyDataSetChanged();
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