package com.startup.eventsearcher.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.EventFragment;
import com.startup.eventsearcher.main.ui.map.MapsFragment;
import com.startup.eventsearcher.main.ui.profile.ProfileFragment;
import com.startup.eventsearcher.main.ui.subscribe.SubscribeFragment;
import com.startup.eventsearcher.utils.Design;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.main_frame) FrameLayout mainFrame;
    @BindView(R.id.main_nav) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Design.setStatusBarGradient(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getSupportFragmentManager().beginTransaction().replace(mainFrame.getId(), new MapsFragment()).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch (item.getItemId()){
                    case R.id.nav_item_map:
                        selectedFragment = new MapsFragment();
                        break;
                    case R.id.nav_item_events:
                        selectedFragment = new EventFragment();
                        break;
                    case R.id.nav_item_favorite:
                        selectedFragment = new SubscribeFragment();
                        break;
                    case R.id.nav_item_profile:
                        selectedFragment = new ProfileFragment();
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(mainFrame.getId(), selectedFragment).commit();
                }

                return true;
            }
        });
    }
}