package com.startup.eventsearcher.authentication.mvpAuth.views.login;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.authentication.mvpAuth.presenters.SignUpPresenter;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.UserDataVerification;
import com.startup.eventsearcher.databinding.ActivitySignUpBinding;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity implements ISignUpView {

    ActivitySignUpBinding bind;
    private SignUpPresenter signUpPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        signUpPresenter = new SignUpPresenter(this, new UserDataVerification(this));

        componentListener();
    }

    private void componentListener() {
        bind.signUpRegistration.setOnClickListener(view ->
                signUpPresenter.onRegistration(Objects.requireNonNull(bind.signUpEmail.getEditText()).getText().toString(),
                Objects.requireNonNull(bind.signUpPassword.getEditText()).getText().toString(),
                Objects.requireNonNull(bind.signUpConfirmPassword.getEditText()).getText().toString()));
    }

    @Override
    public void onEmailError(String message) {
        bind.signUpEmail.setError(message);
    }

    @Override
    public void onPasswordError(String message) {
        bind.signUpPassword.setError(message);
    }

    @Override
    public void onConfirmPassword(String message) {
        bind.signUpConfirmPassword.setError(message);
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "Вы успешно зарегестрированы!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onErrorFirebase(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}