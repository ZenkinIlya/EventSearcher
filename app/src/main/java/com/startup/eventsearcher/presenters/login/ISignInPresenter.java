package com.startup.eventsearcher.presenters.login;

public interface ISignInPresenter {
    void isUserSignIn(String email, String password);
    void doesUserHaveLoginAndPhoto();
}
