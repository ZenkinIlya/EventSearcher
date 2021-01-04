package com.startup.eventsearcher.utils.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation {
    final int startWidth;
    final int deltaWidth;
    final int startHeight;
    final int deltaHeight;
    View view;

    public ResizeAnimation(View view, int deltaWidth, int deltaHeight) {
        this.view = view;
        this.deltaWidth = deltaWidth;
        startWidth = view.getWidth();
        this.deltaHeight = deltaHeight;
        startHeight = view.getHeight();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.getLayoutParams().width = (int) (startWidth + deltaWidth * interpolatedTime);
        view.getLayoutParams().height = (int) (startHeight + deltaHeight * interpolatedTime);
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
