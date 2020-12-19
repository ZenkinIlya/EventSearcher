package com.startup.eventsearcher.main.ui.map.createEvent;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textfield.TextInputLayout;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.connectionToServer.test.TestRequester;
import com.startup.eventsearcher.utils.Config;
import com.startup.eventsearcher.utils.ErrorServerHandler;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventCreatorActivity extends AppCompatActivity {

    @BindView(R.id.event_creator_header)
    TextInputLayout textFieldHeader;
    @BindView(R.id.event_creator_category)
    ViewPager viewPagerCategory;
    @BindView(R.id.event_creator_location)
    TextInputLayout textFieldLocation;
    @BindView(R.id.event_creator_start_time)
    TextInputLayout textFieldStartTime;
    @BindView(R.id.event_creator_comment)
    TextInputLayout textFieldComment;
    @BindView(R.id.event_creator_accept_btn)
    Button buttonAccept;
    @BindView(R.id.event_creator_progress_bar)
    FrameLayout frameLayoutProgressBar;

    private static ErrorServerHandler errorServer;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creator);
        ButterKnife.bind(this);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Создание эвента");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        componentListener();
    }

    private void componentListener() {
        textFieldLocation.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        textFieldStartTime.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisibleProgressBar(View.VISIBLE);
                Thread threadSendEventToServer = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //Поток, отвечающий за связь с сервером
                        Thread threadRequestToServer = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    //TODO Отправка на сервер нового эвента в отдельном потоке
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

                        setVisibleProgressBar(View.INVISIBLE);

                        errorServer = TestRequester.getErrorServerHandler();
                        //Если сервер не ответил или ответил ошибкой
                        if (errorServer.getCode() != 200) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(view.getContext(), errorServer.getDescription(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }else {
                            finish();
                        }
                    }
                });
                threadSendEventToServer.start();
            }
        });
    }

    public void setVisibleProgressBar(int visible) {
        frameLayoutProgressBar.setVisibility(visible);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
