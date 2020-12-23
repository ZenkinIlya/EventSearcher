package com.startup.eventsearcher.main.ui.events.model;

import com.startup.eventsearcher.main.ui.profile.model.Person;

import java.io.Serializable;
import java.util.HashMap;

public class Event implements Serializable {

    private String header;
    private String category;
    private EventAddress eventAddress;
    private String startTime;
    private Person personCreator;
    private HashMap<Person, ExtraDate> hashMapSubscribersPerson;
    private String comment;

    public Event(String header, String category, EventAddress eventAddress, String startTime,
                 Person personCreator, HashMap<Person, ExtraDate> hashMapSubscribersPerson, String comment) {
        this.header = header;
        this.category = category;
        this.eventAddress = eventAddress;
        this.startTime = startTime;
        this.personCreator = personCreator;
        this.hashMapSubscribersPerson = hashMapSubscribersPerson;
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

    public HashMap<Person, ExtraDate> getHashMapSubscribersPerson() {
        return hashMapSubscribersPerson;
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
                ", hashMapSubscribersPerson=" + hashMapSubscribersPerson +
                ", comment='" + comment + '\'' +
                '}';
    }
}
