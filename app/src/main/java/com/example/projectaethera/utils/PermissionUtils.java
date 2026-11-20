package com.example.projectaethera.utils;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

/**
 * Simple permission checks used before camera, storage, or location features run.
 * Use this to see whether a dangerous permission is already granted before
 * showing a runtime permission request dialog.
 */
public class PermissionUtils {

    /** Callback for screens that need to react after a permission result. */
    public interface PermissionCallBack{
        void onGranted();
        void  onDenied();
    }

    /**
     * Returns whether the app already has a specific Android permission.
     * Call this before {@code requestPermissions} to skip the dialog when access exists.
     *
     * @param context    any valid context from the app
     * @param permission the manifest permission string to check (for example camera or storage)
     * @return true if the permission is granted, false otherwise
     */
    public static  boolean hasPermission(Context context, String permission){
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
