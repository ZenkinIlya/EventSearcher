package com.startup.eventsearcher.authentication.mvpAuth.presenters.userData;

import android.net.Uri;

public interface IUpdateUserDataPresenter {
    void updateDisplayNameAndPhoto(String displayName, Uri uriPhoto);
    void updateEmail(String email);
    void updatePassword(String password);
}
