package com.startup.eventsearcher.main.ui.events.createEvent;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.startup.eventsearcher.R;
import com.startup.eventsearcher.main.ui.events.model.Category;

import java.util.ArrayList;

public class CategoryAdapter extends PagerAdapter {

    private final Context context;
    private final ArrayList<Category> categoryArrayList;

    public CategoryAdapter(Context context, ArrayList<Category> categoryArrayList) {
        this.context = context;
        this.categoryArrayList = categoryArrayList;
    }

    @Override
    public int getCount() {
        return categoryArrayList.size();
    }
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(layoutParams);

        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(categoryArrayList.get(position).getCategoryImage());
        relativeLayout.addView(imageView);

        TextView textView = new TextView(context);
        textView.setText(categoryArrayList.get(position).getCategoryName());
        textView.setTextColor(ContextCompat.getColor(context, R.color.white));
        textView.setTextSize(35);
        textView.setTypeface(null, Typeface.BOLD);

        RelativeLayout.LayoutParams layoutParamsNameCategory = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsNameCategory.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutParamsNameCategory.addRule(RelativeLayout.ALIGN_PARENT_START);
        layoutParamsNameCategory.setMargins(20,10,10,10);

        textView.setLayoutParams(layoutParamsNameCategory);
        relativeLayout.addView(textView);

        container.addView(relativeLayout, 0);

        return relativeLayout;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }
}

