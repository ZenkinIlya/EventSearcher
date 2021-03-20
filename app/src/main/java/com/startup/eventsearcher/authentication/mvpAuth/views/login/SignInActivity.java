package com.startup.eventsearcher.authentication.mvpAuth.views.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.authentication.mvpAuth.presenters.SaveUserDataPresenter;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.CurrentUser;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.UserDataDataVerification;
import com.startup.eventsearcher.authentication.mvpAuth.presenters.SignInPresenter;
import com.startup.eventsearcher.authentication.mvpAuth.presenters.GetUserDataPresenter;
import com.startup.eventsearcher.databinding.ActivitySignInBinding;
import com.startup.eventsearcher.main.MainActivity;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements ISignInView, ISetUserDataView {

    private ActivitySignInBinding bind;

    private SignInPresenter signInPresenter;
    private GetUserDataPresenter getUserDataPresenter;
    private SaveUserDataPresenter saveUserDataPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        /*Перед вызовом этого активити, в сплешЭкране проверяется был ли запомнит пользователь
        * ранее. Если был, то это активити пропускается и происходит вход по данным из sharedPreference*/

        getUserDataPresenter = new GetUserDataPresenter(this, new CurrentUser(this));
        //Получение данных раннее входившего пользователя из SharedPreference
        getUserDataPresenter.onGetData();

        saveUserDataPresenter = new SaveUserDataPresenter(new CurrentUser(this));

        signInPresenter = new SignInPresenter(this, new UserDataDataVerification(this));
        //Проверка авторизован ли пользователь
        signInPresenter.checkLogin();

        componentListener();
    }

    private void componentListener() {
        bind.signInToComeIn.setOnClickListener(view -> {
            signInPresenter.onLogin(Objects.requireNonNull(bind.signInEmail.getEditText()).getText().toString(),
                    Objects.requireNonNull(bind.signInPassword.getEditText()).getText().toString());
        });

        bind.signInRegistration.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
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
    public void onEmailError(String message) {
        bind.signInEmail.setError(message);
    }

    @Override
    public void onPasswordError(String message) {
        bind.signInPassword.setError(message);
    }

    @Override
    public void onSuccess() {
        //Метод вызывается в случае если данные пользователя корректны

        boolean saveData = bind.signInSaveData.isChecked();
        String email = Objects.requireNonNull(bind.signInEmail.getEditText()).getText().toString();
        String password = Objects.requireNonNull(bind.signInPassword.getEditText()).getText().toString();

        //Сохранение данных пользователя
        saveUserDataPresenter.onSetData(saveData, "", email, password, "", "");

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void isLogin(boolean isLogin) {
        if (isLogin){
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
        }
    }
}