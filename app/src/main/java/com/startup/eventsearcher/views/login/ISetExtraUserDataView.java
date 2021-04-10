package com.startup.eventsearcher.views.login;

public interface ISetExtraUserDataView {
    void onSuccess();
    void onErrorEmail(String message);
    void onErrorPassword(String message);
    void onErrorLogin(String message);
    void onError(String message);
    void showLoading(boolean show);
}
