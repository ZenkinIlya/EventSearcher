package com.startup.eventsearcher.authentication.mvpAuth.presenters;

public interface ISignUpPresenter {
    void onRegistration(String login, String email, String password, String confirmPassword);
}
