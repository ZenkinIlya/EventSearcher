package com.startup.eventsearcher.authentication.mvpAuth.presenters.userData;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.startup.eventsearcher.authentication.mvpAuth.utils.firebase.FirebaseErrorHandler;
import com.startup.eventsearcher.authentication.mvpAuth.utils.firebase.TypeViewOutputError;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.ISetExtraUserDataView;

import java.util.Objects;

public class UpdateUserDataPresenter implements IUpdateUserDataPresenter {

    private static final String TAG = "tgUpdateUserData";
    private FirebaseAuth firebaseAuth;
    private ISetExtraUserDataView iSetExtraUserDataView;

    public UpdateUserDataPresenter(ISetExtraUserDataView iSetExtraUserDataView) {
        this.iSetExtraUserDataView = iSetExtraUserDataView;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void updateDisplayNameAndPhoto(String displayName, Uri uriPhoto) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uriPhoto)
                .setDisplayName(displayName)
                .build();

        Objects.requireNonNull(currentUser).updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Авторизация прошла успешно
                        Log.i(TAG, "updateDisplayNameAndPhoto: success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d(TAG, "updateDisplayNameAndPhoto: firebaseUser DisplayNam = " + user.getDisplayName() +
                                ", PhotoUrl = " + user.getPhotoUrl());
                        iSetExtraUserDataView.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    String message = FirebaseErrorHandler.errorHandler(e);
                    iSetExtraUserDataView.onError(message);
                });
    }

    @Override
    public void updateEmail(String email) {

    }

    @Override
    public void updatePassword(String password) {

    }
}
