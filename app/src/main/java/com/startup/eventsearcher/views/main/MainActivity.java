package com.startup.eventsearcher.views.main;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.startup.eventsearcher.R;
import com.startup.eventsearcher.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "tgMainAct";

    private ActivityMainBinding bind;
    private FirebaseAnalytics firebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        bind = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (savedInstanceState == null){
            setupButtonNavigationBar();
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
        new MaterialAlertDialogBuilder(this)
                .setMessage("Вы действительно хотите выйти из аккаунта?")
                .setCancelable(true)
                .setPositiveButton(
                        "Да",
                        (dialog, id) -> {
                            dialog.cancel();
                            finish();
                        })
                .setNegativeButton(
                        "Нет",
                        (dialog, id) -> dialog.cancel())
                .show();
    }

    private void setupButtonNavigationBar() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(bind.mainFragmentContainer.getId());
        if (navHostFragment != null) {
            final NavController navController = navHostFragment.getNavController();

            bind.mainBottomNavView.setOnNavigationItemSelectedListener(item -> {
                //Отмечаем иконку на панели
                bind.mainBottomNavView.getMenu().findItem(item.getItemId()).setChecked(true);
                //Очистка бекстека
                navController.popBackStack();
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
            });
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        setupButtonNavigationBar();
    }
}