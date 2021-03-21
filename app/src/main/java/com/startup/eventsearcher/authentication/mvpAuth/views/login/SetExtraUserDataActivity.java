package com.startup.eventsearcher.authentication.mvpAuth.views.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.startup.eventsearcher.authentication.mvpAuth.presenters.userData.UpdateUserDataPresenter;
import com.startup.eventsearcher.databinding.ActivitySetExtraUserDataBinding;
import com.startup.eventsearcher.main.MainActivity;
import com.startup.eventsearcher.utils.Config;

import java.util.Objects;

public class SetExtraUserDataActivity extends AppCompatActivity implements ISetExtraUserDataView {

    ActivitySetExtraUserDataBinding bind;
    UpdateUserDataPresenter updateUserDataPresenter;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySetExtraUserDataBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        updateUserDataPresenter = new UpdateUserDataPresenter(this);

        componentListener();
    }

    private void componentListener() {
        bind.setDataUserLoadPhoto.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, Config.LOAD_IMAGE);
        });

        bind.setDataUserAccept.setOnClickListener(view -> {
            bind.setDataUserLoading.setVisibility(View.VISIBLE);
            updateUserDataPresenter.updateDisplayNameAndPhoto(Objects.requireNonNull(bind.setDataUserLogin.getEditText()).getText().toString(),
                    imageUri);
        });

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
        bind.setDataUserLoading.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onErrorEmail(String message) {

    }

    @Override
    public void onErrorPassword(String message) {

    }

    @Override
    public void onError(String message) {
        bind.setDataUserLoading.setVisibility(View.INVISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}