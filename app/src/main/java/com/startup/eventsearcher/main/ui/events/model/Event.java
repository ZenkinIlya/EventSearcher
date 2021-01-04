package com.startup.eventsearcher.main.ui.events.model;

import com.startup.eventsearcher.main.ui.profile.model.Person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Event implements Serializable {

    private String header;
    private String category;
    private EventAddress eventAddress;
    private String startTime;
    private Person personCreator;
    private ArrayList<Subscriber> subscribers;
    private String comment;

    public Event(String header, String category, EventAddress eventAddress, String startTime,
                 Person personCreator, ArrayList<Subscriber> subscribers, String comment) {
        this.header = header;
        this.category = category;
        this.eventAddress = eventAddress;
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

    @Override
    public String toString() {
        return "Event{" +
                "header='" + header + '\'' +
                ", category='" + category + '\'' +
                ", eventAddress=" + eventAddress +
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
                Objects.equals(startTime, event.startTime) &&
                Objects.equals(personCreator, event.personCreator) &&
                Objects.equals(subscribers, event.subscribers) &&
                Objects.equals(comment, event.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(header, category, eventAddress, startTime, personCreator, subscribers, comment);
    }
}
