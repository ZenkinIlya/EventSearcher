package com.startup.eventsearcher.main.ui.map.presenters;

import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.startup.eventsearcher.App;
import com.startup.eventsearcher.main.ui.events.model.Event;
import com.startup.eventsearcher.main.ui.map.views.IFireStoreView;

import java.util.ArrayList;

public class EventFireStorePresenter implements IEventFireStorePresenter{

    private static final String TAG = "tgEventFireStorePres";
    private final ArrayList<Event> eventArrayList = new ArrayList<>();
    private final IFireStoreView iFireStoreView;
    private ListenerRegistration listenerRegistration;

    public EventFireStorePresenter(IFireStoreView iFireStoreView) {
        this.iFireStoreView = iFireStoreView;
    }

    @Override
    public void startEventsListener() {

        listenerRegistration = App.db.collection("events")
                .addSnapshotListener((value, error) -> {

                    if (error != null){
                        Log.e(TAG, "startEventsListener: " + error.getLocalizedMessage());
                    }else if (value != null) {
                        //Перебираю все изменения во всей коллекции
                        for (DocumentChange documentChange: value.getDocumentChanges()){
                            //Получаю id документа который был изменен/удален/добавлен
                            String id = documentChange.getDocument().getId();
                            Log.d(TAG, "startEventsListener: id эвента с изменениями = " +id);

                            switch (documentChange.getType()) {
                                case ADDED:
                                    Event addedEvent = documentChange.getDocument().toObject(Event.class);
                                    addedEvent.setId(id);
                                    eventArrayList.add(addedEvent);
                                    Log.d(TAG, "startEventsListener: эвент добавлен");
                                    break;
                                case MODIFIED:
                                    //Ищу эвент с таким же id  у себя в списке и обновляю его
                                    for (Event event: eventArrayList){
                                        if (event.getId().equals(id)){
                                            //Получаю индекс эвента в списке
                                            int index = eventArrayList.indexOf(event);
                                            //Заменяю эвент списка на новый-обновленный
                                            Event modifyEvent = documentChange.getDocument().toObject(Event.class);
                                            modifyEvent.setId(id);
                                            eventArrayList.set(index, modifyEvent);
                                            Log.d(TAG, "startEventsListener: эвент обновлен");
                                            break;
                                        }
                                    }
                                    break;
                                case REMOVED:
                                    for (Event event: eventArrayList){
                                        if (event.getId().equals(id)){
                                            eventArrayList.remove(event);
                                            break;
                                        }
                                    }
                                    Log.d(TAG, "startEventsListener: эвент изъят");
                                    break;
                            }
                        }

                        Log.i(TAG, "startEventsListener: eventArrayList = " +eventArrayList.toString());

                        iFireStoreView.onGetEvents(eventArrayList);
                    }
        });

    }

    @Override
    public void endEventsListener() {
        listenerRegistration.remove();
    }
}
