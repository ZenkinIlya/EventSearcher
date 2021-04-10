package com.startup.eventsearcher.views.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.presenters.userData.UpdateUserDataPresenter;
import com.startup.eventsearcher.databinding.ActivitySetExtraUserDataBinding;
import com.startup.eventsearcher.views.main.MainActivity;
import com.startup.eventsearcher.utils.Config;

import java.util.Objects;

public class SetExtraUserDataActivity extends AppCompatActivity implements ISetExtraUserDataView {

    private static final String TAG = "tgSetExtraUserDataAct";
    private ActivitySetExtraUserDataBinding bind;

    private UpdateUserDataPresenter updateUserDataPresenter;
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
            String displayName = Objects.requireNonNull(bind.setDataUserLogin.getEditText()).getText().toString();
            if (displayName.isEmpty()){
                displayName = "no_name";
            }
            updateUserDataPresenter.updateDisplayNameAndPhoto(displayName, imageUri);
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