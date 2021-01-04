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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.Utils.DataVerification;
import com.startup.eventsearcher.authentication.connectionToServer.test.TestRequester;
import com.startup.eventsearcher.main.MainActivity;
import com.startup.eventsearcher.utils.Config;
import com.startup.eventsearcher.utils.ErrorServerHandler;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignInTabFragment extends Fragment {

    @BindView(R.id.sign_in_login) EditText login;
    @BindView(R.id.sign_in_password) EditText password;
    @BindView(R.id.sign_in_forget_password) TextView forgetPassword;
    @BindView(R.id.sign_in_to_come_in) Button toComeIn;

    private static ErrorServerHandler errorServer;
    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signin_tab_fragment, container, false);
        ButterKnife.bind(this, root);

        login.setTranslationX(800);
        password.setTranslationX(800);
        forgetPassword.setTranslationX(800);
        toComeIn.setTranslationX(800);

        login.setAlpha(0);
        password.setAlpha(0);
        forgetPassword.setAlpha(0);
        toComeIn.setAlpha(0);

        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        password.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        forgetPassword.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        toComeIn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();

        toComeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DataVerification dataVerification = new DataVerification();
                dataVerification.verificationLogin(getContext(), login);
                dataVerification.verificationPassword(getContext(), password);

                //Данные корректны
                if (dataVerification.isDataCorrect()) {

                    //запуск значка загрузки
                    if (getActivity() != null) {
                        LoginActivity loginActivity = (LoginActivity) getActivity();
                        loginActivity.setVisibleProgressBar(View.VISIBLE);
                    }

                    Thread threadCompletionSignIn = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            //Поток, отвечающий за связь с сервером
                            Thread threadRequestToServer = new Thread(new Runnable() {
                                @Override
                                public void run() {
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
                                LoginActivity loginActivity = (LoginActivity) getActivity();
                                loginActivity.setVisibleProgressBar(View.INVISIBLE);
                            }

                            errorServer = TestRequester.getErrorServerHandler();
                            //Если сервер не ответил или ответил ошибкой
                            if (errorServer.getCode() != 200) {
                                 handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getContext(), errorServer.getDescription(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra("login", String.valueOf(login.getText()));
                                startActivity(intent);

                            }
                        }
                    });

                    threadCompletionSignIn.start();
                }
            }
        });

        login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                login.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return root;
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
}