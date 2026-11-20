package com.example.projectaethera.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.config.SettingsPrefs;
import com.example.projectaethera.ui.auth.AuthActivity;
import com.example.projectaethera.ui.home.HomeFragment;
import com.example.projectaethera.ui.post.AddPostFragment;
import com.example.projectaethera.ui.profile.ProfileFragment;
import com.example.projectaethera.ui.search.SearchFragment;
import com.example.projectaethera.utils.LocalNotificationHelper;
import com.example.projectaethera.utils.NavHelper;
import com.example.projectaethera.viewmodel.NotificationViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.lifecycle.ViewModelProvider;

/**
 * Main screen of the app after login. Hosts four bottom tabs (Home, Search, Add Post, Profile)
 * and a separate overlay container for full-screen flows like settings, post details, or
 * another user's profile.
 * <p>
 * Tab fragments live in {@code R.id.content} (or {@code R.id.camera_host} for the camera tab).
 * Overlay fragments are pushed onto a back stack in {@code R.id.overlay_content} via
 * {@link #openOverlay(Fragment)}. The bottom nav hides while an overlay is open or when
 * Search / Add Post tabs need full-screen space.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG_HOME = "tab_home";
    private static final String TAG_SEARCH = "tab_search";
    private static final String TAG_ADD_POST = "tab_add_post";
    private static final String TAG_PROFILE = "tab_profile";

    public BottomNavigationView navigationView;
    private FragmentContainerView overlayContainer;
    private FragmentContainerView cameraHost;
    private View contentContainer;

    private Fragment homeFragment;
    private Fragment searchFragment;
    private Fragment addPostFragment;
    private Fragment profileFragment;
    private Fragment activeTabFragment;
    private boolean suppressNavCallback = false;


    private final ActivityResultLauncher<String> notificationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {

            });

    private static final long NOTIFICATION_POLL_MS = 8_000L;
    private final Handler notificationPollHandler = new Handler(Looper.getMainLooper());
    private NotificationViewModel notificationViewModel;

    private final Runnable notificationPollRunnable = new Runnable() {
        @Override
        public void run() {
            int userId = AppConfig.getUserId(MainActivity.this);
            if (userId > 0 && notificationViewModel != null) {
                notificationViewModel.fetchUnreadCount(userId);
            }
            notificationPollHandler.postDelayed(this, NOTIFICATION_POLL_MS);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsPrefs.applyTheme(this);
        super.onCreate(savedInstanceState);

        if (!AppConfig.isLoggedIn(this)) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.main_activity);
        LocalNotificationHelper.ensureChannel(this);
        requestNotificationPermissionIfNeeded();
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        int loggedInUserId = AppConfig.getUserId(this);
        if (loggedInUserId > 0) {
            notificationViewModel.resetForUser(loggedInUserId);
        }
        observePushAlerts();
        observeUnreadBadge();

        navigationView = findViewById(R.id.bottom_nav);
        overlayContainer = findViewById(R.id.overlay_content);
        cameraHost = findViewById(R.id.camera_host);
        contentContainer = findViewById(R.id.content);

        if (savedInstanceState == null) {
            initTabs();
            navigationView.setSelectedItemId(R.id.nav_home);
        } else {
            restoreTabs();
        }

        navigationView.setOnItemSelectedListener(item -> {
            if (suppressNavCallback) {
                return true;
            }
            switchTab(item.getItemId());
            return true;
        });

        getSupportFragmentManager().addOnBackStackChangedListener(this::onOverlayChanged);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    return;
                }
                if (activeTabFragment instanceof AddPostFragment) {
                    selectTab(R.id.nav_home);
                    return;
                }
                if (activeTabFragment instanceof SearchFragment) {
                    selectTab(R.id.nav_home);
                    return;
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        onOverlayChanged();
        updateScreenModeForTab(activeTabFragment);
        refreshNotificationBadge();
    }

    /** Creates all four tab fragments once and shows Home as the starting tab. */
    private void initTabs() {
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        addPostFragment = new AddPostFragment();
        profileFragment = new ProfileFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, profileFragment, TAG_PROFILE).hide(profileFragment)
                .add(R.id.content, searchFragment, TAG_SEARCH).hide(searchFragment)
                .add(R.id.content, homeFragment, TAG_HOME)
                .add(R.id.camera_host, addPostFragment, TAG_ADD_POST)
                .commitNow();

        activeTabFragment = homeFragment;
        syncBottomNavForTab(activeTabFragment);
    }

    /** Re-attaches tab fragment references after a configuration change instead of re-creating them. */
    private void restoreTabs() {
        FragmentManager manager = getSupportFragmentManager();
        homeFragment = manager.findFragmentByTag(TAG_HOME);
        searchFragment = manager.findFragmentByTag(TAG_SEARCH);
        addPostFragment = manager.findFragmentByTag(TAG_ADD_POST);
        profileFragment = manager.findFragmentByTag(TAG_PROFILE);

        activeTabFragment = homeFragment;
        if (addPostFragment != null && addPostFragment.isVisible()) {
            activeTabFragment = addPostFragment;
        } else {
            for (Fragment fragment : manager.getFragments()) {
                if (fragment != null && fragment.isVisible() && fragment.getId() == R.id.content) {
                    activeTabFragment = fragment;
                    break;
                }
            }
        }
        syncBottomNavForTab(activeTabFragment);
    }

    /**
     * Swaps the visible tab fragment when the user taps bottom navigation.
     * Clears any open overlay first so tabs always start from a clean state.
     */
    private void switchTab(int itemId) {
        Fragment target = null;
        if (itemId == R.id.nav_home) {
            target = homeFragment;
        } else if (itemId == R.id.nav_search) {
            target = searchFragment;
        } else if (itemId == R.id.nav_add_post) {
            target = addPostFragment;
        } else if (itemId == R.id.nav_profile) {
            target = profileFragment;
        }

        if (target == null || target == activeTabFragment) {
            return;
        }

        clearOverlayStackQuietly();

        FragmentManager manager = getSupportFragmentManager();
        if (target instanceof AddPostFragment) {
            manager.beginTransaction()
                    .hide(activeTabFragment)
                    .show(addPostFragment)
                    .commit();
        } else if (activeTabFragment instanceof AddPostFragment) {
            manager.beginTransaction()
                    .hide(addPostFragment)
                    .show(target)
                    .commit();
        } else {
            manager.beginTransaction()
                    .hide(activeTabFragment)
                    .show(target)
                    .commit();
        }

        activeTabFragment = target;
        syncBottomNavForTab(activeTabFragment);
        updateScreenModeForTab(activeTabFragment);
        refreshNotificationBadge();
    }

    public void selectTab(int itemId) {
        switchTab(itemId);
    }

    private void syncBottomNavForTab(Fragment tab) {
        int navId = getNavIdForFragment(tab);
        if (navigationView.getSelectedItemId() != navId) {
            suppressNavCallback = true;
            navigationView.setSelectedItemId(navId);
            suppressNavCallback = false;
        }
    }

    /**
     * Shows or hides the bottom nav, main content area, and camera host depending on the active tab.
     * Search and Add Post use full-screen layouts without the bottom bar.
     */
    private void updateScreenModeForTab(Fragment tab) {
        boolean isCameraTab = tab instanceof AddPostFragment;
        boolean isSearchTab = tab instanceof SearchFragment;
        cameraHost.setVisibility(isCameraTab ? View.VISIBLE : View.GONE);
        contentContainer.setVisibility(isCameraTab ? View.GONE : View.VISIBLE);
        navigationView.setVisibility(isCameraTab || isSearchTab ? View.GONE : View.VISIBLE);
    }

    private int getNavIdForFragment(Fragment tab) {
        if (tab == searchFragment) {
            return R.id.nav_search;
        }
        if (tab == addPostFragment) {
            return R.id.nav_add_post;
        }
        if (tab == profileFragment) {
            return R.id.nav_profile;
        }
        return R.id.nav_home;
    }

    public void loadFragment(Fragment fragment) {
        openOverlay(fragment);
    }

    /**
     * Opens a full-screen overlay on top of the current tab (settings, profile, post detail, etc.).
     * The overlay sits in {@code overlay_content} and is added to the overlay back stack.
     */
    public void openOverlay(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.overlay_content, fragment)
                .addToBackStack(NavHelper.OVERLAY_STACK)
                .commit();

        onOverlayChanged();
    }

    public void clearOverlayStack() {
        clearOverlayStackQuietly();
        onOverlayChanged();
    }

    /**
     * Called after a successful post upload: closes overlays, switches to Home tab,
     * releases the camera, and refreshes the home feed.
     */
    public void returnHomeAfterPost() {
        if (isFinishing()) {
            return;
        }

        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack(NavHelper.OVERLAY_STACK, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (addPostFragment != null && addPostFragment instanceof AddPostFragment) {
            ((AddPostFragment) addPostFragment).releaseCamera();
        }

        if (homeFragment != null && activeTabFragment != homeFragment) {
            manager.beginTransaction()
                    .hide(activeTabFragment)
                    .show(homeFragment)
                    .commitNowAllowingStateLoss();
            activeTabFragment = homeFragment;
        }

        suppressNavCallback = true;
        navigationView.setSelectedItemId(R.id.nav_home);
        suppressNavCallback = false;

        updateScreenModeForTab(homeFragment);
        onOverlayChanged();

        if (homeFragment instanceof HomeFragment) {
            ((HomeFragment) homeFragment).refreshAfterNewPost();
        }

        getSupportFragmentManager().setFragmentResult(AppConfig.POST_CREATED_KEY, new Bundle());

        if (profileFragment instanceof ProfileFragment) {
            ((ProfileFragment) profileFragment).refreshPostsAfterUpload();
        }
    }

    private void clearOverlayStackQuietly() {
        getSupportFragmentManager().popBackStack(NavHelper.OVERLAY_STACK, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /** Shows or hides the overlay container and bottom nav whenever the overlay back stack changes. */
    public void onOverlayChanged() {
        boolean overlayOpen = getSupportFragmentManager().getBackStackEntryCount() > 0;
        overlayContainer.setVisibility(overlayOpen ? View.VISIBLE : View.GONE);

        if (overlayOpen) {
            navigationView.setVisibility(View.GONE);
            if (addPostFragment instanceof AddPostFragment) {
                ((AddPostFragment) addPostFragment).releaseCamera();
            }
        } else {
            updateScreenModeForTab(activeTabFragment);
        }
        if (!overlayOpen) {
            refreshNotificationBadge();
        }
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        int userId = AppConfig.getUserId(this);
        if (userId > 0 && notificationViewModel != null) {
            notificationViewModel.fetchUnreadCount(userId);
        }
        notificationPollHandler.postDelayed(notificationPollRunnable, NOTIFICATION_POLL_MS);
    }

    private void refreshNotificationBadge() {
        int userId = AppConfig.getUserId(this);
        if (userId > 0 && notificationViewModel != null) {
            notificationViewModel.fetchUnreadCount(userId);
        }
    }

    private void observeUnreadBadge() {
        notificationViewModel.getUnreadCount().observe(this, count -> {
            int safeCount = count != null ? count : 0;
            updateBottomNavBadge(safeCount);
            if (homeFragment instanceof HomeFragment && homeFragment.isAdded()) {
                ((HomeFragment) homeFragment).updateUnreadBadge(safeCount);
            }
        });
    }

    private void updateBottomNavBadge(int count) {
        if (navigationView == null) {
            return;
        }
        if (count <= 0) {
            navigationView.removeBadge(R.id.nav_home);
            return;
        }
        BadgeDrawable badge = navigationView.getOrCreateBadge(R.id.nav_home);
        badge.setVisible(true);
        badge.setBackgroundColor(getColor(R.color.red));
        badge.setBadgeTextColor(getColor(R.color.white));
        if (count > 9) {
            badge.clearNumber();
            badge.setText("9+");
        } else {
            badge.setNumber(count);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        notificationPollHandler.removeCallbacks(notificationPollRunnable);
    }

    private void observePushAlerts() {
        notificationViewModel.getPushAlert().observe(this, item -> {
            if (item == null) {
                return;
            }
            if (!SettingsPrefs.isNotifyPush(this)) {
                return;
            }
            String type = item.getType();
            if ("like".equals(type) && !SettingsPrefs.isNotifyLikes(this)) {
                return;
            }
            if ("comment".equals(type) && !SettingsPrefs.isNotifyComments(this)) {
                return;
            }
            if ("follow".equals(type) && !SettingsPrefs.isNotifyFollows(this)) {
                return;
            }
            LocalNotificationHelper.showForItem(this, item);
        });
    }
}
