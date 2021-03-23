package com.startup.eventsearcher.authentication.utils.firebase;

import android.util.Log;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthException;

public class FirebaseErrorHandler {

    private static final String TAG = "tgFirebaseErrorHandler";
    private static TypeViewOutputError typeViewOutputError;

    public static TypeViewOutputError getTypeViewOutputError() {
        return typeViewOutputError;
    }

    public static String errorHandler(Exception e) {
        typeViewOutputError = TypeViewOutputError.DEFAULT;
        
        if (e instanceof FirebaseNetworkException){
            Log.w(TAG, "errorHandler: Нет соединения с сетью. " + e.getLocalizedMessage());
            return "Нет соединения с сетью";
        }else if (e instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) e).getErrorCode();
            switch (errorCode) {
                case "ERROR_USER_NOT_FOUND": {
                    Log.w(TAG, "errorHandler: (" +errorCode+ ") Пользователь не существует. " +e.getLocalizedMessage());
                    typeViewOutputError = TypeViewOutputError.EMAIL;
                    return "Пользователь не существует";
                }
                case "ERROR_EMAIL_ALREADY_IN_USE": {
                    Log.w(TAG, "errorHandler: (" +errorCode+ ") Email уже используется другим аккаунтом. " +e.getLocalizedMessage());
                    typeViewOutputError = TypeViewOutputError.EMAIL;
                    return "Email уже используется другим аккаунтом";
                }
                case "ERROR_INVALID_EMAIL": {
                    Log.w(TAG, "errorHandler: (" +errorCode+ ") Не верный формат Email. " +e.getLocalizedMessage());
                    typeViewOutputError = TypeViewOutputError.EMAIL;
                    return "Не верный формат Email";
                }

                case "ERROR_WRONG_PASSWORD": {
                    Log.w(TAG, "errorHandler: (" +errorCode+ ") Неверный пароль. " +e.getLocalizedMessage());
                    typeViewOutputError = TypeViewOutputError.PASSWORD;
                    return "Неверный пароль";
                }
                case "ERROR_WEAK_PASSWORD": {
                    Log.w(TAG, "errorHandler: (" +errorCode+ ") Введите не менее 6 символов. " +e.getLocalizedMessage());
                    typeViewOutputError = TypeViewOutputError.PASSWORD;
                    return "Введите не менее 6 символов";
                }

                default:{
                    Log.w(TAG, "errorHandler: (" +errorCode+ ") " +e.getLocalizedMessage());
                    return errorCode;
                }
            }
        }else {
            Log.w(TAG, "errorHandler: Неизвестная ошибка. " +e.getLocalizedMessage());
            return "Неизвестная ошибка";
        }
    }
}
