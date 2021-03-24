package com.startup.eventsearcher.authentication.presenters.login;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.startup.eventsearcher.authentication.utils.firebase.FirebaseErrorHandler;
import com.startup.eventsearcher.authentication.utils.firebase.TypeViewOutputError;
import com.startup.eventsearcher.authentication.utils.user.IUserDataVerification;
import com.startup.eventsearcher.authentication.views.login.ISignInView;

public class SignInPresenter implements ISignInPresenter {

    private static final String TAG = "tgSignInPres";
    private final ISignInView iSignInView;
    private final IUserDataVerification iUserDataVerification;
    private final FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public SignInPresenter(ISignInView iSignInView, IUserDataVerification iUserDataVerification) {
        this.iSignInView = iSignInView;
        this.iUserDataVerification = iUserDataVerification;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
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
                        firebaseUser = firebaseAuth.getCurrentUser();
                        Log.d(TAG, "signInWithEmailAndPassword: firebaseUser email = " + firebaseUser.getEmail());
                        iSignInView.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    String message = FirebaseErrorHandler.errorHandler(e);
                    TypeViewOutputError typeViewOutputError = FirebaseErrorHandler.getTypeViewOutputError();

                    switch (typeViewOutputError){
                        case DEFAULT:{
                            iSignInView.onErrorFirebase(message);
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
    public void isUserSignIn(String email, String password) {
        //Проверка корректности данных
        if (verificationData(email, password)){
            signInWithEmailAndPassword(email, password);
        }
    }

    @Override
    public void doesUserHaveLoginAndPhoto() {
        if (firebaseUser.getDisplayName() != null &&
                !firebaseUser.getDisplayName().isEmpty() &&
                firebaseUser.getPhotoUrl() != null){
            Log.i(TAG, "doesUserHaveLoginAndPhoto: Пользователь имеет логин и фото");
            iSignInView.onCheckUserHaveLoginAndPhoto(true);
        }else {
            Log.w(TAG, "doesUserHaveLoginAndPhoto: Пользователь не имеет логин и фото");
            iSignInView.onCheckUserHaveLoginAndPhoto(false);
        }
    }
}