package com.startup.eventsearcher.authentication.presenters.userData;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.startup.eventsearcher.authentication.utils.firebase.FirebaseErrorHandler;
import com.startup.eventsearcher.authentication.views.login.ISetExtraUserDataView;

import java.util.Objects;

public class UpdateUserDataPresenter implements IUpdateUserDataPresenter {

    private static final String TAG = "tgUpdateUserDataPres";

    private final ISetExtraUserDataView iSetExtraUserDataView;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser firebaseUser;

    public UpdateUserDataPresenter(ISetExtraUserDataView iSetExtraUserDataView) {
        this.iSetExtraUserDataView = iSetExtraUserDataView;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @Override
    public void updateDisplayNameAndPhoto(String displayName, Uri uriPhoto) {

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uriPhoto)
                .setDisplayName(displayName)
                .build();

        Objects.requireNonNull(firebaseUser).updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Авторизация прошла успешно
                        Log.i(TAG, "updateDisplayNameAndPhoto: success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d(TAG, "updateDisplayNameAndPhoto: firebaseUser DisplayName = " + user.getDisplayName() +
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
