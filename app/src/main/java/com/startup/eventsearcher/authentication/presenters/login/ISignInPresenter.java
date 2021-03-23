package com.startup.eventsearcher.authentication.presenters.login;

public interface ISignInPresenter {
    void isUserSignIn(String email, String password);
    void doesUserHaveLoginAndPhoto();
}
