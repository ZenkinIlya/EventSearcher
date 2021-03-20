package com.startup.eventsearcher.authentication.mvpAuth.views.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.authentication.mvpAuth.presenters.userData.SaveUserDataPresenter;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.CurrentUser;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.UserDataVerification;
import com.startup.eventsearcher.authentication.mvpAuth.presenters.SignInPresenter;
import com.startup.eventsearcher.databinding.ActivitySignInBinding;
import com.startup.eventsearcher.main.MainActivity;

import java.util.Objects;

public class SignInActivity extends AppCompatActivity implements ISignInView{

    private ActivitySignInBinding bind;

    private SignInPresenter signInPresenter;
    private SaveUserDataPresenter saveUserDataPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        /*Перед вызовом этого активити, в сплешЭкране проверяется был ли запомнит пользователь
        * ранее. Если был, то это активити пропускается и происходит вход по данным из sharedPreference*/
        String email = getIntent().getStringExtra("email");
        String password = getIntent().getStringExtra("password");
        Objects.requireNonNull(bind.signInEmail.getEditText()).setText(email);
        Objects.requireNonNull(bind.signInPassword.getEditText()).setText(password);

        saveUserDataPresenter = new SaveUserDataPresenter(new CurrentUser(this));

        signInPresenter = new SignInPresenter(this, new UserDataVerification(this));
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

        exitFromActivity();
    }

    @Override
    public void onErrorFirebase(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorVerification() {
        //ничего не делаю, все ошибки уже выведены под email password
    }

    @Override
    public void isLogin(boolean isLogin) {
        if (isLogin){
            exitFromActivity();
        }
    }

    private void exitFromActivity(){
        //Если даный пользователь имеет логин и фото, то переходим в MainActivity
        if (signInPresenter.getFirebaseUser().getDisplayName() != null &&
                !signInPresenter.getFirebaseUser().getDisplayName().isEmpty() &&
                signInPresenter.getFirebaseUser().getPhotoUrl() != null){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else {
            Intent intent = new Intent(this, SetExtraUserDataActivity.class);
            startActivity(intent);
        }
    }
}