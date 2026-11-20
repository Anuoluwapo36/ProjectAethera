package com.example.projectaethera.utils;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.ui.post.CreatePostFragment;
import com.example.projectaethera.ui.post.PreviewPostFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Navigation shortcuts for previewing and creating posts from picked images.
 * Use these helpers after the user selects one or more photos so the right overlay
 * screen opens with the image data already packed into fragment arguments.
 */
public class PreviewViewUtils {

    // Wraps a single image URI in the bundle key the preview/create fragments expect.
    private static Bundle createSingleBundle(Uri imageUri) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppConfig.IMAGE_URI_PATH, imageUri);
        return bundle;
    }

    // Wraps a list of image URIs for multi-image post flows.
    private static Bundle createMultipleBundle(List<Uri> imageList) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(AppConfig.IMAGE_LIST_PATH, new ArrayList<>(imageList));
        return bundle;
    }

    /**
     * Opens the post preview overlay for one selected image.
     * Use after the user picks a single photo and wants to review it before posting.
     *
     * @param currentFragment the fragment that is launching the preview
     * @param imageUri        the chosen image to preview
     */
    public static void previewReview(Fragment currentFragment, Uri imageUri) {
        PreviewPostFragment previewFragment = new PreviewPostFragment();
        previewFragment.setArguments(createSingleBundle(imageUri));
        NavHelper.openOverlay(currentFragment, previewFragment);
    }

    /**
     * Opens the post preview overlay for several selected images.
     * Use when the gallery flow allows multi-select before review.
     *
     * @param currentFragment the fragment that is launching the preview
     * @param imageList       all selected image URIs, in display order
     */
    public static void previewReviewMultiple(Fragment currentFragment, List<Uri> imageList) {
        PreviewPostFragment previewPostFragment = new PreviewPostFragment();
        previewPostFragment.setArguments(createMultipleBundle(imageList));
        NavHelper.openOverlay(currentFragment, previewPostFragment);
    }

    /**
     * Jumps straight to the create-post screen with one image already attached.
     * Use when the user skips preview and goes directly to composing a post.
     *
     * @param currentFragment the fragment that is launching create post
     * @param imageUri        the image to include in the new post
     */
    public static void goToCreatePostView(Fragment currentFragment, Uri imageUri) {
        CreatePostFragment createPostFragment = new CreatePostFragment();
        createPostFragment.setArguments(createSingleBundle(imageUri));
        NavHelper.openOverlay(currentFragment, createPostFragment);
    }

    /**
     * Opens the status preview overlay for one image (stories/status flow).
     * Use when the user is creating a status rather than a regular feed post.
     *
     * @param currentFragment the fragment that is launching the status preview
     * @param imageUri        the image that will become the status background
     */
    public static void goToCreateStatusView(Fragment currentFragment, Uri imageUri) {
        com.example.projectaethera.ui.status.StatusPreviewFragment previewFragment =
                new com.example.projectaethera.ui.status.StatusPreviewFragment();
        previewFragment.setArguments(createSingleBundle(imageUri));
        NavHelper.openOverlay(currentFragment, previewFragment);
    }

    /**
     * Opens the create-post screen with multiple images ready to upload.
     * Use for carousel-style posts built from a multi-image gallery selection.
     *
     * @param currentFragment the fragment that is launching create post
     * @param imageLists      all images to attach to the new post
     */
    public static void goToCreatePostForMultipleImage(Fragment currentFragment, List<Uri> imageLists) {
        CreatePostFragment createPostFragment = new CreatePostFragment();
        createPostFragment.setArguments(createMultipleBundle(imageLists));
        NavHelper.openOverlay(currentFragment, createPostFragment);
    }
}
