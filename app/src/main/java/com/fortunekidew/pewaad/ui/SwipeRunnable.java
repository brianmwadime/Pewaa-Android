package com.fortunekidew.pewaad.ui;

/**
 * Created by mwakima on 10/22/16.
 */

public abstract class SwipeRunnable implements Runnable {
    int mDirection;

    protected SwipeRunnable(int direction) {
        mDirection = direction;
    }

    @Override
    public abstract void run();

    public int getDirection() {
        return mDirection;
    }
}
