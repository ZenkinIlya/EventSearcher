package com.startup.eventsearcher.authentication.mvpAuth.views.introduction;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.startup.eventsearcher.R;
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

/*1. Получение данных пользователя из SharedPreference
* 2. Проверка корректности данных и попытка входа пользователя
* 3. При не удаче пользователь попадает в SignIn, в противном случае в Main*/
public class IntroductionActivity extends AppCompatActivity implements ISetUserDataView, ISignInView {

    ActivityIntroductionBinding bind;

    private GetUserDataPresenter getUserDataPresenter;
    private SignInPresenter signInPresenter;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityIntroductionBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        getUserDataPresenter = new GetUserDataPresenter(this, new CurrentUser(this));
        //Получаем данные пользователя, которые были сохранены в SharedPreference ранее (email, password)
        getUserDataPresenter.onGetData();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        signInPresenter = new SignInPresenter(this, new UserDataVerification(this));
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
    }

    @Override
    public void onPasswordError(String message) {
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
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
        finish();
    }

    @Override
    public void onErrorVerification() {
        Intent intent = new Intent(this, SignInActivity.class);
        intent.putExtra("email", "");
        intent.putExtra("password", "");
        startActivity(intent);
        finish();
    }

    @Override
    public void isLogin(boolean isLogin) {
        if (isLogin){
            exitFromActivityWithSuccessLogin();
        }else {
            signInPresenter.onLogin(email, password);
        }
    }


    private void exitFromActivityWithSuccessLogin(){
        //Если даный пользователь имеет логин и фото, то переходим в MainActivity
        if (signInPresenter.getFirebaseUser().getDisplayName() != null &&
                !signInPresenter.getFirebaseUser().getDisplayName().isEmpty() &&
                signInPresenter.getFirebaseUser().getPhotoUrl() != null){

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            Intent intent = new Intent(this, SetExtraUserDataActivity.class);
            startActivity(intent);
            finish();
        }
    }
}