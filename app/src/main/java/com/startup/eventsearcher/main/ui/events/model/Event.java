package com.startup.eventsearcher.main.ui.events.model;

import com.google.firebase.firestore.Exclude;
import com.startup.eventsearcher.authentication.models.user.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class Event implements Serializable {

    private String id;

    private String header;
    private String category;
    private EventAddress eventAddress;
    private Date date;
    private User user;
    private ArrayList<Subscriber> subscribers;
    private String comment;

    public Event() {
    }

    public Event(String header, String category, EventAddress eventAddress,
                 Date date, User user, ArrayList<Subscriber> subscribers, String comment) {
        this.header = header;
        this.category = category;
        this.eventAddress = eventAddress;
        this.date = date;
        this.user = user;
        this.subscribers = subscribers;
        this.comment = comment;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Date getDate(){
        return date;
    }

    public User getUser() {
        return user;
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
                "id='" + id + '\'' +
                ", header='" + header + '\'' +
                ", category='" + category + '\'' +
                ", eventAddress=" + eventAddress +
                ", date=" + date +
                ", user=" + user +
                ", subscribers=" + subscribers +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(id, event.id) &&
                Objects.equals(header, event.header) &&
                Objects.equals(category, event.category) &&
                Objects.equals(eventAddress, event.eventAddress) &&
                Objects.equals(date, event.date) &&
                Objects.equals(user, event.user) &&
                Objects.equals(subscribers, event.subscribers) &&
                Objects.equals(comment, event.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, header, category, eventAddress, date, user, subscribers, comment);
    }

}
