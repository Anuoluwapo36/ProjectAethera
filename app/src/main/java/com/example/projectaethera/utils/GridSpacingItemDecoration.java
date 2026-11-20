package com.example.projectaethera.utils;



import android.graphics.Rect;

import android.view.View;



import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;



/**

 * Adds even spacing between items in a grid-style RecyclerView.

 * Attach this as an {@link RecyclerView.ItemDecoration} when you want consistent gaps

 * between columns and rows, such as in a photo grid or profile posts layout.

 */

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {



    private final int spanCount;

    private final int spacingPx;

    private final boolean includeEdge;



    /**

     * Creates spacing rules for a grid with a fixed number of columns.

     *

     * @param spanCount   how many columns the grid has

     * @param spacingPx   gap size in pixels between items

     * @param includeEdge true to also pad the outer edges of the grid

     */

    public GridSpacingItemDecoration(int spanCount, int spacingPx, boolean includeEdge) {

        this.spanCount = spanCount;

        this.spacingPx = spacingPx;

        this.includeEdge = includeEdge;

    }



    @Override

    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,

                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        if (position == RecyclerView.NO_POSITION) {

            return;

        }



        int column = position % spanCount;

        if (includeEdge) {

            outRect.left = spacingPx - column * spacingPx / spanCount;

            outRect.right = (column + 1) * spacingPx / spanCount;

            if (position < spanCount) {

                outRect.top = spacingPx;

            }

            outRect.bottom = spacingPx;

        } else {

            outRect.left = column * spacingPx / spanCount;

            outRect.right = spacingPx - (column + 1) * spacingPx / spanCount;

            if (position >= spanCount) {

                outRect.top = spacingPx;

            }

        }

    }

}

