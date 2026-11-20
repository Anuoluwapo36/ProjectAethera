package com.example.projectaethera.ui.auth;

import android.view.View;

import com.example.projectaethera.R;
import com.example.projectaethera.ui.widgets.SwipeCloseHelper;

/**
 * Small helper for auth overlay sheets. Wires the dimmed scrim tap, swipe-down handle,
 * and sheet click behavior so login/signup/forget-password forms can be dismissed the same way.
 */
public final class AuthOverlayHelper {

    private AuthOverlayHelper() {
    }

    /** Attaches scrim tap and swipe-down dismiss to an auth overlay root view. */
    public static void bindDismiss(View root, Runnable onClose) {
        View scrim = root.findViewById(R.id.auth_scrim);
        if (scrim != null) {
            scrim.setOnClickListener(v -> onClose.run());
        }

        View handle = root.findViewById(R.id.auth_sheet_handle);
        if (handle != null) {
            new SwipeCloseHelper(handle, onClose::run);
        }

        View loginSheet = root.findViewById(R.id.login_sheet);
        View signupSheet = root.findViewById(R.id.signup_sheet);
        View forgetSheet = root.findViewById(R.id.forget_sheet);
        View sheet = loginSheet != null ? loginSheet : (signupSheet != null ? signupSheet : forgetSheet);
        if (sheet != null) {
            sheet.setClickable(true);
            sheet.setFocusable(true);
        }
    }
}
