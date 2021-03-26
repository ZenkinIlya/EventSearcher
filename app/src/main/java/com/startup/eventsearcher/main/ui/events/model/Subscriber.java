package com.startup.eventsearcher.main.ui.events.model;

import androidx.annotation.NonNull;

import com.startup.eventsearcher.main.ui.profile.model.Person;

import java.io.Serializable;
import java.util.Objects;

public class Subscriber implements Serializable {

    private Person person;
    private ExtraDate extraDate;

    public Subscriber(){}

    public Subscriber(Person person, ExtraDate extraDate) {
        this.person = person;
        this.extraDate = extraDate;
    }

    public Person getPerson() {
        return person;
    }

    public ExtraDate getExtraDate() {
        return extraDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "Subscriber{" +
                "person=" + person +
                ", extraDate=" + extraDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscriber that = (Subscriber) o;
        return Objects.equals(person, that.person) &&
                Objects.equals(extraDate, that.extraDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(person, extraDate);
    }
}
