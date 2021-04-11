package com.startup.eventsearcher.views.login;

public interface ISetExtraUserDataView {
    void onSetLogin();
    void onSetUriPhoto();
    void onErrorLogin(String message);
    void onError(String message);
    void showLoading(boolean show);
}
