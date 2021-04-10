package com.startup.eventsearcher.utils.filter;

import com.startup.eventsearcher.models.event.Event;
import com.startup.eventsearcher.utils.DateParser;

import java.util.ArrayList;
import java.util.Date;

public class EventsFilter {

    public static ArrayList<Event> filterEventsByStartTime(ArrayList<Event> eventArrayList, boolean mapChipFutureEventsChecked,
                                                           boolean mapChipStartsEventsChecked, boolean mapChipStartsEventsRecentlyChecked){

        //Если все Chips !checked то возвращаем получаемый список - исходный список, считанный из FireStore
        if (!mapChipFutureEventsChecked && !mapChipStartsEventsChecked && !mapChipStartsEventsRecentlyChecked){
            return eventArrayList;
        }

        //Если все Chips checked то возвращаем получаемый список - исходный список, считанный из FireStore
        if (mapChipFutureEventsChecked && mapChipStartsEventsChecked && mapChipStartsEventsRecentlyChecked){
            return eventArrayList;
        }

        Date dateNow =new Date();
        ArrayList<Event> resultEventArrayList = new ArrayList<>();

        for (Event event: eventArrayList){
            Date date = event.getDate();

            if (mapChipFutureEventsChecked && date.after(dateNow)){
                resultEventArrayList.add(event);
                continue;
            }

            if (mapChipStartsEventsChecked && date.after(DateParser.getDateWithMinusHours(dateNow, 2))
                    && date.before(dateNow)){
                resultEventArrayList.add(event);
                continue;
            }

            if (mapChipStartsEventsRecentlyChecked && date.after(DateParser.getDateWithMinusHours(dateNow, 12))
                    && date.before(DateParser.getDateWithMinusHours(dateNow, 2))){
                resultEventArrayList.add(event);
            }
        }

        return resultEventArrayList;
    }
}
