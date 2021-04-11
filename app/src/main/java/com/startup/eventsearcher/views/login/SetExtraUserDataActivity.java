package com.startup.eventsearcher.views.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.databinding.ActivitySetExtraUserDataBinding;
import com.startup.eventsearcher.presenters.userData.StoragePhotoPresenter;
import com.startup.eventsearcher.presenters.userData.UpdateFirebaseUserDataPresenter;
import com.startup.eventsearcher.utils.Config;
import com.startup.eventsearcher.utils.converters.ImageConverter;
import com.startup.eventsearcher.utils.user.UserDataVerification;
import com.startup.eventsearcher.views.main.MainActivity;
import com.startup.eventsearcher.views.profile.IStoragePhotoView;

import java.util.Objects;

public class SetExtraUserDataActivity extends AppCompatActivity implements ISetExtraUserDataView, IStoragePhotoView {

    private static final String TAG = "tgSetExtraUserDataAct";
    private ActivitySetExtraUserDataBinding bind;

    private UpdateFirebaseUserDataPresenter updateFirebaseUserDataPresenter;
    private StoragePhotoPresenter storagePhotoPresenter;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySetExtraUserDataBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        updateFirebaseUserDataPresenter = new UpdateFirebaseUserDataPresenter(this,
                new UserDataVerification(this));
        storagePhotoPresenter = new StoragePhotoPresenter(this);

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
            byte[] bytes = ImageConverter.compressImage(this, imageUri);
            storagePhotoPresenter.savePhotoInStorage(bytes);
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
    public void onSavePhotoInStorage() {
        updateFirebaseUserDataPresenter.setUriPhotoInFirebaseAuth(imageUri);
    }

    @Override
    public void onLoadPhotoFromStorage(Bitmap bitmap) {
        //Загрузка аватрки не выполняется в данном Activity
    }

    @Override
    public void onSetLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSetUriPhoto() {
        String displayName = Objects.requireNonNull(bind.setDataUserLogin.getEditText()).getText().toString();
        updateFirebaseUserDataPresenter.setLoginInFirebaseAuth(displayName);
    }

    @Override
    public void onErrorLogin(String message) {
        bind.setDataUserLogin.setError(message);
    }

    @Override
    public void onError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean show) {
        if (show){
            bind.setDataUserLoading.setVisibility(View.VISIBLE);
        }else {
            bind.setDataUserLoading.setVisibility(View.INVISIBLE);
        }
    }
}