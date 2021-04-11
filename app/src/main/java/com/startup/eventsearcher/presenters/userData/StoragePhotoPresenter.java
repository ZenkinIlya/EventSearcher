package com.startup.eventsearcher.presenters.userData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.startup.eventsearcher.utils.firebase.FirebaseErrorHandler;
import com.startup.eventsearcher.views.profile.IStoragePhotoView;

import java.io.File;
import java.io.IOException;

public class StoragePhotoPresenter {

    private static final String TAG = "tgStoragePhotoPres";

    private IStoragePhotoView iStoragePhotoView;
    private final FirebaseAuth firebaseAuth;
    private final FirebaseUser firebaseUser;
    private final StorageReference firebaseStorageRef;

    public StoragePhotoPresenter(IStoragePhotoView iStoragePhotoView) {
        this.iStoragePhotoView = iStoragePhotoView;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseStorageRef = FirebaseStorage.getInstance().getReference();
    }

    public void savePhotoInStorage(byte[] bytes){
        Log.d(TAG, "savePhotoInStorage: Сохранение аватарки пользователя с uri = " + firebaseUser.getUid());

        if (bytes == null){
            Log.w(TAG, "savePhotoInStorage: uriPhoto = " + bytes);
            iStoragePhotoView.onSavePhotoInStorage();
            return;
        }

        iStoragePhotoView.showLoading(true);
        StorageReference storageReferencePhoto = firebaseStorageRef.child("images/users/" + firebaseUser.getUid() + "/avatar.jpg");
        storageReferencePhoto
                .putBytes(bytes)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.i(TAG, "savePhotoInStorage: success");
                    iStoragePhotoView.showLoading(false);
                    iStoragePhotoView.onSavePhotoInStorage();
                })
                .addOnFailureListener(e -> {
                    String message = FirebaseErrorHandler.errorHandler(e);
                    Log.e(TAG, "savePhotoInStorage: " + message);
                    iStoragePhotoView.showLoading(false);
                    iStoragePhotoView.onError(message);
                });
    }

    public void loadPhotoFromStorage(String uidUser, Bitmap defaultBitmap){
        Log.d(TAG, "loadPhotoFromStorage: Загрузка аватарки пользователя с uid = " + uidUser);

        iStoragePhotoView.showLoading(true);
        StorageReference storageReferencePhoto = firebaseStorageRef.child("images/users/" + uidUser + "/avatar.jpg");
        try {
            final File file = File.createTempFile("avatar", "jpg");
            storageReferencePhoto.getFile(file)
                    .addOnCompleteListener(task -> {
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                        file.delete();
                        Log.i(TAG, "loadPhotoFromStorage: success");
                        iStoragePhotoView.showLoading(false);
                        iStoragePhotoView.onLoadPhotoFromStorage(bitmap);

                    })
                    .addOnFailureListener(e -> {
                        String message = FirebaseErrorHandler.errorHandler(e);
                        Log.e(TAG, "loadPhotoFromStorage: " + message);
                        iStoragePhotoView.showLoading(false);
                        iStoragePhotoView.onError(message);
                        iStoragePhotoView.onLoadPhotoFromStorage(defaultBitmap);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
