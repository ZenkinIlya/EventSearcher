package com.startup.eventsearcher.views.introduction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.presenters.introduction.IntroductionPresenter;
import com.startup.eventsearcher.views.login.SetExtraUserDataActivity;
import com.startup.eventsearcher.views.login.SignInActivity;
import com.startup.eventsearcher.databinding.ActivityIntroductionBinding;
import com.startup.eventsearcher.views.main.MainActivity;

/*1. Проверка аутентифицирован ли пользователь в Firebase - checkLogin()
* 2. Пользователь аутентифицирован в Firebase?
*       да - имеет ли пользователь логин и фото?
*           да - переходим в MainActivity
*           нет -переходим в SetExtraUserDataActivity
*       нет - переходим в SignInActivity
* */

public class IntroductionActivity extends AppCompatActivity implements IIntroductionView {

    private static final String TAG = "tgIntroductionAct";

    ActivityIntroductionBinding bind;

    private IntroductionPresenter introductionPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityIntroductionBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        introductionPresenter = new IntroductionPresenter(this);
        introductionPresenter.isUserLoginInFirebase();
    }

    @Override
    public void onCheckLoginInFirebase(boolean loginInFirebase) {
        if (loginInFirebase){
            Toast.makeText(this, "Аутентификация успешна", Toast.LENGTH_SHORT).show();
            introductionPresenter.doesUserHaveLoginAndPhoto();
        }else {
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onCheckUserHaveLoginAndPhoto(boolean userHaveLoginAndPhoto) {
        Intent intent;
        if (userHaveLoginAndPhoto){
            intent = new Intent(this, MainActivity.class);
        }else {
            intent = new Intent(this, SetExtraUserDataActivity.class);
        }
        startActivity(intent);
        finish();
    }
}