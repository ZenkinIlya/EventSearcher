package com.startup.eventsearcher.main.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.startup.eventsearcher.databinding.FragmentProfileBinding;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding bind;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bind = FragmentProfileBinding.inflate(inflater, container, false);

        fillFields();

        bind.profileSignOut.setOnClickListener(view -> {
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signOut();
        });

        return bind.getRoot();
    }

    private void fillFields() {
        bind.profileLogin.setText(CurrentPerson.getPerson().getLogin());
        bind.profilePassword.setText(CurrentPerson.getPerson().getPassword());
        bind.profileName.setText(CurrentPerson.getPerson().getName());
        bind.profileSecondName.setText(CurrentPerson.getPerson().getSurname());
        bind.profileEmail.setText(CurrentPerson.getPerson().getEmail());
    }
}