package com.startup.eventsearcher.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.google.android.gms.dynamic.SupportFragmentWrapper;
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

    private static final String TAG = "myMain";

    @BindView(R.id.main_fragment_container) FrameLayout fragmentContainer;
    @BindView(R.id.main_bottom_nav_view) BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Design.setStatusBarGradient(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState == null){
            setupButtonNavigationBar();
        }
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
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void setupButtonNavigationBar() {
      /*  AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mapsFragment, R.id.eventFragment, R.id.subscribeFragment, R.id.profileFragment).build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(fragmentContainer.getId());
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);*/

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
        setupButtonNavigationBar();
    }
}