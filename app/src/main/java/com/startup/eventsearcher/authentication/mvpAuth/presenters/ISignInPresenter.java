package com.startup.eventsearcher.authentication.mvpAuth.presenters;

public interface ISignInPresenter {
    void onLogin(String email, String password);
    void checkLogin();
}
