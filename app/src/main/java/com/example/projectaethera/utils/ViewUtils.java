package com.example.projectaethera.utils;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.core.content.ContextCompat;

import com.example.projectaethera.R;

/**
 * Small UI helpers for common view updates across the app.
 * Use these when you need to refresh dots, loading buttons, or follow-button styling
 * without copying the same view logic into every screen.
 */
public class ViewUtils {

    /**
     * Builds or updates the page-indicator dots below a ViewPager.
     * Call this whenever the current page or total page count changes.
     * Hides the container automatically when there is only one page.
     *
     * @param dotContainer the horizontal layout that holds the dot views
     * @param totalPages   how many pages the pager has
     * @param currentPage  the page index that should look active (0-based)
     * @param context      needed to create views and read display density
     */
    public static void updateDots(LinearLayout dotContainer, int totalPages, int currentPage, Context context) {
        if (dotContainer == null || context == null || totalPages <= 1) {
            if (dotContainer != null) dotContainer.setVisibility(View.GONE);
            return;
        }

        dotContainer.removeAllViews();
        dotContainer.setVisibility(View.VISIBLE);

        int dotSize = (int) (context.getResources().getDisplayMetrics().density * 4);
        int maxVisible = 3;
        int start = Math.max(0, currentPage - 1);
        int end = Math.min(totalPages, currentPage + 2);

        for (int i = start; i < end; i++) {
            View dot = new View(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dotSize, dotSize);
            params.setMargins(4, 0, 4, 0);
            dot.setLayoutParams(params);

            if (i == currentPage) {
                dot.setBackgroundResource(R.drawable.dot_active);
            } else {
                dot.setBackgroundResource(R.drawable.dot_inactive);
            }

            dotContainer.addView(dot);
        }
    }

    /**
     * Switches a button between normal text and a loading spinner state.
     * Use this on submit buttons while an API call is in progress.
     *
     * @param button      the button whose enabled state and label should change
     * @param progressBar the spinner shown while loading is true
     * @param loading     true to disable the button and show the spinner
     * @param label       text to restore when loading finishes and the button is empty
     */
    public static void setButtonLoading(Button button, ProgressBar progressBar, boolean loading, String label) {
        button.setEnabled(!loading);
        if (loading) {
            button.setText("");
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            CharSequence currentText = button.getText();
            if (currentText == null || currentText.toString().trim().isEmpty()) {
                button.setText(label);
            }
        }
    }

    /**
     * Applies the correct follow / following look to a profile follow button.
     * Call this after you know whether the logged-in user already follows that person.
     *
     * @param button      the follow button to style
     * @param isFollowing true for the grey "Following" state, false for the purple "Follow" state
     */
    public static void applyFollowButtonState(Button button, boolean isFollowing) {
        Context context = button.getContext();
        button.setAllCaps(false);
        button.setStateListAnimator(null);
        button.setElevation(0f);
        button.setBackgroundTintList(null);

        if (isFollowing) {
            button.setText("Following");
            button.setBackgroundResource(R.drawable.bg_follow_following);
            button.setTextColor(ContextCompat.getColor(context, R.color.midnight_black));
        } else {
            button.setText("Follow");
            button.setBackgroundResource(R.drawable.follow_user_btn);
            button.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
    }
}
