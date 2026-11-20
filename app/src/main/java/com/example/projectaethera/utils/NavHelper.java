package com.example.projectaethera.utils;



import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;



import com.example.projectaethera.R;

import com.example.projectaethera.ui.MainActivity;



/**

 * Helper for opening and closing overlay screens on top of the main app content.

 * Use this when a fragment needs to show another fragment in the overlay area

 * (for example post preview or create-post flows) instead of replacing the whole screen.

 */

public final class NavHelper {



    /** Back-stack tag used for all overlay fragment transactions. */

    public static final String OVERLAY_STACK = "overlay_stack";



    private NavHelper() {

    }



    /**

     * Opens a new fragment in the overlay container and adds it to the back stack.

     * Call this when you want to push a screen on top of the current one.

     *

     * @param from    the fragment that is starting the overlay navigation

     * @param overlay the fragment to display inside the overlay area

     */

    public static void openOverlay(Fragment from, Fragment overlay) {

        FragmentManager manager = from.requireActivity().getSupportFragmentManager();

        manager.beginTransaction()

                .replace(R.id.overlay_content, overlay)

                .addToBackStack(OVERLAY_STACK)

                .commit();



        if (from.requireActivity() instanceof MainActivity) {

            ((MainActivity) from.requireActivity()).onOverlayChanged();

        }

    }



    /**

     * Closes the top overlay fragment by popping one entry off the back stack.

     * Use this for a normal back action when the user leaves one overlay screen.

     *

     * @param from the fragment that is requesting the back navigation

     */

    public static void popOverlay(Fragment from) {

        FragmentManager manager = from.requireActivity().getSupportFragmentManager();

        if (manager.getBackStackEntryCount() > 0) {

            manager.popBackStack();

        }

        if (from.requireActivity() instanceof MainActivity) {

            ((MainActivity) from.requireActivity()).onOverlayChanged();

        }

    }



    /**

     * Removes every overlay fragment from the back stack in one step.

     * Use this when you need to fully dismiss the overlay flow, not just go back one screen.

     *

     * @param from the fragment that is requesting the stack to be cleared

     */

    public static void clearOverlayStack(Fragment from) {

        FragmentManager manager = from.requireActivity().getSupportFragmentManager();

        manager.popBackStack(OVERLAY_STACK, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (from.requireActivity() instanceof MainActivity) {

            ((MainActivity) from.requireActivity()).onOverlayChanged();

        }

    }

}

