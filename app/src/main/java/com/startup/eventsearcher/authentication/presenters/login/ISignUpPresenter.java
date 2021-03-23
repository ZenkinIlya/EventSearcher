package com.startup.eventsearcher.authentication.presenters.login;

public interface ISignUpPresenter {
    void onRegistration(String email, String password, String confirmPassword);
}
