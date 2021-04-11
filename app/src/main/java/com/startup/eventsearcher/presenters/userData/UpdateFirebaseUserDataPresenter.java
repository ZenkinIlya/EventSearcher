package com.startup.eventsearcher.presenters.userData;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.startup.eventsearcher.utils.firebase.FirebaseErrorHandler;
import com.startup.eventsearcher.utils.user.IUserDataVerification;
import com.startup.eventsearcher.utils.user.UserDataVerification;
import com.startup.eventsearcher.views.login.ISetExtraUserDataView;

import java.util.Objects;

public class UpdateFirebaseUserDataPresenter {

    private static final String TAG = "tgUpdateUserDataPres";

    private final ISetExtraUserDataView iSetExtraUserDataView;
    private final IUserDataVerification iUserDataVerification;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser firebaseUser;
    private final StorageReference firebaseStorageRef;

    public UpdateFirebaseUserDataPresenter(ISetExtraUserDataView iSetExtraUserDataView,
                                           UserDataVerification userDataVerification) {
        this.iSetExtraUserDataView = iSetExtraUserDataView;
        this.iUserDataVerification = userDataVerification;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorageRef = FirebaseStorage.getInstance().getReference();
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

    public void setLoginInFirebaseAuth(String displayName) {
        Log.d(TAG, "setLoginInFirebaseAuth: Сохранение логина и аватарки по uri в firebaseUser");

        if (verificationLogin(displayName)){
            iSetExtraUserDataView.showLoading(true);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();

            Objects.requireNonNull(firebaseUser).updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "setLoginInFirebaseAuth: success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d(TAG, "setLoginInFirebaseAuth: firebaseUser DisplayName = " + user.getDisplayName());
                            iSetExtraUserDataView.showLoading(false);
                            iSetExtraUserDataView.onSetLogin();
                        }
                    })
                    .addOnFailureListener(e -> {
                        String message = FirebaseErrorHandler.errorHandler(e);
                        Log.e(TAG, "savePhotoInStorage: error = " + message);
                        iSetExtraUserDataView.showLoading(false);
                        iSetExtraUserDataView.onError(message);
                    });
        }
    }

    public void setUriPhotoInFirebaseAuth(Uri uriPhoto) {
        Log.d(TAG, "setUriPhotoInFirebaseAuth: Сохранение аватарки по uri в firebaseUser");

            iSetExtraUserDataView.showLoading(true);
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uriPhoto)
                    .build();

            Objects.requireNonNull(firebaseUser).updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "setUriPhotoInFirebaseAuth: success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d(TAG, "setUriPhotoInFirebaseAuth: firebaseUser: photoUrl = " + user.getPhotoUrl());
                            iSetExtraUserDataView.showLoading(false);
                            iSetExtraUserDataView.onSetUriPhoto();
                        }
                    })
                    .addOnFailureListener(e -> {
                        String message = FirebaseErrorHandler.errorHandler(e);
                        Log.e(TAG, "setUriPhotoInFirebaseAuth: error = " + message);
                        iSetExtraUserDataView.showLoading(false);
                        iSetExtraUserDataView.onError(message);
                    });
    }
}
