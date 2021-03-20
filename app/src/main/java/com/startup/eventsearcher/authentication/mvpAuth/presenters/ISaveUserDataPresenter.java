package com.startup.eventsearcher.authentication.mvpAuth.presenters;

public interface ISaveUserDataPresenter {
    void onSetData(boolean saveData, String login, String email, String password,
                   String firstName, String secondName);
}
