package com.startup.eventsearcher.authentication.mvpAuth.views.login;

public interface ISignInView {
    void onEmailError(String message);
    void onPasswordError(String message);
    void onSuccess();
    void onErrorFirebase(String message);
    void onErrorVerification();
    void isLogin(boolean isLogin);
}
