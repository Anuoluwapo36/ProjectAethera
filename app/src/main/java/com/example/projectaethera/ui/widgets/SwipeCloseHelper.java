package com.example.projectaethera.ui.widgets;

import android.view.MotionEvent;
import android.view.View;

/**
 * Helper that listens for a downward swipe on a view and closes the screen when the swipe is long enough.
 * Used for bottom sheets or full-screen overlays that dismiss on swipe down.
 */
public class SwipeCloseHelper {

    private float startY;
    private final View targetView;
    private final OnSwipeCloseListener listener;
    private final boolean consumeAllTouches;

    /**
     * Called when the user swipes down far enough to close the current screen.
     */
    public interface OnSwipeCloseListener {
        void onSwipeClose();
    }

    /**
     * Attaches swipe-to-close handling and consumes all touch events by default.
     *
     * @param targetView view that receives touch events
     * @param listener   called when swipe-down threshold is reached
     */
    public SwipeCloseHelper(View targetView, OnSwipeCloseListener listener) {
        this(targetView, listener, true);
    }

    /**
     * Attaches swipe-to-close handling with control over whether touches are consumed.
     *
     * @param targetView         view that receives touch events
     * @param listener           called when swipe-down threshold is reached
     * @param consumeAllTouches  if true, the touch listener returns true so parent views do not also handle the gesture
     */
    public SwipeCloseHelper(View targetView, OnSwipeCloseListener listener, boolean consumeAllTouches) {
        this.targetView = targetView;
        this.listener = listener;
        this.consumeAllTouches = consumeAllTouches;
        enable();
    }

    private void enable() {
        targetView.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    startY = event.getRawY();
                    return consumeAllTouches;
                case MotionEvent.ACTION_UP:
                    if (event.getRawY() - startY > 120 && listener != null) {
                        listener.onSwipeClose();
                        return consumeAllTouches;
                    }
                    return consumeAllTouches;
                default:
                    return consumeAllTouches;
            }
        });
    }
}
