package com.example.projectaethera.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Custom camera shutter button drawn as two circles.
 * The inner red circle shrinks slightly while the user is pressing down.
 */
public class CameraPressCircleView extends View {
    private Paint outerpaint;
    private Paint innerpaint;
    private boolean ispressed = false;

    /**
     * Creates the shutter button from XML layout attributes.
     */
    public CameraPressCircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private  void  init(){
        outerpaint = new Paint();
        outerpaint.setColor(Color.BLACK);
        outerpaint.setStyle(Paint.Style.STROKE);
        outerpaint.setStrokeWidth(12);
        outerpaint.setAntiAlias(true);

        innerpaint = new Paint();
        innerpaint.setColor(Color.RED);
        innerpaint.setStyle(Paint.Style.FILL);
        innerpaint.setAntiAlias(true);
    }

    /**
     * Draws the outer ring and inner filled circle; inner size depends on press state.
     */
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() /2;
        int centerY = getHeight() / 2;
        int maxRadius = Math.min(getWidth(), getHeight())/ 2 -20;
        canvas.drawCircle(centerX,centerY,maxRadius, outerpaint);

        int innerRadius = ispressed ? maxRadius -25 : maxRadius -15;
        canvas.drawCircle(centerX,centerY,innerRadius,innerpaint);
    }


    /**
     * Marks the button as pressed on touch down and fires a click on touch up.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                ispressed = true;
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
                ispressed=false;
                invalidate();
                performClick();
                return true;

            case MotionEvent.ACTION_CANCEL:
                ispressed=false;
                invalidate();
                return true;

        }
        return super.onTouchEvent(event);
    }

    /**
     * Required for accessibility; delegates to the parent click handling.
     */
    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }
}
