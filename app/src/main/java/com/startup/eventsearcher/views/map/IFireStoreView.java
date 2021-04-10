package com.startup.eventsearcher.views.map;

import com.startup.eventsearcher.models.event.Event;

import java.util.ArrayList;

public interface IFireStoreView {
    void onGetEvents(ArrayList<Event> eventArrayList);
    void onGetError(String message);
    void showLoading(boolean show);
}
