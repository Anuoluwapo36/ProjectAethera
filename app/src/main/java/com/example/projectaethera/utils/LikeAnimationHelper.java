package com.example.projectaethera.utils;



import android.view.GestureDetector;

import android.view.MotionEvent;

import android.view.View;

import android.view.ViewGroup;

import android.view.animation.OvershootInterpolator;

import android.widget.FrameLayout;

import android.widget.ImageView;



import androidx.core.view.GestureDetectorCompat;



import com.example.projectaethera.R;



/**

 * Handles double-tap-to-like gestures and the visual feedback for likes.

 * Use this on post images or like buttons when you want Instagram-style

 * double-tap hearts and a small bounce on the like icon.

 */

public final class LikeAnimationHelper {



    /** Called when the user double-taps a view that supports like-on-double-tap. */

    public interface OnDoubleTapLikeListener {

        void onDoubleTapLike(float x, float y);

    }



    private LikeAnimationHelper() {

    }



    /**

     * Listens for double taps on a view without blocking normal single taps or scrolling.

     * Wire your like action inside the listener when a double tap is detected.

     *

     * @param target   the view that should receive double-tap gestures (often a post image)

     * @param listener called with tap coordinates when the user double-taps

     */

    public static void attachDoubleTapLike(View target, OnDoubleTapLikeListener listener) {

        GestureDetectorCompat detector = new GestureDetectorCompat(

                target.getContext(),

                new GestureDetector.SimpleOnGestureListener() {

                    @Override

                    public boolean onDown(MotionEvent e) {

                        return true;

                    }



                    @Override

                    public boolean onDoubleTap(MotionEvent e) {

                        if (listener != null) {

                            listener.onDoubleTapLike(e.getX(), e.getY());

                        }

                        return true;

                    }

                }

        );



        target.setOnTouchListener((v, event) -> {

            detector.onTouchEvent(event);

            return false;

        });

    }



    /**

     * Shows a brief heart animation at the tap location on top of a post.

     * Call this right after a successful double-tap like for visual feedback.

     *

     * @param container the parent view group that should host the floating heart

     * @param x         horizontal tap position relative to the target view

     * @param y         vertical tap position relative to the target view

     */

    public static void showHeartBurst(ViewGroup container, float x, float y) {

        if (container == null) {

            return;

        }



        float density = container.getResources().getDisplayMetrics().density;

        int size = (int) (96f * density);



        ImageView heart = new ImageView(container.getContext());

        heart.setImageResource(R.drawable.heart_solid_white);

        heart.setScaleType(ImageView.ScaleType.FIT_CENTER);

        heart.setElevation(8f * density);



        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);

        params.leftMargin = Math.max(0, (int) (x - size / 2f));

        params.topMargin = Math.max(0, (int) (y - size / 2f));

        heart.setLayoutParams(params);

        heart.setScaleX(0f);

        heart.setScaleY(0f);

        heart.setAlpha(0f);



        container.addView(heart);



        heart.animate()

                .scaleX(1f)

                .scaleY(1f)

                .alpha(1f)

                .setDuration(120L)

                .setInterpolator(new OvershootInterpolator(2.5f))

                .withEndAction(() -> heart.animate()

                        .scaleX(1.15f)

                        .scaleY(1.15f)

                        .alpha(0f)

                        .setStartDelay(180L)

                        .setDuration(420L)

                        .withEndAction(() -> container.removeView(heart))

                        .start())

                .start();

    }



    /**

     * Plays a quick scale bounce on the like button when the user taps it.

     * Use alongside {@link #applyLikedIcon(ImageView, boolean)} for a polished like interaction.

     *

     * @param likeButton the heart icon button to animate; ignored if null

     */

    public static void animateLikeButton(ImageView likeButton) {

        if (likeButton == null) {

            return;

        }



        likeButton.animate().cancel();

        likeButton.setScaleX(1f);

        likeButton.setScaleY(1f);



        likeButton.animate()

                .scaleX(1.28f)

                .scaleY(1.28f)

                .setDuration(90L)

                .withEndAction(() -> likeButton.animate()

                        .scaleX(1f)

                        .scaleY(1f)

                        .setDuration(120L)

                        .start())

                .start();

    }



    /**

     * Swaps the like button between filled and outline heart drawables.

     * Call whenever the liked state changes from the server or after a local toggle.

     *

     * @param likeButton the heart icon to update; ignored if null

     * @param liked      true for the solid heart, false for the outline heart

     */

    public static void applyLikedIcon(ImageView likeButton, boolean liked) {

        if (likeButton == null) {

            return;

        }

        likeButton.setImageResource(liked ? R.drawable.heart_solid_full : R.drawable.heart_regular_full);

    }

}

