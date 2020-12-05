package com.startup.eventsearcher.utils.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation {
    final int startWidth;
    final int deltaWidth;
    View view;

    public ResizeAnimation(View view, int deltaWidth) {
        this.view = view;
        this.deltaWidth = deltaWidth;
        startWidth = view.getWidth();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.getLayoutParams().width = (int) (startWidth + deltaWidth * interpolatedTime);
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
