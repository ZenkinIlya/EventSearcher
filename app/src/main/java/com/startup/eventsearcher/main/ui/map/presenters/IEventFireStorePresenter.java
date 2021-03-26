package com.startup.eventsearcher.main.ui.map.presenters;

import com.startup.eventsearcher.main.ui.events.model.Event;

import java.util.ArrayList;

public interface IEventFireStorePresenter {
    void startEventsListener(ArrayList<Event> eventArrayList);
}
