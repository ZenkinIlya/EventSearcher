package com.startup.eventsearcher.views.login;

public interface ISignInView {
    void onEmailError(String message);
    void onPasswordError(String message);
    void onSuccess();
    void onErrorFirebase(String message);
    void onCheckUserHasLogin(boolean userHasLogin);
}
