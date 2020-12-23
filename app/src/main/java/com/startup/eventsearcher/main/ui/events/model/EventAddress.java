package com.startup.eventsearcher.main.ui.events.model;

import java.io.Serializable;

public class EventAddress implements Serializable {

    private String address;
    private Double latitude;
    private Double longitude;

    public EventAddress(String address, Double latitude, Double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "EventAddress{" +
                "address='" + address + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
