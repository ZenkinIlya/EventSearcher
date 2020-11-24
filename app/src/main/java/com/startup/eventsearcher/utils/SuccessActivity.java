package com.startup.eventsearcher.utils;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.EditText;
import android.widget.TextView;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.LoginActivity;
import com.startup.eventsearcher.introduction.IntroductionActivity;
import com.startup.eventsearcher.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SuccessActivity extends AppCompatActivity {

    @BindView(R.id.success_frame_text) TextView textViewSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            textViewSuccess.setText(String.valueOf(bundle.get("text")));
        }

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, Config.delay);

    }
}