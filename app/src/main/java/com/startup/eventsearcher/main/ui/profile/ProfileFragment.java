package com.startup.eventsearcher.main.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment {

    @BindView(R.id.profile_login)
    TextView textViewLogin;
    @BindView(R.id.profile_password)
    TextView textViewPassword;
    @BindView(R.id.profile_name)
    TextView textViewName;
    @BindView(R.id.profile_second_name)
    TextView textViewSecondName;
    @BindView(R.id.profile_email)
    TextView textViewEmail;

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString("ARG_PARAM1", param1);
        args.putString("ARG_PARAM2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, view);

        fillFields();

        return view;
    }

    private void fillFields() {
        textViewLogin.setText(CurrentPerson.getPerson().getLogin());
        textViewPassword.setText(CurrentPerson.getPerson().getPassword());
        textViewName.setText(CurrentPerson.getPerson().getName());
        textViewSecondName.setText(CurrentPerson.getPerson().getSurname());
        textViewEmail.setText(CurrentPerson.getPerson().getEmail());
    }
}