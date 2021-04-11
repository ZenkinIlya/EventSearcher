package com.startup.eventsearcher.views.profile;

import android.graphics.Bitmap;

public interface IStoragePhotoView {
    void onSavePhotoInStorage();
    void onLoadPhotoFromStorage(Bitmap bitmap);
    void onError(String message);
    void showLoading(boolean show);
}
