package com.startup.eventsearcher.views.events.adapters;

import com.startup.eventsearcher.models.event.Event;
import com.startup.eventsearcher.models.event.Subscriber;

public interface EventRecyclerViewListener {
    void onEventClick(String eventId);
    void onSubscribe(Event event);
    void onMarkerClick(Event event);
    void onUnSubscribe(Event event, Subscriber subscriber);
}
