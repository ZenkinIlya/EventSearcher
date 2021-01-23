package com.startup.eventsearcher.main.ui.events.filter;

public class Filter {

    //при сбросе: ""
    private String city;

    //при сбросе: 0
    private int startCountMembers;
    //при сбросе: -1 (-1 - нет ограничения)
    private int endCountMembers;

    //при сбросе: ""
    private int lastSelectedYear;
    private int lastSelectedMonth;
    private int lastSelectedDayOfMonth;

    public Filter(String city, int startCountMembers, int endCountMembers,
                  int lastSelectedYear, int lastSelectedMonth, int lastSelectedDayOfMonth) {
        this.city = city;
        this.startCountMembers = startCountMembers;
        this.endCountMembers = endCountMembers;
        this.lastSelectedYear = lastSelectedYear;
        this.lastSelectedMonth = lastSelectedMonth;
        this.lastSelectedDayOfMonth = lastSelectedDayOfMonth;
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

    public int getLastSelectedYear() {
        return lastSelectedYear;
    }

    public void setLastSelectedYear(int lastSelectedYear) {
        this.lastSelectedYear = lastSelectedYear;
    }

    public int getLastSelectedMonth() {
        return lastSelectedMonth;
    }

    public void setLastSelectedMonth(int lastSelectedMonth) {
        this.lastSelectedMonth = lastSelectedMonth;
    }

    public int getLastSelectedDayOfMonth() {
        return lastSelectedDayOfMonth;
    }

    public void setLastSelectedDayOfMonth(int lastSelectedDayOfMonth) {
        this.lastSelectedDayOfMonth = lastSelectedDayOfMonth;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "city='" + city + '\'' +
                ", startCountMembers=" + startCountMembers +
                ", endCountMembers=" + endCountMembers +
                ", lastSelectedYear=" + lastSelectedYear +
                ", lastSelectedMonth=" + lastSelectedMonth +
                ", lastSelectedDayOfMonth=" + lastSelectedDayOfMonth +
                '}';
    }
}
