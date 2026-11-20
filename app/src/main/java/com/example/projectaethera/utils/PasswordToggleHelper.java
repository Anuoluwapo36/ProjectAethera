package com.example.projectaethera.utils;



import android.text.method.HideReturnsTransformationMethod;

import android.text.method.PasswordTransformationMethod;

import android.widget.EditText;

import android.widget.ImageButton;



import com.example.projectaethera.R;



/**

 * Wires up the show/hide password eye button next to a password field.

 * Use this on login or sign-up screens so users can peek at what they typed

 * without writing the toggle logic in every fragment.

 */

public final class PasswordToggleHelper {



    private PasswordToggleHelper() {

    }



    /**

     * Connects a password EditText to its visibility toggle button.

     * Call once during view setup; the helper keeps the icon and masking in sync.

     *

     * @param passwordField the field whose characters should be shown or hidden

     * @param toggle        the eye button that switches between visible and masked text

     */

    public static void attach(EditText passwordField, ImageButton toggle) {

        toggle.setOnClickListener(v -> {

            int selection = passwordField.getSelectionEnd();

            boolean hidden = passwordField.getTransformationMethod() instanceof PasswordTransformationMethod;



            if (hidden) {

                passwordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());

                toggle.setImageResource(R.drawable.ic_visibility);

            } else {

                passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());

                toggle.setImageResource(R.drawable.ic_visibility_off);

            }



            if (selection >= 0) {

                passwordField.setSelection(Math.min(selection, passwordField.getText().length()));

            }

        });

    }

}

