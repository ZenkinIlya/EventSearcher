package com.startup.eventsearcher.authentication.views.login;

public interface ISetExtraUserDataView {
    void onSuccess();
    void onErrorEmail(String message);
    void onErrorPassword(String message);
    void onError(String message);
}
