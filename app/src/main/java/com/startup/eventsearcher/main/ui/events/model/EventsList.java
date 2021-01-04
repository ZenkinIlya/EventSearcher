package com.startup.eventsearcher.main.ui.events.model;

import java.util.ArrayList;

//Список эвентов на все приложение
public class EventsList {

    private static ArrayList<Event> eventArrayList = new ArrayList<>();

    public static ArrayList<Event> getEventArrayList() {
        return eventArrayList;
    }

    public static void setEventArrayList(ArrayList<Event> eventArrayList) {
        EventsList.eventArrayList = eventArrayList;
    }

    public static void addEvent(Event event){
        eventArrayList.add(event);
    }
}
