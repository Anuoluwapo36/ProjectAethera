package com.example.projectaethera.ui.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * Custom circular profile image view with a thin ring drawn around the photo.
 * The ring color can be changed to show status (for example seen vs unseen stories).
 */
public class ProfileCircleWidgets extends AppCompatImageView {

    private final Paint strokePaint;
    private final int STROKE_WIDTH = 1;
    private int strokeColor = Color.LTGRAY;

    /**
     * Creates the view with only a context (used from code).
     */
    public ProfileCircleWidgets(Context context) {
        this(context, null);
    }

    /**
     * Creates the view from XML layout attributes.
     */
    public ProfileCircleWidgets(Context context, AttributeSet attrs) {
        super(context, attrs);
        strokePaint = new Paint();
        strokePaint.setColor(strokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(STROKE_WIDTH);
    }

    /**
     * Changes the color of the circle ring and redraws the view.
     *
     * @param color Android color int for the ring stroke
     */
    public void setRingColor(int color) {
        strokeColor = color;
        strokePaint.setColor(color);
        invalidate();
    }

    /**
     * Draws the profile image first, then paints the ring on top.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) - (STROKE_WIDTH / 2f);

        canvas.drawCircle(centerX, centerY, radius, strokePaint);
    }
}
