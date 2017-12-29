package com.shiweinan.keyboard;

import android.text.method.Touch;

/**
 * Created by Weinan on 2017/12/26.
 */

public class TouchPoint {
    double x, y;
    long time;
    public TouchPoint(float x, float y) {
        this.x = x;
        this.y = y;
        this.time = System.currentTimeMillis();
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
}
