package com.startup.eventsearcher.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.Utils.DataVerification;
import com.startup.eventsearcher.authentication.connectionToServer.test.TestRequester;
import com.startup.eventsearcher.databinding.FragmentSignupTabBinding;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;
import com.startup.eventsearcher.main.ui.profile.model.Person;
import com.startup.eventsearcher.utils.Config;
import com.startup.eventsearcher.utils.ErrorServerHandler;
import com.startup.eventsearcher.utils.SuccessActivity;

public class SignUpTabFragment extends Fragment {

    private FragmentSignupTabBinding bind;

    private static ErrorServerHandler errorServer;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bind = FragmentSignupTabBinding.inflate(inflater, container, false);

        componentListener();

        return bind.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(LoginActivity.TAG, "SignUpTabFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LoginActivity.TAG, "SignUpTabFragment onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LoginActivity.TAG, "SignUpTabFragment onDestroy");
    }

    private void componentListener() {
        bind.signUpRegistration.setOnClickListener(view -> {
            final DataVerification dataVerification = new DataVerification();
            dataVerification.verificationLogin(getContext(), bind.signUpLogin);
            dataVerification.verificationEmail(getContext(), bind.signUpEmail);
            dataVerification.verificationPassword(getContext(), bind.signUpPassword);
            dataVerification.verificationPassword(getContext(), bind.signUpConfirmPassword);
            if (dataVerification.isDataCorrect()){
                dataVerification.comparePasswords(getContext(), bind.signUpPassword, bind.signUpConfirmPassword);
            }

            //Данные корректны
            if (dataVerification.isDataCorrect()) {

                //запуск значка загрузки
                if (getActivity() != null) {
                    handler.post(() -> {
                        LoginActivity loginActivity = (LoginActivity) getActivity();
                        loginActivity.setVisibleProgressBar(View.VISIBLE);
                    });
                }

                Thread threadCompletionSignUp = new Thread(() -> {

                    //Поток, отвечающий за связь с сервером
                    Thread threadRequestToServer = new Thread(() -> {
                        try {
                            //TODO Отправка данных о пользователе на сервер и регистрация его
                            TestRequester.testRequest();
                            Thread.sleep(Config.delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    });

                    threadRequestToServer.start();
                    try {
                        //ожидание завершения потока связи с сервером
                        threadRequestToServer.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //остановка значка загрузки
                    if (getActivity() != null) {
                        handler.post(() -> {
                            LoginActivity loginActivity = (LoginActivity) getActivity();
                            loginActivity.setVisibleProgressBar(View.INVISIBLE);
                        });
                    }

                    errorServer = TestRequester.getErrorServerHandler();
                    //Если сервер не ответил или ответил ошибкой
                    if (errorServer.getCode() != 200) {
                        handler.post(() -> Toast.makeText(getContext(), errorServer.getDescription(), Toast.LENGTH_LONG).show());
                    } else {
                        CurrentPerson.setPerson(new Person(
                                bind.signUpLogin.getText().toString(),
                                bind.signUpPassword.getText().toString(),
                                "Pacient",
                                "Navalniy",
                                bind.signUpEmail.getText().toString()
                        ));
                        Intent intent = new Intent(getContext(), SuccessActivity.class);
                        intent.putExtra("text", getResources().getString(R.string.registrationSuccess));
                        startActivity(intent);
                    }
                });

                threadCompletionSignUp.start();
            }
        });

        bind.signUpLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bind.signUpLogin.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bind.signUpEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bind.signUpEmail.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bind.signUpPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bind.signUpPassword.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        bind.signUpConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bind.signUpConfirmPassword.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
}
