package com.startup.eventsearcher.introduction;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.LoginActivity;
import com.startup.eventsearcher.utils.Config;

public class IntroductionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(IntroductionActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, Config.delay);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}