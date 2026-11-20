package com.example.projectaethera.utils;



import android.view.View;



import androidx.core.widget.NestedScrollView;



/**

 * Keeps form fields visible when the soft keyboard opens.

 * Attach this to login or sign-up screens so focused inputs scroll above the keyboard

 * instead of staying hidden behind it.

 */

public final class KeyboardScrollHelper {



    private KeyboardScrollHelper() {

    }



    /**

     * Scrolls the given scroll view whenever any of the passed fields receives focus.

     * Call once after inflating a form layout that lives inside a NestedScrollView.

     *

     * @param scrollView the scroll container that should move its content

     * @param fields     one or more input views that should stay visible on focus

     */

    public static void attach(NestedScrollView scrollView, View... fields) {

        for (View field : fields) {

            field.setOnFocusChangeListener((v, hasFocus) -> {

                if (hasFocus) {

                    scrollToView(scrollView, v);

                }

            });

        }

    }



    // Waits briefly for the keyboard, then scrolls so the focused field sits near the top.

    private static void scrollToView(NestedScrollView scrollView, View target) {

        scrollView.postDelayed(() -> {

            int[] scrollPos = new int[2];

            int[] targetPos = new int[2];

            scrollView.getLocationOnScreen(scrollPos);

            target.getLocationOnScreen(targetPos);



            int offset = targetPos[1] - scrollPos[1] - scrollView.getPaddingTop() - 120;

            if (offset > 0) {

                scrollView.smoothScrollBy(0, offset);

            }

        }, 120);

    }

}

