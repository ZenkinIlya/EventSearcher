package com.startup.eventsearcher.presenters.firestore;

import android.util.Log;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.startup.eventsearcher.App;
import com.startup.eventsearcher.models.event.Event;
import com.startup.eventsearcher.utils.DateParser;
import com.startup.eventsearcher.utils.firebase.FirebaseErrorHandler;
import com.startup.eventsearcher.views.map.IEventGetterFireStoreView;

import java.util.ArrayList;
import java.util.Date;

public class EventGetterFireStorePresenter {

    private static final String TAG = "tgEventFireStorePres";
    private final ArrayList<Event> eventArrayList = new ArrayList<>();
    private final IEventGetterFireStoreView iEventGetterFireStoreView;
    private ListenerRegistration listenerRegistration;

    public EventGetterFireStorePresenter(IEventGetterFireStoreView iEventGetterFireStoreView) {
        this.iEventGetterFireStoreView = iEventGetterFireStoreView;
    }

    public void startAllEventChangesListener() {
        //Необходимо при инициализации слушателя очищать список
        eventArrayList.clear();

        listenerRegistration = App.db.collection("events")
                .whereGreaterThanOrEqualTo("date", DateParser.getDateWithMinusHours(new Date(), 12))
                .addSnapshotListener((value, error) -> {

                    if (error != null){
                        Log.e(TAG, "startAllEventChangesListener: " + error.getLocalizedMessage());
                        iEventGetterFireStoreView.onGetError(error.getLocalizedMessage());
                    }else if (value != null) {
                        //Перебираю все изменения во всей коллекции
                        for (DocumentChange documentChange: value.getDocumentChanges()){
                            //Получаю id документа который был изменен/удален/добавлен
                            String id = documentChange.getDocument().getId();
                            Log.d(TAG, "startAllEventChangesListener: id эвента с изменениями = " +id);

                            switch (documentChange.getType()) {
                                case ADDED:
                                    Event addedEvent = documentChange.getDocument().toObject(Event.class);
                                    addedEvent.setId(id);
                                    eventArrayList.add(addedEvent);
                                    Log.d(TAG, "startAllEventChangesListener: эвент добавлен");
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
                                            Log.d(TAG, "startAllEventChangesListener: эвент обновлен");
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
                                    Log.d(TAG, "startAllEventChangesListener: эвент изъят");
                                    break;
                            }
                        }
                        ArrayList<String> idStringArrayList = new ArrayList<>();
                        for (Event event: eventArrayList){
                            idStringArrayList.add(event.getId());
                        }
                        Log.i(TAG, "startAllEventChangesListener: id eventArrayList = " + idStringArrayList.toString());
                        iEventGetterFireStoreView.onGetEvents(eventArrayList);
                    }
        });
    }

    public void startEventAddListener() {
        Log.d(TAG, "startEventAddListener()");

        //Необходимо при инициализации слушателя очищать список
        eventArrayList.clear();

        //Слушатель на добавление эвентов
        listenerRegistration = App.db.collection("events")
                .whereGreaterThanOrEqualTo("date", DateParser.getDateWithMinusHours(new Date(), 12))
                .addSnapshotListener((value, error) -> {

                    if (error != null) {
                        String stringError = FirebaseErrorHandler.errorHandler(error);
                        Log.e(TAG, "startEventAddListener: " + stringError);
                        iEventGetterFireStoreView.onGetError(stringError);
                    } else if (value != null) {
                        //Перебираю все изменения во всей коллекции
                        for (DocumentChange documentChange : value.getDocumentChanges()) {

                            //Получаю id документа который был добавлен/удален
                            String id = documentChange.getDocument().getId();
                            Log.d(TAG, "startEventAddListener: id добавленного эвента = " + id);
                            switch (documentChange.getType()) {
                                case ADDED:
                                    Event addedEvent = documentChange.getDocument().toObject(Event.class);
                                    addedEvent.setId(id);
                                    eventArrayList.add(addedEvent);
                                    Log.i(TAG, "startAllEventChangesListener: эвент добавлен");
                                    break;
                                case REMOVED:
                                    for (Event event: eventArrayList){
                                        if (event.getId().equals(id)){
                                            eventArrayList.remove(event);
                                            break;
                                        }
                                    }
                                    Log.i(TAG, "startAllEventChangesListener: эвент изъят");
                                    break;
                            }
                        }

                        ArrayList<String> idStringArrayList = new ArrayList<>();
                        for (Event event: eventArrayList){
                            idStringArrayList.add(event.getId());
                        }
                        Log.d(TAG, "startEventAddListener: id eventArrayList = " + idStringArrayList.toString());
                        iEventGetterFireStoreView.onGetEvents(eventArrayList);
                    }
                });
    }

    public void getAllEventsFromFireBase() {
        Log.d(TAG, "getAllEventsFromFireBase()");

        iEventGetterFireStoreView.showLoading(true);
        eventArrayList.clear();
        App.db.collection("events")
                .whereGreaterThanOrEqualTo("date", DateParser.getDateWithMinusHours(new Date(), 12))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        //Получаем id документа
                        String id = documentSnapshot.getId();
                        Log.d(TAG, "getAllEventsFromFireBase: id эвента = " + id);
                        Event addedEvent = documentSnapshot.toObject(Event.class);
                        addedEvent.setId(id);
                        eventArrayList.add(addedEvent);
                        Log.i(TAG, "getAllEventsFromFireBase: эвент считан");
                    }
                    iEventGetterFireStoreView.onGetEvents(eventArrayList);
                })
                .addOnFailureListener(error -> {
                    String stringError = FirebaseErrorHandler.errorHandler(error);
                    Log.e(TAG, "getAllEventsFromFireBase: " + stringError);
                    iEventGetterFireStoreView.onGetError(stringError);
                })
                .addOnCompleteListener(task -> {
                    iEventGetterFireStoreView.showLoading(false);
                });
    }

    public void getEventById(String id){
        Log.d(TAG, "getEventById()");

        iEventGetterFireStoreView.showLoading(true);
        eventArrayList.clear();
        App.db.collection("events")
                .document(id)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.i(TAG, "getEventById: Получен эвент по id");
                    Event gottenEvent = documentSnapshot.toObject(Event.class);
                    gottenEvent.setId(id);
                    eventArrayList.add(gottenEvent);
                    iEventGetterFireStoreView.onGetEvents(eventArrayList);
                })
                .addOnFailureListener(error -> {
                    String stringError = FirebaseErrorHandler.errorHandler(error);
                    Log.e(TAG, "getEventById: " + stringError);
                    iEventGetterFireStoreView.onGetError(stringError);
                })
                .addOnCompleteListener(task -> {
                    iEventGetterFireStoreView.showLoading(false);
                });
    }

    public void endRegistrationListener() {
        listenerRegistration.remove();
    }
}
