package com.startup.eventsearcher.views.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.startup.eventsearcher.models.user.User;
import com.startup.eventsearcher.utils.user.FirebaseAuthUserGetter;
import com.startup.eventsearcher.databinding.FragmentProfileBinding;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding bind;

    private FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProfileBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        componentListeners();

        return bind.getRoot();
    }

    private void componentListeners() {
        bind.profileSignOut.setOnClickListener(view -> {
            firebaseAuth.signOut();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        fillFields();
    }

    private void fillFields() {
        User user = FirebaseAuthUserGetter.getUserFromFirebaseAuth();
        String email = FirebaseAuthUserGetter.getUserEmailFromFirebaseAuth();
        bind.profileLogin.setText(user.getLogin());
        bind.profileEmail.setText(email);
//        bind.profilePhoto.setImageURI(user.getUriPhoto());
    }
}