package com.startup.eventsearcher.authentication.Utils;

import android.content.Context;
import android.widget.EditText;

import com.startup.eventsearcher.R;

public class DataVerification {

    private boolean dataCorrect;

    public DataVerification() {
        this.dataCorrect = true;
    }

    public boolean isDataCorrect() {
        return dataCorrect;
    }

    public void verificationPassword(Context context, EditText editTextPassword){
        String password = String.valueOf(editTextPassword.getText());
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            editTextPassword.setError(context.getString(R.string.lengthPassword));
            dataCorrect = false;
        } else {
            editTextPassword.setError(null);
        }
    }

    public void verificationLogin(Context context, EditText editTextLogin) {
        String login = String.valueOf(editTextLogin.getText());
        if (login.isEmpty() || login.length() < 3) {
            editTextLogin.setError(context.getString(R.string.lengthLogin));
            dataCorrect = false;
        } else {
            editTextLogin.setError(null);
        }
    }

    public void verificationEmail(Context context, EditText editTextEmail) {
        String email = String.valueOf(editTextEmail.getText());
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(context.getString(R.string.notValidEmail));
            dataCorrect = false;
        } else {
            editTextEmail.setError(null);
        }
    }

    public void comparePasswords(Context context, EditText editTextPassword, EditText editTextConfirmPassword) {
        String password = String.valueOf(editTextPassword.getText());
        String passwordConfirm = String.valueOf(editTextConfirmPassword.getText());
        if (!password.equals(passwordConfirm)){
            editTextConfirmPassword.setError(context.getString(R.string.passwordsNotCompare));
            dataCorrect = false;
        }else {
            editTextConfirmPassword.setError(null);
        }
    }
}
