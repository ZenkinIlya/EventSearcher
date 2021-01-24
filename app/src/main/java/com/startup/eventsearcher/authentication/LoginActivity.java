package com.startup.eventsearcher.authentication;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "Login";

    private ActivityLoginBinding bind;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bind = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        bind.tabLayout.addTab(bind.tabLayout.newTab().setText(this.getString(R.string.entry)));
        bind.tabLayout.addTab(bind.tabLayout.newTab().setText(this.getString(R.string.registrationWithoutUnderline)));
        bind.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(),  bind.tabLayout.getTabCount());
        bind.viewPager.setAdapter(adapter);

        bind.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(bind.tabLayout));
        bind.tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(bind.viewPager));

        //Анимация bind.tabLayout
        bind.tabLayout.setTranslationX(800);
        bind.tabLayout.setAlpha(0);
        bind.tabLayout.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();

    }

    public void setVisibleProgressBar(int visible) {
        bind.loginProgressBar.setVisibility(visible);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "LoginActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "LoginActivity onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "LoginActivity onDestroy");
    }
}
