package com.startup.eventsearcher.authentication.mvpAuth.views.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.startup.eventsearcher.authentication.mvpAuth.models.user.User;
import com.startup.eventsearcher.authentication.mvpAuth.presenters.userData.SaveUserDataPresenter;
import com.startup.eventsearcher.authentication.mvpAuth.presenters.userData.UpdateUserDataPresenter;
import com.startup.eventsearcher.authentication.mvpAuth.utils.user.CurrentUser;
import com.startup.eventsearcher.databinding.ActivitySetExtraUserDataBinding;
import com.startup.eventsearcher.main.MainActivity;
import com.startup.eventsearcher.utils.Config;

import java.io.Serializable;
import java.util.Objects;

public class SetExtraUserDataActivity extends AppCompatActivity implements ISetExtraUserDataView {

    private static final String TAG = "tgSetExtraUserData";
    private ActivitySetExtraUserDataBinding bind;

    private UpdateUserDataPresenter updateUserDataPresenter;
    private SaveUserDataPresenter saveUserDataPresenter;
    private Uri imageUri;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySetExtraUserDataBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        user = (User) getIntent().getSerializableExtra("user");
        Log.d(TAG, "onCreate: user = " +user.toString());

        updateUserDataPresenter = new UpdateUserDataPresenter(this);

        saveUserDataPresenter = new SaveUserDataPresenter(new CurrentUser(this));

        componentListener();
    }

    private void componentListener() {

        //Выбор изображения
        bind.setDataUserLoadPhoto.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, Config.LOAD_IMAGE);
        });

        //Применить изменения
        bind.setDataUserAccept.setOnClickListener(view -> {
            bind.setDataUserLoading.setVisibility(View.VISIBLE);
            updateUserDataPresenter.updateDisplayNameAndPhoto(Objects.requireNonNull(bind.setDataUserLogin.getEditText()).getText().toString(),
                    imageUri);
        });

        //Пропустить шаг установки логина и изображения
        bind.setDataUserSkip.setOnClickListener(view -> {
            updateUserDataPresenter.updateDisplayNameAndPhoto("no_name", null);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Config.LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            bind.setDataUserPhoto.setImageURI(imageUri);
        }
    }

    @Override
    public void onSuccess() {
        saveUserDataPresenter.onSetData(true,
                updateUserDataPresenter.getFirebaseUser().getUid(),
                user.getConfidentialUserData().getEmail(),
                user.getConfidentialUserData().getPassword(),
                updateUserDataPresenter.getFirebaseUser().getDisplayName(),
                updateUserDataPresenter.getFirebaseUser().getPhotoUrl().toString());
        bind.setDataUserLoading.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onErrorEmail(String message) {
        //При обновлении email
    }

    @Override
    public void onErrorPassword(String message) {
        //При обновлении пароля
    }

    @Override
    public void onError(String message) {
        bind.setDataUserLoading.setVisibility(View.INVISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}