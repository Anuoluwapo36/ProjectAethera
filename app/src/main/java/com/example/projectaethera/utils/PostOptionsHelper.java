package com.example.projectaethera.utils;



import androidx.appcompat.app.AlertDialog;

import androidx.fragment.app.Fragment;



import com.example.projectaethera.R;

import com.example.projectaethera.config.AppConfig;

import com.example.projectaethera.viewmodel.DeletePostViewModel;



/**

 * Shows post action menus that depend on who is logged in.

 * Use this from post screens when the owner should be able to delete their own post

 * without duplicating permission checks and dialog code in every fragment.

 */

public final class PostOptionsHelper {



    private PostOptionsHelper() {

    }



    /**

     * Opens a small options dialog with delete when the logged-in user owns the post.

     * Does nothing silently when someone else views the post.

     *

     * @param fragment     the screen showing the post options menu

     * @param postId       id of the post that might be deleted

     * @param ownerUserId  user id of the person who created the post

     * @param viewModel    handles the actual delete request after confirmation

     */

    public static void showDeleteOption(Fragment fragment, int postId, int ownerUserId, DeletePostViewModel viewModel) {

        int loggedInUserId = AppConfig.getUserId(fragment.requireContext());

        if (ownerUserId != loggedInUserId) {

            return;

        }



        new AlertDialog.Builder(fragment.requireContext())

                .setItems(new CharSequence[]{fragment.getString(R.string.delete_post)}, (dialog, which) ->

                        confirmDelete(fragment, postId, loggedInUserId, viewModel))

                .show();

    }



    // Second step: ask the user to confirm before calling the delete API.

    private static void confirmDelete(Fragment fragment, int postId, int userId, DeletePostViewModel viewModel) {

        new AlertDialog.Builder(fragment.requireContext())

                .setMessage(R.string.delete_post_confirm)

                .setNegativeButton(android.R.string.cancel, null)

                .setPositiveButton(R.string.delete, (dialog, which) -> viewModel.deletePost(userId, postId))

                .show();

    }

}

