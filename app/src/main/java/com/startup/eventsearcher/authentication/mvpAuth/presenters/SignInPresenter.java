package com.startup.eventsearcher.authentication.mvpAuth.presenters;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.IUserDataVerification;
import com.startup.eventsearcher.authentication.mvpAuth.utils.firebase.FirebaseErrorHandler;
import com.startup.eventsearcher.authentication.mvpAuth.utils.firebase.TypeViewOutputError;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.ISignInView;

public class SignInPresenter implements ISignInPresenter {

    private static final String TAG = "tgSignInPresenter";
    ISignInView iSignInView;
    IUserDataVerification iUserDataVerification;
    FirebaseAuth firebaseAuth;

    public SignInPresenter(ISignInView iSignInView, IUserDataVerification iUserDataVerification) {
        this.iSignInView = iSignInView;
        this.iUserDataVerification = iUserDataVerification;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    //Проверка корректности данных
    private boolean verificationData(String email, String password){
        Log.d(TAG, "verificationData: Проверка корректности данных");
        iUserDataVerification.setDataCorrect(true);

        String verificationEmail = iUserDataVerification.verificationEmail(email);
        iSignInView.onEmailError(verificationEmail);

        String verificationPassword = iUserDataVerification.verificationPassword(password);
        iSignInView.onPasswordError(verificationPassword);

        if (!iUserDataVerification.isDataCorrect()) {
            Log.w(TAG, "verificationData: Данные не корректны");
            return false;
        }else {
            Log.i(TAG, "verificationData: Данные корректны");
            return true;
        }
    }

    //Аутентификация
    private void signInWithEmailAndPassword(String email, String password){
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Авторизация прошла успешно
                        Log.i(TAG, "signInWithEmailAndPassword: success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d(TAG, "signInWithEmailAndPassword: firebaseUser email = " + user.getEmail());
                        iSignInView.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    String message = FirebaseErrorHandler.errorHandler(e);
                    TypeViewOutputError typeViewOutputError = FirebaseErrorHandler.getTypeViewOutputError();

                    switch (typeViewOutputError){
                        case DEFAULT:{
                            iSignInView.onError(message);
                            return;
                        }
                        case EMAIL:{
                            iSignInView.onEmailError(message);
                            return;
                        }
                        case PASSWORD:{
                            iSignInView.onPasswordError(message);
                        }
                    }
                });
    }

    @Override
    public void onLogin(String email, String password) {
        //Проверка корректности данных
        if (verificationData(email, password)){
            signInWithEmailAndPassword(email, password);
        }
    }

    @Override
    public void checkLogin() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null){
            Log.d(TAG, "checkLogin: currentUser:");
            Log.d(TAG, "checkLogin: DisplayName = " + currentUser.getDisplayName());
            Log.d(TAG, "checkLogin: Email = " + currentUser.getEmail());
            Log.d(TAG, "checkLogin: PhotoUrl = " + currentUser.getPhotoUrl());
            Log.d(TAG, "checkLogin: isEmailVerified = " + currentUser.isEmailVerified());
            Log.d(TAG, "checkLogin: Uid = " + currentUser.getUid());
            Log.i(TAG, "checkLogin: Пользователь авторизован");

            for (UserInfo userInfo: currentUser.getProviderData()){
                Log.d(TAG, "checkLogin: Provider:");
                Log.d(TAG, "checkLogin: DisplayName = " + userInfo.getDisplayName());
                Log.d(TAG, "checkLogin: Email = " + userInfo.getEmail());
                Log.d(TAG, "checkLogin: PhotoUrl = " + userInfo.getPhotoUrl());
                Log.d(TAG, "checkLogin: isEmailVerified = " + userInfo.isEmailVerified());
                Log.d(TAG, "checkLogin: Uid = " + userInfo.getUid());
                Log.d(TAG, "checkLogin: ProviderId = " + userInfo.getProviderId());
            }
            iSignInView.isLogin(true);
        }else {
            Log.w(TAG, "checkLogin: Пользователь не авторизован");
            iSignInView.isLogin(false);
        }
    }
}
