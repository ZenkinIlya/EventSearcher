package com.startup.eventsearcher.authentication.mvpAuth.utils.user;

import android.content.Context;

import com.startup.eventsearcher.R;

public class UserDataDataVerification implements IUserDataVerification {
    private boolean dataCorrect;
    private final Context context;

    public UserDataDataVerification(Context context) {
        this.context = context;
    }

    @Override
    public void setDataCorrect(boolean dataCorrect) {
        this.dataCorrect = dataCorrect;
    }

    @Override
    public boolean isDataCorrect() {
        return dataCorrect;
    }

    @Override
    public String verificationPassword(String password){
        if (password == null) {
            dataCorrect = false;
            return null;
        }
        if (password.isEmpty() || password.length() < 6 || password.length() > 10) {
            dataCorrect = false;
            return context.getString(R.string.lengthPassword);
        } else {
            return null;
        }
    }

    @Override
    public String verificationEmail(String email) {
        if (email == null) {
            dataCorrect = false;
            return null;
        }
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            dataCorrect = false;
            return context.getString(R.string.notValidEmail);
        } else {
            return null;
        }
    }

    @Override
    public String verificationLogin(String login) {
        if (login == null) {
            dataCorrect = false;
            return null;
        }
        if (login.isEmpty() || login.length() < 3) {
            dataCorrect = false;
            return context.getString(R.string.lengthLogin);
        } else {
            return null;
        }
    }

    @Override
    public String comparePasswords(String firstPassword, String secondPassword) {
        if (!firstPassword.equals(secondPassword)){
            dataCorrect = false;
            return context.getString(R.string.passwordsNotCompare);
        }else {
            return null;
        }
    }
}
