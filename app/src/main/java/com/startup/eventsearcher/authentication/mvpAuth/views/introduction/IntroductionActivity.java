package com.startup.eventsearcher.authentication.mvpAuth.views.introduction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.CurrentUser;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.UserDataDataVerification;
import com.startup.eventsearcher.authentication.mvpAuth.presenters.SignInPresenter;
import com.startup.eventsearcher.authentication.mvpAuth.presenters.GetUserDataPresenter;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.ISignInView;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.ISetUserDataView;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.SignInActivity;

/*1. Получение данных пользователя из SharedPreference
* 2. Проверка корректности данных и попытка входа пользователя
* 3. При не удаче пользователь попадает в SignIn, в противном случае в Main*/
public class IntroductionActivity extends AppCompatActivity implements ISetUserDataView, ISignInView {

    private GetUserDataPresenter getUserDataPresenter;
    private SignInPresenter signInPresenter;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        getUserDataPresenter = new GetUserDataPresenter(this, new CurrentUser(this));
        //Получаем данные пользователя, которые были сохранены в SharedPreference ранее (email, password)
        getUserDataPresenter.onGetData();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        signInPresenter = new SignInPresenter(this, new UserDataDataVerification(this));
        signInPresenter.checkLogin();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    public void onSetEmail(String email) {
        this.email = email;
    }

    @Override
    public void onSetPassword(String password) {
        this.password = password;
    }

    @Override
    public void onEmailError(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPasswordError(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "Аутентификация успешна", Toast.LENGTH_SHORT).show();
        //TODO Заменить SignInActivity на MainActivity
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(String message) {
        if (message != null) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void isLogin(boolean isLogin) {
        if (isLogin){
            //TODO Заменить SignInActivity на MainActivity
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }else {
            signInPresenter.onLogin(email, password);
        }
    }
}