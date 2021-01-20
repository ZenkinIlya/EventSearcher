package com.startup.eventsearcher.main.ui.events.model;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.startup.eventsearcher.utils.JsonHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Objects;

//Список эвентов на все приложение
public class EventsList {

    private static final String TAG = "staticEventList";
    private static ArrayList<Event> eventArrayList = new ArrayList<>();

    public static ArrayList<Event> getEventArrayList() {
        return eventArrayList;
    }

    public static ArrayList<Event> getEventArrayListFromJSON(Context context){
        Type type = new TypeToken<ArrayList<Event>>(){}.getType();
        eventArrayList = JsonHandler.getSavedObjectFromPreference(
                context,
                "Events",
                "eventKey",
                type);
        if (eventArrayList == null){
            eventArrayList = new ArrayList<>();
        }
        Log.d(TAG, "getEventArrayListFromJSON " +context.getClass().getSimpleName()+
                " : eventArrayList = " + Objects.requireNonNull(eventArrayList).toString());
        return eventArrayList;
    }

    public static void saveEventArrayListInJSON(Context context){
        JsonHandler.saveObjectToSharedPreference(
                context,
                "Events",
                "eventKey",
                eventArrayList);
        Log.d(TAG, "saveEventArrayListInJSON " +context.getClass().getSimpleName()+
                " : eventArrayList = " + eventArrayList.toString());
    }

    public static void addEvent(Event event){
        eventArrayList.add(event);
    }
}
