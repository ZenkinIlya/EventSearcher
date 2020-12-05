package com.startup.eventsearcher.utils.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class MovingAnimation extends Animation {
    final int startX;
    final int deltaX;
    final int startY;
    final int deltaY;
    View view;

    public MovingAnimation(View view, int deltaX, int deltaY) {
        this.view = view;
        this.deltaX = deltaX;
        startX = (int) view.getX();
        this.deltaY = deltaY;
        startY = (int) view.getY();

    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.setX(startX + deltaX * interpolatedTime);
        view.setY(startY + deltaY * interpolatedTime);
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
