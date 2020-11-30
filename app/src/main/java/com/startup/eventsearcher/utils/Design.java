package com.startup.eventsearcher.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.startup.eventsearcher.R;

public class Design {

    public static void setStatusBarGradient(Activity activity) {
        Window window = activity.getWindow();
        Drawable background = ResourcesCompat.getDrawable(activity.getResources(), R.drawable.gradient, null);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, android.R.color.transparent));
        window.setNavigationBarColor(ContextCompat.getColor(activity, android.R.color.transparent));
        window.setBackgroundDrawable(background);
    }

}
