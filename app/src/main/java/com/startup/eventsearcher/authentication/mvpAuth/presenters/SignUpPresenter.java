package com.startup.eventsearcher.authentication.mvpAuth.presenters;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.startup.eventsearcher.authentication.mvpAuth.utils.firebase.FirebaseErrorHandler;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.IUserDataVerification;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.ISignUpView;

public class SignUpPresenter implements ISignUpPresenter{

    private static final String TAG = "tgSignUpPresenter";
    private ISignUpView iSignUpView;
    private IUserDataVerification iUserDataVerification;
    private FirebaseAuth firebaseAuth;

    public SignUpPresenter(ISignUpView iSignUpView, IUserDataVerification iUserDataVerification) {
        this.iSignUpView = iSignUpView;
        this.iUserDataVerification = iUserDataVerification;

        firebaseAuth = FirebaseAuth.getInstance();
    }

    //Проверка корректности данных
    private boolean verificationData(String login, String email, String password, String confirmPassword){
        //Проверка корректности данных
        Log.d(TAG, "verificationData: Проверка корректности данных");
        iUserDataVerification.setDataCorrect(true);

        String verificationLogin = iUserDataVerification.verificationLogin(login);
        iSignUpView.onLoginError(verificationLogin);

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
                    iSignUpView.onError(message);
                });
    }

    @Override
    public void onRegistration(String login, String email, String password, String confirmPassword) {
        //Проверка корректности данных
        if (verificationData(login, email, password, confirmPassword)){
            //Регистрация пользователя в Firebase
            createUserWithEmailAndPassword(email, password);
        }
    }
}
