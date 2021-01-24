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

import com.startup.eventsearcher.authentication.Utils.DataVerification;
import com.startup.eventsearcher.authentication.connectionToServer.test.TestRequester;
import com.startup.eventsearcher.databinding.FragmentSigninTabBinding;
import com.startup.eventsearcher.main.MainActivity;
import com.startup.eventsearcher.utils.Config;
import com.startup.eventsearcher.utils.ErrorServerHandler;

public class SignInTabFragment extends Fragment {

    private FragmentSigninTabBinding bind;

    private static ErrorServerHandler errorServer;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bind = FragmentSigninTabBinding.inflate(inflater, container, false);

        componentListener();

        bind.signInLogin.setTranslationX(800);
        bind.signInPassword.setTranslationX(800);
        bind.signInForgetPassword.setTranslationX(800);
        bind.signInToComeIn.setTranslationX(800);

        bind.signInLogin.setAlpha(0);
        bind.signInPassword.setAlpha(0);
        bind.signInForgetPassword.setAlpha(0);
        bind.signInToComeIn.setAlpha(0);

        bind.signInLogin.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        bind.signInPassword.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        bind.signInForgetPassword.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        bind.signInToComeIn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();

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
        Log.d(LoginActivity.TAG, "SignInTabFragment onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(LoginActivity.TAG, "SignInTabFragment onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LoginActivity.TAG, "SignInTabFragment onDestroy");
    }

    private void componentListener() {
        bind.signInToComeIn.setOnClickListener(view -> {
            final DataVerification dataVerification = new DataVerification();
            dataVerification.verificationLogin(getContext(), bind.signInLogin);
            dataVerification.verificationPassword(getContext(), bind.signInPassword);

            //Данные корректны
            if (dataVerification.isDataCorrect()) {

                //запуск значка загрузки
                if (getActivity() != null) {
                    LoginActivity loginActivity = (LoginActivity) getActivity();
                    loginActivity.setVisibleProgressBar(View.VISIBLE);
                }

                Thread threadCompletionSignIn = new Thread(() -> {

                    //Поток, отвечающий за связь с сервером
                    Thread threadRequestToServer = new Thread(() -> {
                        try {
                            //TODO Проверка пользователя на наличие в базе сервера
                            TestRequester.testRequest();
/*                                        SignInMessage signInMessage = new SignInMessage(
                                    String.valueOf(editTextLogin.getText()), String.valueOf(editTextPassword.getText()));
                            answerServer = AuthenticationRequester.signInRequest(signInMessage);*/

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
                        LoginActivity loginActivity= (LoginActivity) getActivity();
                        loginActivity.setVisibleProgressBar(View.INVISIBLE);
                    }

                    errorServer = TestRequester.getErrorServerHandler();
                    //Если сервер не ответил или ответил ошибкой
                    if (errorServer.getCode() != 200) {
                        handler.post(() -> Toast.makeText(getContext(), errorServer.getDescription(), Toast.LENGTH_LONG).show());
                    }else {
                        Intent intent = new Intent(getContext(), MainActivity.class);
                        intent.putExtra("bind.signInLogin", String.valueOf(bind.signInLogin.getText()));
                        startActivity(intent);

                    }
                });

                threadCompletionSignIn.start();
            }
        });

        bind.signInLogin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bind.signInLogin.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        bind.signInPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                bind.signInPassword.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
}