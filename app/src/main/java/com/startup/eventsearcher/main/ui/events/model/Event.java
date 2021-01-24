package com.startup.eventsearcher.main.ui.events.model;

import androidx.annotation.NonNull;

import com.startup.eventsearcher.main.ui.profile.model.Person;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Event implements Serializable {

    private final String header;
    private final String category;
    private final EventAddress eventAddress;
    private final String startDate;
    private final String startTime;
    private final Person personCreator;
    private final ArrayList<Subscriber> subscribers;
    private final String comment;

    public Event(String header, String category, EventAddress eventAddress, String startDate, String startTime,
                 Person personCreator, ArrayList<Subscriber> subscribers, String comment) {
        this.header = header;
        this.category = category;
        this.eventAddress = eventAddress;
        this.startDate = startDate;
        this.startTime = startTime;
        this.personCreator = personCreator;
        this.subscribers = subscribers;
        this.comment = comment;
    }

    public String getHeader() {
        return header;
    }

    public String getCategory() {
        return category;
    }

    public EventAddress getEventAddress() {
        return eventAddress;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public Person getPersonCreator() {
        return personCreator;
    }

    public ArrayList<Subscriber> getSubscribers() {
        return subscribers;
    }

    public String getComment() {
        return comment;
    }

    @NonNull
    @Override
    public String toString() {
        return "Event{" +
                "header='" + header + '\'' +
                ", category='" + category + '\'' +
                ", eventAddress=" + eventAddress +
                ", startDate='" + startDate + '\'' +
                ", startTime='" + startTime + '\'' +
                ", personCreator=" + personCreator +
                ", subscribers=" + subscribers +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(header, event.header) &&
                Objects.equals(category, event.category) &&
                Objects.equals(eventAddress, event.eventAddress) &&
                Objects.equals(startDate, event.startDate) &&
                Objects.equals(startTime, event.startTime) &&
                Objects.equals(personCreator, event.personCreator) &&
                Objects.equals(subscribers, event.subscribers) &&
                Objects.equals(comment, event.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, category, eventAddress, startDate, startTime, personCreator, subscribers, comment);
    }

    public String getDateFormatDay(SimpleDateFormat simpleDateFormat) {
        String defaultResult = "-";
        try {
            Date date = simpleDateFormat.parse(this.getStartDate());
            if (date == null){
                return defaultResult;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return defaultResult;
    }

    public String getDateFormatMonth(SimpleDateFormat simpleDateFormat) {
        String defaultResult = "-";
        try {
            Date date = simpleDateFormat.parse(this.getStartDate());
            if (date == null){
                return defaultResult;
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return String.valueOf(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return defaultResult;
    }
}
