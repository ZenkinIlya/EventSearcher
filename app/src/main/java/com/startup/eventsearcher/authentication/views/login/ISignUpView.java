package com.startup.eventsearcher.authentication.views.login;

public interface ISignUpView {
    void onEmailError(String message);
    void onPasswordError(String message);
    void onConfirmPassword(String message);
    void onSuccess();
    void onErrorFirebase(String message);
}
