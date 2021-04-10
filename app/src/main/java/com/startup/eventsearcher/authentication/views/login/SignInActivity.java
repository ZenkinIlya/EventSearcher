package com.startup.eventsearcher.authentication.views.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.authentication.presenters.login.SignInPresenter;
import com.startup.eventsearcher.authentication.presenters.userData.LoadConfidentialUserDataPresenter;
import com.startup.eventsearcher.authentication.presenters.userData.SaveConfidentialUserDataPresenter;
import com.startup.eventsearcher.authentication.utils.user.ProviderConfidentialUserData;
import com.startup.eventsearcher.authentication.utils.user.UserDataVerification;
import com.startup.eventsearcher.databinding.ActivitySignInBinding;
import com.startup.eventsearcher.main.MainActivity;

import java.util.Objects;

/*1. Получаем email и password из SharedPreference. */

public class SignInActivity extends AppCompatActivity implements ISignInView, ISetUserDataView {

    private static final String TAG = "tgSignInAct";
    private ActivitySignInBinding bind;

    private SignInPresenter signInPresenter;
    private SaveConfidentialUserDataPresenter saveConfidentialUserDataPresenter;
    private LoadConfidentialUserDataPresenter getConfidentialUserDataPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        getConfidentialUserDataPresenter =
                new LoadConfidentialUserDataPresenter(this, new ProviderConfidentialUserData(this));
        //Получаем email и пароль из SharedPreference и заполняем поля
        getConfidentialUserDataPresenter.getData();

        saveConfidentialUserDataPresenter = new SaveConfidentialUserDataPresenter(new ProviderConfidentialUserData(this));

        signInPresenter = new SignInPresenter(this, new UserDataVerification(this));

        componentListener();
    }

    private void componentListener() {
        //Вход
        bind.signInToComeIn.setOnClickListener(view -> {
            signInPresenter.isUserSignIn(Objects.requireNonNull(bind.signInEmail.getEditText()).getText().toString(),
                    Objects.requireNonNull(bind.signInPassword.getEditText()).getText().toString());
        });

        //Регистрация
        bind.signInRegistration.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onEmailError(String message) {
        bind.signInEmail.setError(message);
    }

    @Override
    public void onPasswordError(String message) {
        bind.signInPassword.setError(message);
    }

    @Override
    public void onSuccess() {
        //Метод вызывается в случае если данные пользователя корректны и аутентификация прошла успешно

        boolean saveData = bind.signInSaveData.isChecked();
        String email = Objects.requireNonNull(bind.signInEmail.getEditText()).getText().toString();
        String password = Objects.requireNonNull(bind.signInPassword.getEditText()).getText().toString();

        //Сохранение конфиденциальных данных пользователя
        saveConfidentialUserDataPresenter.onSetData(saveData, email, password);

        exitFromActivity();
    }

    @Override
    public void onErrorFirebase(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSetEmail(String email) {
        Objects.requireNonNull(bind.signInEmail.getEditText()).setText(email);
    }

    @Override
    public void onSetPassword(String password) {
        Objects.requireNonNull(bind.signInPassword.getEditText()).setText(password);
    }

    @Override
    public void onCheckUserHaveLoginAndPhoto(boolean userHaveLoginAndPhoto) {
        if (userHaveLoginAndPhoto){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, SetExtraUserDataActivity.class);
            startActivity(intent);
        }
    }

    private void exitFromActivity(){
        signInPresenter.doesUserHaveLoginAndPhoto();
    }
}