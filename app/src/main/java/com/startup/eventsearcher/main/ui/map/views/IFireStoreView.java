package com.startup.eventsearcher.main.ui.map.views;

import com.startup.eventsearcher.main.ui.events.model.Event;

import java.util.ArrayList;

public interface IFireStoreView {
    void onGetEvents(ArrayList<Event> eventArrayList);
    void onGetError(String message);
    void showLoading(boolean show);
}
