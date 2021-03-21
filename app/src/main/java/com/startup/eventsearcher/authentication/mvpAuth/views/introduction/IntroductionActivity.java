package com.startup.eventsearcher.authentication.mvpAuth.views.introduction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.mvpAuth.models.user.User;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.CurrentUser;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.UserDataVerification;
import com.startup.eventsearcher.authentication.mvpAuth.presenters.SignInPresenter;
import com.startup.eventsearcher.authentication.mvpAuth.presenters.userData.GetUserDataPresenter;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.ISignInView;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.ISetUserDataView;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.SetExtraUserDataActivity;
import com.startup.eventsearcher.authentication.mvpAuth.views.login.SignInActivity;
import com.startup.eventsearcher.databinding.ActivityIntroductionBinding;
import com.startup.eventsearcher.main.MainActivity;

/*1. Проверка аутентифицирован ли пользователь в Firebase checkLogin()
* 2. Если пользователь аутентифицирован в Firebase то происходит проверка - имеет ли пользователь
* логин и фото. Если да, то переходим в MainActivity, в противном случае в SetExtraUserDataActivity.
* Если пользователь не аутентифицирован, то получаем данные из SharedPreference
* 3. Запуск onLogin() - верификация данных и аутентификация
* 3. При не удаче пользователь попадает в SignInActivity, в противном случае повтор п.2
* */
public class IntroductionActivity extends AppCompatActivity implements ISetUserDataView, ISignInView {

    private static final String TAG = "tgIntroductionActivity";

    ActivityIntroductionBinding bind;

    private GetUserDataPresenter getUserDataPresenter;
    private SignInPresenter signInPresenter;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityIntroductionBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        getUserDataPresenter = new GetUserDataPresenter(this, new CurrentUser(this));

        signInPresenter = new SignInPresenter(this, new UserDataVerification(this));
        signInPresenter.checkLogin();

    }

    @Override
    public void onSetEmail(String email) {}

    @Override
    public void onSetPassword(String password) {}

    @Override
    public void onGetUserDataFromSharedPreferenceSuccess(User user) {
        this.user = user;
        signInPresenter.onLogin(user.getConfidentialUserData().getEmail(),
                user.getConfidentialUserData().getPassword());
    }

    @Override
    public void onEmailError(String message) {
        //onErrorVerification
    }

    @Override
    public void onPasswordError(String message) {
        //onErrorVerification
    }

    @Override
    public void onSuccess() {
        Toast.makeText(this, "Аутентификация успешна", Toast.LENGTH_SHORT).show();
        exitFromActivityWithSuccessLogin();
    }

    @Override
    public void onErrorFirebase(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onErrorVerification() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void isLogin(boolean isLogin) {
        //Получаем данные пользователя, которые были сохранены в SharedPreference ранее (email, password)
        getUserDataPresenter.onGetData();
    }

    private void exitFromActivityWithSuccessLogin(){
        //Если данный пользователь имеет логин и фото, то переходим в MainActivity
        if (signInPresenter.getFirebaseUser().getDisplayName() != null &&
                !signInPresenter.getFirebaseUser().getDisplayName().isEmpty() &&
                signInPresenter.getFirebaseUser().getPhotoUrl() != null){

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(this, SetExtraUserDataActivity.class);
            intent.putExtra("user", user);
            Log.d(TAG, "exitFromActivityWithSuccessLogin: user = " +user.toString());
            startActivity(intent);
            finish();
        }
    }
}