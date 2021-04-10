package com.startup.eventsearcher.presenters.userData;

public interface ISaveConfidentialUserDataPresenter {
    void onSetData(boolean saveData, String email, String password);
}
