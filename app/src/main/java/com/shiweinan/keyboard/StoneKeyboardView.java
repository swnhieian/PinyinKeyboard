package com.shiweinan.keyboard;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * TODO: document your custom view class.
 */
public class StoneKeyboardView extends KeyboardView {
    private IMEService service;

    public StoneKeyboardView(Context context, AttributeSet attrs, IMEService service) {
        super(context, attrs);
        this.service = service;
    }
    class RepeatTask implements Runnable {
        View v; MotionEvent me;
        public RepeatTask(View view) {
            v = view;
        }
        public void setMotionEvent(MotionEvent me) {
            this.me = me;
        }
        public void run() {
            //System.out.println("repeat:::" + me.getX(me.getActionIndex())+"," + me.getY(me.getActionIndex()));
            service.onTouchEvent(me);
            postDelayed(this, 100);
        }
    }

    RepeatTask rt = new RepeatTask(this);
    @Override
    public boolean onTouchEvent(MotionEvent me) {
        //Log.d(this.getClass().toString(), "touch:!" + me.getX() + "," + me.getY());
        /*int action = me.getAction() & MotionEvent.ACTION_MASK;
        int actionId = me.getActionIndex();
        boolean ret = false;
        float x = me.getX(actionId);
        float y = me.getY(actionId);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                System.out.println("stoneview: down" + me.getX(me.getActionIndex())+"," + me.getY(me.getActionIndex()));
                removeCallbacks(rt);

                rt.setMotionEvent(MotionEvent.obtain(me));
                ret = service.onTouchEvent(me);
                postDelayed(rt, 100);
                break;
            case MotionEvent.ACTION_MOVE:
                System.out.println("stoneview: move" + me.getX(me.getActionIndex())+"," + me.getY(me.getActionIndex()));
                break;
            case MotionEvent.ACTION_UP:
                System.out.println("stoneview: up" + me.getX(me.getActionIndex())+"," + me.getY(me.getActionIndex()));
                removeCallbacks(rt);
                break;
            default:
                break;
        }
        return ret;*/
        service.onTouchEvent(me);
        return super.onTouchEvent(me);
        //return service.onTouchEvent(me);
        //return super.onTouchEvent(me);
    }
    List<TouchPoint> touchPoints;
    public void drawPoints(List<TouchPoint> points) {
        touchPoints = new ArrayList<>(points);
        invalidate();
    }
    public void clearDrawPoints() {
        touchPoints.clear();
        invalidate();
    }




    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10.0f);
        if (Settings.getShowTouchPoints() && touchPoints!=null) {
            for (TouchPoint tp: touchPoints) {
                canvas.drawPoint((float)tp.getX(), (float)tp.getY(), paint);
            }
        }
    }

}
