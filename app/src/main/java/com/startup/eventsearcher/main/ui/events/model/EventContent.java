package com.startup.eventsearcher.main.ui.events.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventContent {

    public static final List<EventItem> ITEMS = new ArrayList<>();
    public static final Map<String, EventItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = 7;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(EventItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static EventItem createDummyItem(int position) {
        return new EventItem(String.valueOf(position), "Футбол " + position, "Спортивная",
                "6", "19:30");
    }

    public static class EventItem {
        public final String id;
        public final String title;
        public final String address;
        public final String countPeople;
        public final String time;

        public EventItem(String id, String title, String address, String countPeople, String time) {
            this.id = id;
            this.title = title;
            this.address = address;
            this.countPeople = countPeople;
            this.time = time;
        }
    }
}