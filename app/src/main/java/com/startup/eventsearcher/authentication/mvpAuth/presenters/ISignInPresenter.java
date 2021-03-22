package com.startup.eventsearcher.authentication.mvpAuth.presenters;

public interface ISignInPresenter {
    void isUserSignIn(String email, String password);
    void doesUserHaveLoginAndPhoto();
}
