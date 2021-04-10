package com.startup.eventsearcher.presenters.firestore;

public interface IEventFireStorePresenter {
    void startAllEventChangesListener();
    void startEventAddListener();

    void endRegistrationListener();

    void getAllEventsFromFireBase();
}
