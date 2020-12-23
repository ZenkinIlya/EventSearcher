package com.startup.eventsearcher.main.ui.profile.model;

public class CurrentPerson {
    private static Person person;

    public static Person getPerson() {
        return person;
    }

    public static void setPerson(Person person) {
        CurrentPerson.person = person;
    }
}
