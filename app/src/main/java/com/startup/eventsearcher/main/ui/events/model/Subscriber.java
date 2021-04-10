package com.startup.eventsearcher.main.ui.events.model;

import androidx.annotation.NonNull;

import com.startup.eventsearcher.authentication.models.user.User;

import java.io.Serializable;
import java.util.Objects;

public class Subscriber implements Serializable {

    private User user;
    private ExtraDate extraDate;

    public Subscriber(){}

    public Subscriber(User user, ExtraDate extraDate) {
        this.user = user;
        this.extraDate = extraDate;
    }

    public User getUser() {
        return user;
    }

    public ExtraDate getExtraDate() {
        return extraDate;
    }

    @NonNull
    @Override
    public String toString() {
        return "Subscriber{" +
                "user=" + user +
                ", extraDate=" + extraDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscriber that = (Subscriber) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(extraDate, that.extraDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, extraDate);
    }
}
