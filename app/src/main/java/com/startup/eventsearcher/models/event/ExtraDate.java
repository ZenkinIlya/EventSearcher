package com.startup.eventsearcher.models.event;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class ExtraDate implements Serializable {

    private Date arrivalTime;
    private String comment;

    public ExtraDate(){}

    public ExtraDate(Date arrivalTime, String comment) {
        this.arrivalTime = arrivalTime;
        this.comment = comment;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "ExtraDate{" +
                "arrivalTime=" + arrivalTime +
                ", comment='" + comment + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExtraDate extraDate = (ExtraDate) o;
        return Objects.equals(arrivalTime, extraDate.arrivalTime) &&
                Objects.equals(comment, extraDate.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(arrivalTime, comment);
    }
}
