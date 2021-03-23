package com.startup.eventsearcher.authentication.presenters.login;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.startup.eventsearcher.authentication.utils.firebase.FirebaseErrorHandler;
import com.startup.eventsearcher.authentication.utils.firebase.TypeViewOutputError;
import com.startup.eventsearcher.authentication.utils.user.IUserDataVerification;
import com.startup.eventsearcher.authentication.views.login.ISignUpView;

public class SignUpPresenter implements ISignUpPresenter{

    private static final String TAG = "tgSignUpPres";
    private final ISignUpView iSignUpView;
    private final IUserDataVerification iUserDataVerification;
    private final FirebaseAuth firebaseAuth;

    public SignUpPresenter(ISignUpView iSignUpView, IUserDataVerification iUserDataVerification) {
        this.iSignUpView = iSignUpView;
        this.iUserDataVerification = iUserDataVerification;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    //Проверка корректности данных
    private boolean verificationData(String email, String password, String confirmPassword){
        //Проверка корректности данных
        Log.d(TAG, "verificationData: Проверка корректности данных");
        iUserDataVerification.setDataCorrect(true);

        String verificationEmail = iUserDataVerification.verificationEmail(email);
        iSignUpView.onEmailError(verificationEmail);

        String verificationPassword = iUserDataVerification.verificationPassword(password);
        iSignUpView.onPasswordError(verificationPassword);

        String verificationConfirmPassword = iUserDataVerification.verificationPassword(confirmPassword);
        iSignUpView.onConfirmPassword(verificationConfirmPassword);

        if (iUserDataVerification.isDataCorrect()){
            String comparePasswords = iUserDataVerification.comparePasswords(password, confirmPassword);
            iSignUpView.onConfirmPassword(comparePasswords);
        }

        if (!iUserDataVerification.isDataCorrect()) {
            Log.w(TAG, "verificationData: Данные не корректны");
            return false;
        }else {
            Log.i(TAG, "verificationData: Данные корректны");
            return true;
        }
    }

    //Регистрация нового пользователя
    private void createUserWithEmailAndPassword(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //Регистрация прошла успешно
                        Log.i(TAG, "createUserWithEmailAndPassword: createUserWithEmail:success");
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        Log.d(TAG, "createUserWithEmailAndPassword: firebaseUser = " + user.getEmail());
                        iSignUpView.onSuccess();
                    }
                })
                .addOnFailureListener(e -> {
                    String message = FirebaseErrorHandler.errorHandler(e);
                    TypeViewOutputError typeViewOutputError = FirebaseErrorHandler.getTypeViewOutputError();

                    switch (typeViewOutputError){
                        case DEFAULT:{
                            iSignUpView.onErrorFirebase(message);
                            return;
                        }
                        case EMAIL:{
                            iSignUpView.onEmailError(message);
                            return;
                        }
                        case PASSWORD:{
                            iSignUpView.onPasswordError(message);
                        }
                    }
                });
    }

    @Override
    public void onRegistration(String email, String password, String confirmPassword) {
        //Проверка корректности данных
        if (verificationData(email, password, confirmPassword)){
            //Регистрация пользователя в Firebase
            createUserWithEmailAndPassword(email, password);
        }
    }
}
