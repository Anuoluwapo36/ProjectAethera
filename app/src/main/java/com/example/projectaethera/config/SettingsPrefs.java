package com.example.projectaethera.config;



import android.content.Context;

import android.content.SharedPreferences;



import androidx.appcompat.app.AppCompatDelegate;



/**

 * Saves and reads user settings from SharedPreferences:

 * dark mode and notification toggles (likes, comments, follows, push).

 */

public final class SettingsPrefs {



    /** SharedPreferences file name for app settings. */

    private static final String PREFS = "app_settings";

    /** Key for dark mode on/off. */

    private static final String KEY_DARK_MODE = "dark_mode";

    /** Key for notifying when someone likes your post. */

    private static final String KEY_NOTIFY_LIKES = "notify_likes";

    /** Key for notifying when someone comments. */

    private static final String KEY_NOTIFY_COMMENTS = "notify_comments";

    /** Key for notifying when someone follows you. */

    private static final String KEY_NOTIFY_FOLLOWS = "notify_follows";

    /** Key for general push notifications. */

    private static final String KEY_NOTIFY_PUSH = "notify_push";



    /** Private constructor — use static methods only. */

    private SettingsPrefs() {

    }



    /**

     * Gets the SharedPreferences instance for settings.

     *

     * @param context app context

     * @return the settings preferences object

     */

    private static SharedPreferences prefs(Context context) {

        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);

    }



    /**

     * Applies saved dark mode preference to the whole app theme.

     *

     * @param context app context

     */

    public static void applyTheme(Context context) {

        AppCompatDelegate.setDefaultNightMode(

                isDarkMode(context)

                        ? AppCompatDelegate.MODE_NIGHT_YES

                        : AppCompatDelegate.MODE_NIGHT_NO

        );

    }



    /**

     * Checks if dark mode is enabled.

     *

     * @param context app context

     * @return true if dark mode is on

     */

    public static boolean isDarkMode(Context context) {

        return prefs(context).getBoolean(KEY_DARK_MODE, false);

    }



    /**

     * Turns dark mode on or off and saves the choice.

     *

     * @param context app context

     * @param enabled true for dark mode, false for light

     */

    public static void setDarkMode(Context context, boolean enabled) {

        prefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply();

    }



    /**

     * Checks if like notifications are enabled.

     *

     * @param context app context

     * @return true if like notifications are on (default true)

     */

    public static boolean isNotifyLikes(Context context) {

        return prefs(context).getBoolean(KEY_NOTIFY_LIKES, true);

    }



    /**

     * Enables or disables like notifications.

     *

     * @param context app context

     * @param enabled true to receive like notifications

     */

    public static void setNotifyLikes(Context context, boolean enabled) {

        prefs(context).edit().putBoolean(KEY_NOTIFY_LIKES, enabled).apply();

    }



    /**

     * Checks if comment notifications are enabled.

     *

     * @param context app context

     * @return true if comment notifications are on (default true)

     */

    public static boolean isNotifyComments(Context context) {

        return prefs(context).getBoolean(KEY_NOTIFY_COMMENTS, true);

    }



    /**

     * Enables or disables comment notifications.

     *

     * @param context app context

     * @param enabled true to receive comment notifications

     */

    public static void setNotifyComments(Context context, boolean enabled) {

        prefs(context).edit().putBoolean(KEY_NOTIFY_COMMENTS, enabled).apply();

    }



    /**

     * Checks if follow notifications are enabled.

     *

     * @param context app context

     * @return true if follow notifications are on (default true)

     */

    public static boolean isNotifyFollows(Context context) {

        return prefs(context).getBoolean(KEY_NOTIFY_FOLLOWS, true);

    }



    /**

     * Enables or disables follow notifications.

     *

     * @param context app context

     * @param enabled true to receive follow notifications

     */

    public static void setNotifyFollows(Context context, boolean enabled) {

        prefs(context).edit().putBoolean(KEY_NOTIFY_FOLLOWS, enabled).apply();

    }



    /**

     * Checks if push notifications are enabled.

     *

     * @param context app context

     * @return true if push notifications are on (default true)

     */

    public static boolean isNotifyPush(Context context) {

        return prefs(context).getBoolean(KEY_NOTIFY_PUSH, true);

    }



    /**

     * Enables or disables push notifications.

     *

     * @param context app context

     * @param enabled true to allow push notifications

     */

    public static void setNotifyPush(Context context, boolean enabled) {

        prefs(context).edit().putBoolean(KEY_NOTIFY_PUSH, enabled).apply();

    }

}

