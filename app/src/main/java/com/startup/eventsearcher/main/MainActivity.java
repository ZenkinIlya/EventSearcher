package com.startup.eventsearcher.main;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.EventFragment;
import com.startup.eventsearcher.main.ui.map.MapsFragment;
import com.startup.eventsearcher.main.ui.profile.ProfileFragment;
import com.startup.eventsearcher.main.ui.subscribe.SubscribeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myMain";

    @BindView(R.id.main_fragment_container) FrameLayout fragmentContainer;
    @BindView(R.id.main_bottom_nav_view) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Design.setStatusBarGradient(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(MapsFragment.newInstance("", ""));

        if (savedInstanceState == null){
//            setupButtonNavigationBar();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы действительно хотите выйти из аккаунта?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Да",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });

        builder.setNegativeButton(
                "Нет",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_item_map:
                            openFragment(MapsFragment.newInstance("", ""));
                            return true;
                        case R.id.nav_item_events:
                            openFragment(EventFragment.newInstance("", ""));
                            return true;
                        case R.id.nav_item_favorite:
                            openFragment(SubscribeFragment.newInstance("", ""));
                            return true;
                        case R.id.nav_item_profile:
                            openFragment(ProfileFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(fragmentContainer.getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setupButtonNavigationBar() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(fragmentContainer.getId());
        if (navHostFragment != null) {
            final NavController navController = navHostFragment.getNavController();
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    bottomNavigationView.getMenu().findItem(item.getItemId()).setChecked(true);
                    switch (item.getItemId()){
                        case R.id.nav_item_map:
                            navController.navigate(R.id.mapsFragment);
                            break;
                        case R.id.nav_item_events:
                            navController.navigate(R.id.eventFragment);
                            break;
                        case R.id.nav_item_favorite:
                            navController.navigate(R.id.subscribeFragment);
                            break;
                        case R.id.nav_item_profile:
                            navController.navigate(R.id.profileFragment);
                            break;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
//        setupButtonNavigationBar();
    }
}