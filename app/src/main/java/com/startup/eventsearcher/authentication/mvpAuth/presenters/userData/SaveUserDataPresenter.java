package com.startup.eventsearcher.authentication.mvpAuth.presenters.userData;

import android.net.Uri;
import android.util.Log;

import com.startup.eventsearcher.authentication.mvpAuth.models.user.ConfidentialUserData;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.ICurrentUser;
import com.startup.eventsearcher.authentication.mvpAuth.models.user.User;

public class SaveUserDataPresenter implements ISaveUserDataPresenter {

    private static final String TAG = "tgSaveDataPresenter";
    private final ICurrentUser iCurrentUser;

    public SaveUserDataPresenter(ICurrentUser iCurrentUser) {
        this.iCurrentUser = iCurrentUser;
    }


    @Override
    public void onSetData(boolean saveData, String uid, String email, String password, String login, String uriPhoto) {
        User user = new User();
        Log.d(TAG, "onSetData: uid = " + uid +
                ", email = " + email +
                ", password = " + password +
                ", login = " + login +
                ", uriPhoto = " + uriPhoto);
        user.setUid(uid);
        user.setConfidentialUserData(new ConfidentialUserData(email, password));
        user.setLogin(login);
        user.setUriPhoto(uriPhoto);
        Log.d(TAG, "onSetData: user = " +user.toString());
        if (saveData){
            Log.d(TAG, "onSetData: Сохранение данных пользователя в SharedPreference");
            iCurrentUser.saveCurrentUserToJSON(user);
        }
    }
}
