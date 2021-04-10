package com.startup.eventsearcher.main.ui.events.adapters;

import com.startup.eventsearcher.main.ui.events.model.Event;

public interface EventRecyclerViewListener {
    void onEventClick(Event event);
    void onSubscribe(Event event);
    void onMarkerClick(Event event);
}
