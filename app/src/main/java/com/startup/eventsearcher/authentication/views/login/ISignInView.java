package com.startup.eventsearcher.authentication.views.login;

public interface ISignInView {
    void onEmailError(String message);
    void onPasswordError(String message);
    void onSuccess();
    void onErrorFirebase(String message);
    void onCheckUserHaveLoginAndPhoto(boolean userHaveLoginAndPhoto);
}
