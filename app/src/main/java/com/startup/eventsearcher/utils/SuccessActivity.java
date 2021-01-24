package com.startup.eventsearcher.utils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.startup.eventsearcher.databinding.ActivitySuccessBinding;
import com.startup.eventsearcher.main.MainActivity;

public class SuccessActivity extends AppCompatActivity {

    private ActivitySuccessBinding bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivitySuccessBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            bind.successFrameText.setText(String.valueOf(bundle.get("text")));
        }

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SuccessActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, Config.delay);

    }
}