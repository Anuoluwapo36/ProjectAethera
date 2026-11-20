package com.example.projectaethera.ui.widgets;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;

/**
 * Grid layout manager that lays out items in a grid but never scrolls on its own.
 * Used when the RecyclerView should grow to full height inside a parent ScrollView.
 */
public class NonScrollableGridLayoutManager extends GridLayoutManager {

    /**
     * Creates a non-scrollable grid with the given number of columns.
     *
     * @param context   context for the RecyclerView
     * @param spanCount number of columns in the grid
     */
    public NonScrollableGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    /**
     * Disables vertical scrolling so the parent can scroll instead.
     */
    @Override
    public boolean canScrollVertically() {
        return false;
    }

    /**
     * Disables horizontal scrolling for this grid.
     */
    @Override
    public boolean canScrollHorizontally() {
        return false;
    }
}
