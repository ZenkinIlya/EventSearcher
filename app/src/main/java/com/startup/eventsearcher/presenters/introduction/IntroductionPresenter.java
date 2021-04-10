package com.startup.eventsearcher.presenters.introduction;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.startup.eventsearcher.views.introduction.IIntroductionView;

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
    public void doesUserHaveLogin() {
        if (firebaseUser.getDisplayName() != null &&
                !firebaseUser.getDisplayName().isEmpty()){
            Log.i(TAG, "doesUserHaveLogin: Пользователь имеет логин");
            iIntroductionView.onCheckUserHasLogin(true);
        }else {
            Log.w(TAG, "doesUserHaveLoginAndPhoto: Пользователь не имеет логин");
            iIntroductionView.onCheckUserHasLogin(false);
        }
    }
}
