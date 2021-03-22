package com.startup.eventsearcher.authentication.mvpAuth.presenters.introduction;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.startup.eventsearcher.authentication.mvpAuth.views.introduction.IIntroductionView;

public class IntroductionPresenter implements IIntroductionPresenter{

    private static final String TAG = "tgIntroductionPres";
    private final FirebaseUser firebaseUser;
    private final IIntroductionView iIntroductionView;

    public IntroductionPresenter(IIntroductionView iIntroductionView) {
        this.iIntroductionView = iIntroductionView;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public void isUserLoginInFirebase() {
        if (firebaseUser != null){
            Log.i(TAG, "isUserLoginInFirebase: Пользователь авторизован");
            Log.d(TAG, "isUserLoginInFirebase: firebaseUser:");
            Log.d(TAG, "isUserLoginInFirebase: Uid = " + firebaseUser.getUid());
            Log.d(TAG, "isUserLoginInFirebase: DisplayName = " + firebaseUser.getDisplayName());
            Log.d(TAG, "isUserLoginInFirebase: Email = " + firebaseUser.getEmail());
            Log.d(TAG, "isUserLoginInFirebase: PhotoUrl = " + firebaseUser.getPhotoUrl());

            iIntroductionView.onCheckLoginInFirebase(true);
        }else {
            Log.w(TAG, "isUserLoginInFirebase: Пользователь не авторизован");
            iIntroductionView.onCheckLoginInFirebase(false);
        }
    }

    @Override
    public void doesUserHaveLoginAndPhoto() {
        if (firebaseUser.getDisplayName() != null &&
                !firebaseUser.getDisplayName().isEmpty() &&
                firebaseUser.getPhotoUrl() != null){
            Log.i(TAG, "doesUserHaveLoginAndPhoto: Пользователь имеет логин и фото");
            iIntroductionView.onCheckUserHaveLoginAndPhoto(true);
        }else {
            Log.w(TAG, "doesUserHaveLoginAndPhoto: Пользователь не имеет логин и фото");
            iIntroductionView.onCheckUserHaveLoginAndPhoto(false);
        }
    }
}
