package com.startup.eventsearcher.main.ui.events.model;

import java.io.Serializable;
import java.util.Objects;

public class ExtraDate implements Serializable {

    private String arrivalTime;
    private String comment;

    public ExtraDate(String arrivalTime, String comment) {
        this.arrivalTime = arrivalTime;
        this.comment = comment;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "ExtraDate{" +
                "arrivalTime='" + arrivalTime + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraDate extraDate = (ExtraDate) o;
        return arrivalTime.equals(extraDate.arrivalTime) &&
                comment.equals(extraDate.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arrivalTime, comment);
    }
}
