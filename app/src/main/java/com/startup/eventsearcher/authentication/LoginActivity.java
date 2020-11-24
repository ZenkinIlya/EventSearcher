package com.startup.eventsearcher.authentication;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.startup.eventsearcher.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "Login";

    @BindView(R.id.tab_layout) TabLayout tabLayout;
    @BindView(R.id.view_pager) ViewPager viewPager;
    @BindView(R.id.login_progress_bar) FrameLayout progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        tabLayout.addTab(tabLayout.newTab().setText(this.getString(R.string.entry)));
        tabLayout.addTab(tabLayout.newTab().setText(this.getString(R.string.registrationWithoutUnderline)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final LoginAdapter adapter = new LoginAdapter(getSupportFragmentManager(), this, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        //Анимация tabLayout
        tabLayout.setTranslationX(800);
        tabLayout.setAlpha(0);
        tabLayout.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();

    }

    public void setVisibleProgressBar(int visible) {
        progressBar.setVisibility(visible);
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
