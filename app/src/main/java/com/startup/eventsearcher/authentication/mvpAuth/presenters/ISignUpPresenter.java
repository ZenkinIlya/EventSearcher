package com.startup.eventsearcher.authentication.mvpAuth.presenters;

public interface ISignUpPresenter {
    void onRegistration(String email, String password, String confirmPassword);
}
