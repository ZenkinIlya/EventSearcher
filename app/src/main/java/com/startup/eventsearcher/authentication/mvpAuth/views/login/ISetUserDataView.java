package com.startup.eventsearcher.authentication.mvpAuth.views.login;

import com.startup.eventsearcher.authentication.mvpAuth.models.user.User;

public interface ISetUserDataView {
    void onSetEmail(String email);
    void onSetPassword(String password);
    void onGetUserDataFromSharedPreferenceSuccess(User user);
}
