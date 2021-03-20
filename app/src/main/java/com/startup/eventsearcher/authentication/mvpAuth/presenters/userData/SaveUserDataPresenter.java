package com.startup.eventsearcher.authentication.mvpAuth.presenters.userData;

import android.util.Log;

import com.startup.eventsearcher.authentication.mvpAuth.utils.user.ICurrentUser;
import com.startup.eventsearcher.authentication.mvpAuth.models.user.User;

public class SaveUserDataPresenter implements ISaveUserDataPresenter {

    private static final String TAG = "tgSaveDataPresenter";
    private final ICurrentUser iCurrentUser;

    public SaveUserDataPresenter(ICurrentUser iCurrentUser) {
        this.iCurrentUser = iCurrentUser;
    }

    @Override
    public void onSetData(boolean saveData, String login, String email, String password, String firstName, String secondName) {
        User user = new User();
        Log.d(TAG, "onSetData: email = " + email + ", password = " + password +
                ", login = " + login + ", firstName = " + firstName + ", secondName = " + secondName);
        user.setEmail(email);
        user.setPassword(password);
        user.setLogin(login);
        user.setName(firstName);
        user.setSurname(secondName);
        Log.d(TAG, "onSetData: user = " +user.toString());
        if (saveData){
            iCurrentUser.saveCurrentUserToJSON(user);
        }
    }
}
