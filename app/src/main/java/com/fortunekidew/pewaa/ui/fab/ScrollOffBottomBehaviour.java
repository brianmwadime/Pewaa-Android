package com.fortunekidew.pewaa.ui.fab;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by mwakima on 10/26/16.
 */

public class ScrollOffBottomBehaviour extends CoordinatorLayout.Behavior<View> {

    private int mViewHeight;
    private ObjectAnimator mAnimator;

    public ScrollOffBottomBehaviour(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {

        mViewHeight = child.getHeight();

        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {


        if(mAnimator == null || !mAnimator.isRunning()){
            int totalScroll = (dyConsumed + dyUnconsumed);

            int targetTranslation = totalScroll > 0 ? mViewHeight : 0;

            mAnimator = ObjectAnimator.ofFloat(child, "translationY", targetTranslation);
            mAnimator.start();
        }
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {

        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;


    }


}
