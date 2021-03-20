package com.startup.eventsearcher.authentication.mvpAuth.utils.firebase;

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
                case "ERROR_WRONG_PASSWORD": {
                    Log.w(TAG, "errorHandler: (" +errorCode+ ") Неверный пароль. " +e.getLocalizedMessage());
                    typeViewOutputError = TypeViewOutputError.PASSWORD;
                    return "Неверный пароль";
                }
                case "ERROR_NETWORK_REQUEST_FAILED": {
                    Log.w(TAG, "errorHandler: (" +errorCode+ ") Ошибка запроса данных пользователя. " +e.getLocalizedMessage());
                    return "Ошибка запроса данных пользователя";
                }
                default:{
                    Log.w(TAG, "errorHandler: (" +errorCode+ ") Неизвестная ошибка аутентификации. " +e.getLocalizedMessage());
                    return "Неизвестная ошибка аутентификации";
                }
            }
        }else {
            Log.w(TAG, "errorHandler: Неизвестная ошибка. " +e.getLocalizedMessage());
            return "Неизвестная ошибка";
        }
    }
}
