package com.startup.eventsearcher.main.ui.events.filter;

public class Filter {

    //при сбросе: ""
    private String city;

    //при сбросе: 0
    private int startCountMembers;
    //при сбросе: -1 (-1 - нет ограничения)
    private int endCountMembers;

    //при сбросе: ""
    private String date;

    public Filter(String city, int startCountMembers, int endCountMembers, String date) {
        this.city = city;
        this.startCountMembers = startCountMembers;
        this.endCountMembers = endCountMembers;
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getStartCountMembers() {
        return startCountMembers;
    }

    public void setStartCountMembers(int startCountMembers) {
        this.startCountMembers = startCountMembers;
    }

    public int getEndCountMembers() {
        return endCountMembers;
    }

    public void setEndCountMembers(int endCountMembers) {
        this.endCountMembers = endCountMembers;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "city='" + city + '\'' +
                ", startCountMembers=" + startCountMembers +
                ", endCountMembers=" + endCountMembers +
                ", date='" + date + '\'' +
                '}';
    }
}
