package com.startup.eventsearcher.presenters.userData;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.startup.eventsearcher.utils.firebase.FirebaseErrorHandler;
import com.startup.eventsearcher.utils.user.IUserDataVerification;
import com.startup.eventsearcher.utils.user.UserDataVerification;
import com.startup.eventsearcher.views.login.ISetExtraUserDataView;

import java.util.Objects;

public class UpdateUserDataPresenter implements IUpdateUserDataPresenter {

    private static final String TAG = "tgUpdateUserDataPres";

    private final ISetExtraUserDataView iSetExtraUserDataView;
    private final IUserDataVerification iUserDataVerification;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser firebaseUser;

    public UpdateUserDataPresenter(ISetExtraUserDataView iSetExtraUserDataView,
                                   UserDataVerification userDataVerification) {
        this.iSetExtraUserDataView = iSetExtraUserDataView;
        this.iUserDataVerification = userDataVerification;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    //Проверка корректности данных
    private boolean verificationLogin(String login){
        Log.d(TAG, "verificationLogin: Проверка корректности логина");
        iUserDataVerification.setDataCorrect(true);

        String verificationLogin = iUserDataVerification.verificationLogin(login);
        iSetExtraUserDataView.onErrorLogin(verificationLogin);

        if (!iUserDataVerification.isDataCorrect()) {
            Log.w(TAG, "verificationData: Логин не корректен");
            return false;
        }else {
            Log.i(TAG, "verificationData: Логин корректен");
            return true;
        }
    }
    
    @Override
    public void updateDisplayNameAndPhoto(String displayName, Uri uriPhoto) {
        if (verificationLogin(displayName)){
            iSetExtraUserDataView.showLoading(true);
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
                            iSetExtraUserDataView.showLoading(false);
                            iSetExtraUserDataView.onSuccess();
                        }
                    })
                    .addOnFailureListener(e -> {
                        String message = FirebaseErrorHandler.errorHandler(e);
                        iSetExtraUserDataView.showLoading(false);
                        iSetExtraUserDataView.onError(message);
                    });
        }
    }

    @Override
    public void updateEmail(String email) {

    }

    @Override
    public void updatePassword(String password) {

    }
}
