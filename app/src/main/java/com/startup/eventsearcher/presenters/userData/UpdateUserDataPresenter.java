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

public class UpdateUserDataPresenter implements IUpdateUserDataPresenter {

    private static final String TAG = "tgUpdateUserDataPres";

    private final ISetExtraUserDataView iSetExtraUserDataView;
    private final IUserDataVerification iUserDataVerification;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser firebaseUser;
    private final StorageReference firebaseStorageRef;

    public UpdateUserDataPresenter(ISetExtraUserDataView iSetExtraUserDataView,
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

    @Override
    public void savePhotoInStorage(Uri uriPhoto){
        Log.d(TAG, "savePhotoInStorage: Сохранение аватарки пользователя с uri = " + firebaseUser.getUid());

        if (uriPhoto == null){
            Log.w(TAG, "savePhotoInStorage: uriPhoto = " + uriPhoto);
            iSetExtraUserDataView.showLoading(false);
            iSetExtraUserDataView.onSavePhotoInStorage();
            return;
        }

        iSetExtraUserDataView.showLoading(true);
        StorageReference storageReferencePhoto = firebaseStorageRef.child("images/users/" + firebaseUser.getUid() + "/avatar.jpg");
        storageReferencePhoto.putFile(uriPhoto)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.i(TAG, "savePhotoInStorage: success");
                    iSetExtraUserDataView.showLoading(false);
                    iSetExtraUserDataView.onSavePhotoInStorage();
                })
                .addOnFailureListener(e -> {
                    String message = FirebaseErrorHandler.errorHandler(e);
                    Log.e(TAG, "savePhotoInStorage: error = " + message);
                    iSetExtraUserDataView.showLoading(false);
                    iSetExtraUserDataView.onError(message);
                });
    }

    @Override
    public void updateDisplayNameAndPhoto(String displayName, Uri uriPhoto) {
        Log.d(TAG, "updateDisplayNameAndPhoto: Сохранение логина и аватарки по uri в firebaseUser");
        if (verificationLogin(displayName)){
            iSetExtraUserDataView.showLoading(true);

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uriPhoto)
                    .setDisplayName(displayName)
                    .build();

            Objects.requireNonNull(firebaseUser).updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "updateDisplayNameAndPhoto: success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d(TAG, "updateDisplayNameAndPhoto: firebaseUser DisplayName = " + user.getDisplayName() +
                                    ", PhotoUrl = " + user.getPhotoUrl());
                            iSetExtraUserDataView.showLoading(false);
                            iSetExtraUserDataView.onUpdateLoginAndPhoto();
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

    @Override
    public void updateEmail(String email) {

    }

    @Override
    public void updatePassword(String password) {

    }
}
