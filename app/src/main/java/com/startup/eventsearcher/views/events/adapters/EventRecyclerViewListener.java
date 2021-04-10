package com.startup.eventsearcher.views.events.adapters;

import com.startup.eventsearcher.models.event.Event;

public interface EventRecyclerViewListener {
    void onEventClick(Event event);
    void onSubscribe(Event event);
    void onMarkerClick(Event event);
}
