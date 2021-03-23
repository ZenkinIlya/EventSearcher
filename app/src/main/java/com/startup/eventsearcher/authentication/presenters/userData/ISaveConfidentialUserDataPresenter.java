package com.startup.eventsearcher.authentication.presenters.userData;

public interface ISaveConfidentialUserDataPresenter {
    void onSetData(boolean saveData, String email, String password);
}
