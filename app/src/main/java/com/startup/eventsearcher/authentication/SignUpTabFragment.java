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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.Utils.DataVerification;
import com.startup.eventsearcher.authentication.connectionToServer.test.TestRequester;
import com.startup.eventsearcher.main.ui.profile.model.CurrentPerson;
import com.startup.eventsearcher.main.ui.profile.model.Person;
import com.startup.eventsearcher.utils.Config;
import com.startup.eventsearcher.utils.ErrorServerHandler;
import com.startup.eventsearcher.utils.SuccessActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignUpTabFragment extends Fragment {

    @BindView(R.id.sign_up_login) EditText login;
    @BindView(R.id.sign_up_email) EditText email;
    @BindView(R.id.sign_up_password) EditText password;
    @BindView(R.id.sign_up_confirm_password) EditText confirmPassword;
    @BindView(R.id.sign_up_registration) Button registration;

    private static ErrorServerHandler errorServer;
    private Handler handler;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.signup_tab_fragment, container, false);
        ButterKnife.bind(this, root);

        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DataVerification dataVerification = new DataVerification();
                dataVerification.verificationLogin(getContext(), login);
                dataVerification.verificationEmail(getContext(), email);
                dataVerification.verificationPassword(getContext(), password);
                dataVerification.verificationPassword(getContext(), confirmPassword);
                if (dataVerification.isDataCorrect()){
                    dataVerification.comparePasswords(getContext(), password, confirmPassword);
                }

                //Данные корректны
                if (dataVerification.isDataCorrect()) {

                    //запуск значка загрузки
                    if (getActivity() != null) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                LoginActivity loginActivity = (LoginActivity) getActivity();
                                loginActivity.setVisibleProgressBar(View.VISIBLE);
                            }
                        });
                    }

                    Thread threadCompletionSignUp = new Thread(new Runnable() {
                        @Override
                        public void run() {

                            //Поток, отвечающий за связь с сервером
                            Thread threadRequestToServer = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        //TODO Отправка данных о пользователе на сервер и регистрация его
                                        TestRequester.testRequest();
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
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        LoginActivity loginActivity = (LoginActivity) getActivity();
                                        loginActivity.setVisibleProgressBar(View.INVISIBLE);
                                    }
                                });
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
                            } else {
                                CurrentPerson.setPerson(new Person(
                                        login.getText().toString(),
                                        password.getText().toString(),
                                        "Pacient",
                                        "Navalniy",
                                        email.getText().toString()
                                ));
                                Intent intent = new Intent(getContext(), SuccessActivity.class);
                                intent.putExtra("text", getResources().getString(R.string.registrationSuccess));
                                startActivity(intent);
                            }
                        }
                    });

                    threadCompletionSignUp.start();
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

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email.setError(null);
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
            public void afterTextChanged(Editable editable) {}
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                confirmPassword.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {}
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
}
