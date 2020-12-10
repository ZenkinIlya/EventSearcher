package com.startup.eventsearcher.introduction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.authentication.LoginActivity;
import com.startup.eventsearcher.utils.Config;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IntroductionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(IntroductionActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, Config.delay);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}