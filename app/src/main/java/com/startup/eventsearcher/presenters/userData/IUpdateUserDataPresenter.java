package com.startup.eventsearcher.presenters.userData;

import android.net.Uri;

public interface IUpdateUserDataPresenter {
    void updateDisplayNameAndPhoto(String displayName, Uri uriPhoto);
    void updateEmail(String email);
    void updatePassword(String password);
}
