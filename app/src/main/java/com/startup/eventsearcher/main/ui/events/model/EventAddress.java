package com.startup.eventsearcher.main.ui.events.model;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Objects;

public class EventAddress implements Serializable {

    private final String address;
    private final String city;
    private final String street;
    private final String house;
    private final Double latitude;
    private final Double longitude;

    public EventAddress(String address, String city, String street, String house, Double latitude, Double longitude) {
        this.address = address;
        this.city = city;
        this.street = street;
        this.house = house;
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

    public String getCity() {
        return city;
    }

    public String getStreet() {
        return street;
    }

    public String getHouse() {
        return house;
    }

    @NonNull
    @Override
    public String toString() {
        return "EventAddress{" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", house='" + house + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventAddress that = (EventAddress) o;
        return Objects.equals(address, that.address) &&
                Objects.equals(city, that.city) &&
                Objects.equals(street, that.street) &&
                Objects.equals(house, that.house) &&
                Objects.equals(latitude, that.latitude) &&
                Objects.equals(longitude, that.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, city, street, house, latitude, longitude);
    }
}
