package com.startup.eventsearcher.views.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.databinding.FragmentProfileBinding;
import com.startup.eventsearcher.models.user.User;
import com.startup.eventsearcher.presenters.userData.StoragePhotoPresenter;
import com.startup.eventsearcher.presenters.userData.UpdateFirebaseUserDataPresenter;
import com.startup.eventsearcher.utils.user.FirebaseAuthUserGetter;
import com.startup.eventsearcher.utils.user.UserDataVerification;
import com.startup.eventsearcher.views.login.ISetExtraUserDataView;

public class ProfileFragment extends Fragment implements ISetExtraUserDataView, IStoragePhotoView {

    private FragmentProfileBinding bind;

    private FirebaseAuth firebaseAuth;
    private UpdateFirebaseUserDataPresenter updateFirebaseUserDataPresenter;
    private StoragePhotoPresenter storagePhotoPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProfileBinding.inflate(inflater, container, false);

        storagePhotoPresenter = new StoragePhotoPresenter(this);
        updateFirebaseUserDataPresenter = new UpdateFirebaseUserDataPresenter(this,
                new UserDataVerification(getContext()));

        firebaseAuth = FirebaseAuth.getInstance();
        componentListeners();

        return bind.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        storagePhotoPresenter.loadPhotoFromStorage(
                FirebaseAuthUserGetter.getUserFromFirebaseAuth().getUid(),
                BitmapFactory.decodeResource(getResources(), R.drawable.default_avatar));
        fillFields();
    }

    @Override
    public void onSavePhotoInStorage() {
        //Вызывается после смены аватарки
    }

    @Override
    public void onLoadPhotoFromStorage(Bitmap bitmap) {
        //Вызывается после загрузки аватарки
        bind.profilePhoto.setImageBitmap(bitmap);

    }

    @Override
    public void onSetLogin() {
        //Не вызывается так как смена логина запрещена
    }

    @Override
    public void onSetUriPhoto() {
        //Вызывается после установки uriPhoto у CurrentUser
    }

    @Override
    public void onErrorLogin(String message) {
        //Не вызывается так как смена логина запрещена
    }

    @Override
    public void onError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoading(boolean show) {
        if (show){
            bind.profileLoading.setVisibility(View.VISIBLE);
        }else {
            bind.profileLoading.setVisibility(View.INVISIBLE);
        }
    }

    private void componentListeners() {
        bind.profileSignOut.setOnClickListener(view -> {
            firebaseAuth.signOut();
            getActivity().finish();
        });
    }

    private void fillFields() {
        User user = FirebaseAuthUserGetter.getUserFromFirebaseAuth();
        String email = FirebaseAuthUserGetter.getUserEmailFromFirebaseAuth();
        bind.profileLogin.setText(user.getLogin());
        bind.profileEmail.setText(email);
    }
}