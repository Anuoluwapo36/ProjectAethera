package com.example.projectaethera.config;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.aware.PublishConfig;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.projectaethera.BuildConfig;
import com.example.projectaethera.R;

/**
 * Central place for app-wide settings: server URLs, validation rules,
 * error messages, login lockout, and saved user session data.
 */
@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class AppConfig {

    // --- UI / image defaults ---

    /** Drawable used when a user has no profile picture. */
    public  static  final  int DEFAULT_PROFILE_IMAGE = R.drawable.user_solid_full;

    /** Intent extra key for passing a single image URI between screens. */
    public static final String IMAGE_URI_PATH = "Image_uri";
    /** Intent extra key for passing a list of image URIs. */
    public static final String IMAGE_LIST_PATH = "Image_list";
    /** Broadcast/intent key when the profile was updated and UI should refresh. */
    public static final String PROFILE_UPDATED_KEY = "PROFILE_UPDATED";
    /** Broadcast/intent key when a new post was created and profile grid should refresh. */
    public static final String POST_CREATED_KEY = "POST_CREATED";

    // --- Permissions ---

    /**
     * Permission needed to pick photos from the device.
     * Uses READ_MEDIA_IMAGES on Android 13+, otherwise READ_EXTERNAL_STORAGE.
     */
    public  static  final  String PHOTO_PERMISSION = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ? Manifest.permission.READ_MEDIA_IMAGES
            : Manifest.permission.READ_EXTERNAL_STORAGE;

    // --- Login attempt / lockout rules ---

    /** Default max wrong login tries before the account is temporarily locked. */
    private static final int MAX_LOGIN_ATTEMPT = 5;
    /** Current max attempts (can be changed at runtime within allowed range). */
    private static int MAX_LOGIN_ATTEMPT_UPDATE = MAX_LOGIN_ATTEMPT;

    /** Default lock duration in milliseconds (15 minutes). */
    private static  final long LOCK_TIME = 900000;
    /** Current lock duration (can be changed at runtime). */
    private static long LOCK_TIME_UPDATE = LOCK_TIME;

    // --- Network ---

    /** How long to wait for connect/read/write before the API call times out (seconds). */
    public static  final int API_TIME_OUT = 30;

    // --- Server URLs ---

    /** Backend host when running on a real phone (update if your PC IP changes). */
    private static final String REAL_DEVICE_HOST = BuildConfig.REAL_DEVICE_HOST;
    /** Backend host when running on the Android emulator (maps to localhost on PC). */
    private static final String EMULATOR_HOST = "http://10.0.2.2:8000";

    /**
     * Picks emulator or real-device host based on how the app is running.
     *
     * @return base server URL without the /api/ path
     */
    public static String getServerHost() {
        return isEmulator() ? EMULATOR_HOST : REAL_DEVICE_HOST;
    }

    /**
     * Full base URL for Retrofit API calls (includes /api/ suffix).
     *
     * @return e.g. http://10.0.2.2:8000/api/
     */
    public static String getBaseUrl() {
        return getServerHost() + "/api/";
    }

    /**
     * Base URL for loading images and media files from the server.
     *
     * @return e.g. http://10.0.2.2:8000/
     */
    public static String getMediaBaseUrl() {
        return getServerHost() + "/";
    }

    /**
     * Guesses if the app is on an emulator using common Build properties.
     *
     * @return true if this looks like an emulator
     */
    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.contains("emulator")
                || Build.FINGERPRINT.contains("sdk_gphone")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    // --- Validation ---

    /** Simple regex pattern used to check email format on login/register. */
    public static final String EMAIL_REGEX = "[a-zA-Z0-9._-]+@[a-z]+\\.[a-z]+";

    // --- Error messages shown to the user ---

    /** Shown when email does not match EMAIL_REGEX. */
    public static final String ERROR_INVALID_EMAIL = "Invalid email format";
    /** Shown when user tries to register without accepting terms. */
    public static final String ERROR_UNCHECK_TERMS_CONDITION = "agree to terms and condition";
    /** Shown when email field is left blank. */
    public static final String ERROR_EMPTY_EMAIL = "Email cannot be empty ";
    /** Shown when password format is wrong. */
    public static  final String ERROR_INVALID_PASSWORD = "Invalid password format";
    /** Shown when password field is left blank. */
    public static final String ERROR_EMPTY_PASSWORD = "password cannot be empty ";
    /** Shown when username is too short. */
    public static  final  String ERROR_USERNAME = "username cannot be short(min 12)";
    /** Shown when username field is left blank. */
    public static  final String ERROR_EMPTY_USERNAME = "username cannot be empty";

    /** Message when too many failed login attempts (not exposed as public constant). */
    private static  final  String ERROR_MAX_ATTEMPT = "you exceeded max attempt try again in 15 mins";
    /** Shown when password length requirement is not met. */
    public static final  String ERROR_PASSWORD_LENGTH = "Password does not meet length requirement";

    /**
     * Returns the current max login attempts before lockout.
     *
     * @return allowed failed attempts (default 5, can be updated)
     */
    public static int getMaxLoginAttemptUpdate() {
        return MAX_LOGIN_ATTEMPT_UPDATE;
    }

    /**
     * Updates max login attempts if the value is between 5 and 20.
     *
     * @param maxLoginAttemptUpdate new attempt limit
     */
    public static void setMaxLoginAttemptUpdate(int maxLoginAttemptUpdate) {

        if(maxLoginAttemptUpdate < 5 || maxLoginAttemptUpdate > 20){
            return;
        }
        MAX_LOGIN_ATTEMPT_UPDATE = maxLoginAttemptUpdate;
    }

    /**
     * Returns how long the user stays locked out after too many failed logins (ms).
     *
     * @return lock duration in milliseconds
     */
    public static long getLockTimeUpdate() {
        return LOCK_TIME_UPDATE;
    }

    /**
     * Sets a new lockout duration after failed login attempts.
     *
     * @param lockTimeUpdate lock time in milliseconds
     */
    public static void setLockTimeUpdate(long lockTimeUpdate) {
        LOCK_TIME_UPDATE = lockTimeUpdate;
    }

    // --- Session (SharedPreferences) ---

    /** Name of the SharedPreferences file for login session. */
    private static final String PREFS_NAME = "user_session";
    /** Key for storing the logged-in user's id. */
    private static final String KEY_USER_ID = "userId";
    /** Key for whether the user is currently logged in. */
    private static final String KEY_LOGGED_IN = "isLoggedIn";
    /** Key for storing the logged-in user's email. */
    private static final String KEY_EMAIL = "email";

    /**
     * Saves user id, email, and logged-in flag after a successful login.
     *
     * @param context app context for SharedPreferences
     * @param userId  the user's id from the server
     * @param email   the user's email (can be null)
     */
    public static void saveSession(Context context, int userId, String email) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putInt(KEY_USER_ID, userId)
                .putBoolean(KEY_LOGGED_IN, true)
                .putString(KEY_EMAIL, email != null ? email : "")
                .apply();
    }

    /**
     * Saves only the user id (email left empty). Used when email is not needed.
     *
     * @param context app context
     * @param userId  the user's id
     */
    public static void saveUserId(Context context, int userId) {
        saveSession(context, userId, null);
    }

    /**
     * Reads the saved user id from the session.
     *
     * @param context app context
     * @return user id, or 0 if not logged in
     */
    public static int getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_USER_ID, 0);
    }

    /**
     * Reads the email saved at login.
     *
     * @param context app context
     * @return email string, or empty if none saved
     */
    public static String getUserEmail(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(KEY_EMAIL, "");
    }

    /**
     * Checks if there is a valid logged-in session.
     *
     * @param context app context
     * @return true if userId is set and login flag allows access
     */
    public static boolean isLoggedIn(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int userId = preferences.getInt(KEY_USER_ID, 0);
        if (userId <= 0) {
            return false;
        }
        if (!preferences.contains(KEY_LOGGED_IN)) {
            return true;
        }
        return preferences.getBoolean(KEY_LOGGED_IN, false);
    }

    /**
     * Clears all session data on logout.
     *
     * @param context app context
     */
    public static void clearSession(Context context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
