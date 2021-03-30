package com.startup.eventsearcher.main.ui.map.presenters;

public interface IEventFireStorePresenter {
    void startAllEventChangesListener();
    void startEventAddListener();

    void endRegistrationListener();
}
