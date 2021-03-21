package com.startup.eventsearcher.authentication.mvpAuth.presenters.userData;

import android.net.Uri;

public interface ISaveUserDataPresenter {
    void onSetData(boolean saveData, String uid, String email, String password, String login,
                   Uri uriPhoto);
}
