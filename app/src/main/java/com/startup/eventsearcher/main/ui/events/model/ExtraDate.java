package com.startup.eventsearcher.main.ui.events.model;

import java.io.Serializable;

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
}
