package com.startup.eventsearcher.views.login;

public interface ISetExtraUserDataView {
    void onUpdateLoginAndPhoto();
    void onSavePhotoInStorage();
    void onErrorEmail(String message);
    void onErrorPassword(String message);
    void onErrorLogin(String message);
    void onError(String message);
    void showLoading(boolean show);
}
