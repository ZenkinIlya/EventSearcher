package com.startup.eventsearcher.main.ui.events.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyContent {

    public static final List<DummyItem> ITEMS = new ArrayList<DummyItem>();
    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    private static final int COUNT = 25;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), "Футбол " + position, "Спортивная",
                "6", "19:30");
    }

    public static class DummyItem {
        public final String id;
        public final String title;
        public final String address;
        public final String countPeople;
        public final String time;

        public DummyItem(String id, String title, String address, String countPeople, String time) {
            this.id = id;
            this.title = title;
            this.address = address;
            this.countPeople = countPeople;
            this.time = time;
        }
    }
}