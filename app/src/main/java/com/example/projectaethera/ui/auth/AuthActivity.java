package com.example.projectaethera.ui.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.config.SettingsPrefs;
import com.example.projectaethera.ui.MainActivity;

import java.util.Random;

/**
 * Login and sign-up entry screen shown when the user is not logged in.
 * Displays animated Lottie cards; tapping Login or Sign Up slides up an overlay with
 * {@link LoginFragment} or {@link SignupFragment}. Redirects to {@link MainActivity} if a session exists.
 */
public class AuthActivity extends AppCompatActivity {

    private static final int[] LOGIN_LOTTIE_ANIMATIONS = {
            R.raw.login_animation,
            R.raw.login_animation_2,
            R.raw.login_animation_3
    };

    private static final int[] REGISTER_LOTTIE_ANIMATIONS = {
            R.raw.register_animation,
            R.raw.register_animation_2,
            R.raw.register_animation_3
    };

    public Animation slideUp, slideDown, slideUpSmall, slideDownSmall;

    private View imageLogin, imageRegister;
    public View imageLayout;
    private FrameLayout mainBody;
    private LottieAnimationView lottieLogin, lottieRegister;

    private final Random random = new Random();
    private int lastLoginAnimation = -1;
    private int lastRegisterAnimation = -1;
    private boolean lottieCyclingEnabled = true;
    private boolean advancingLoginLottie;
    private boolean advancingRegisterLottie;
    private TextView auth_moto, moto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SettingsPrefs.applyTheme(this);
        super.onCreate(savedInstanceState);

        if (AppConfig.isLoggedIn(this)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.auth_activity);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        applyDefaultStatusBar();

        initData();
        loadAnimation();
        setupCyclingLottie(lottieLogin, true);
        setupCyclingLottie(lottieRegister, false);
        imageClickAction();
        bodyClickClose();
    }

    private void initData() {
        mainBody = findViewById(R.id.mainbody);
        imageLayout = findViewById(R.id.image_layout);
        imageLogin = findViewById(R.id.img_login);
        imageRegister = findViewById(R.id.img_register);
        lottieLogin = findViewById(R.id.lottie_login);
        lottieRegister = findViewById(R.id.lottie_register);
        configureLottieView(lottieLogin);
        configureLottieView(lottieRegister);

        auth_moto = findViewById(R.id.motor_auth);
        moto = findViewById(R.id.motor);
    }

    private void configureLottieView(LottieAnimationView view) {
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        view.setClipToCompositionBounds(false);
    }

    private void loadAnimation() {
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
        slideUpSmall = AnimationUtils.loadAnimation(this, R.anim.slide_up_small);
        slideDownSmall = AnimationUtils.loadAnimation(this, R.anim.slide_down_back);
    }

    private void setupCyclingLottie(LottieAnimationView view, boolean isLoginCard) {
        view.setRepeatCount(0);
        view.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!lottieCyclingEnabled || isAdvancing(isLoginCard)) {
                    return;
                }
                view.post(() -> playNextLottie(view, isLoginCard));
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
        playNextLottie(view, isLoginCard);
    }

    private boolean isAdvancing(boolean isLoginCard) {
        return isLoginCard ? advancingLoginLottie : advancingRegisterLottie;
    }

    private void setAdvancing(boolean isLoginCard, boolean advancing) {
        if (isLoginCard) {
            advancingLoginLottie = advancing;
        } else {
            advancingRegisterLottie = advancing;
        }
    }

    private void playNextLottie(LottieAnimationView view, boolean isLoginCard) {
        if (!lottieCyclingEnabled || isAdvancing(isLoginCard)) {
            return;
        }

        setAdvancing(isLoginCard, true);

        int lastUsed = isLoginCard ? lastLoginAnimation : lastRegisterAnimation;
        int next = pickNextAnimation(lastUsed, isLoginCard);

        if (isLoginCard) {
            lastLoginAnimation = next;
        } else {
            lastRegisterAnimation = next;
        }

        view.setAnimation(next);
        view.setRepeatCount(0);
        view.playAnimation();

        setAdvancing(isLoginCard, false);
    }

    private int pickNextAnimation(int lastUsed, boolean isLoginCard) {
        int[] pool = isLoginCard ? LOGIN_LOTTIE_ANIMATIONS : REGISTER_LOTTIE_ANIMATIONS;
        if (pool.length == 1) {
            return pool[0];
        }

        int next;
        do {
            next = pool[random.nextInt(pool.length)];
        } while (next == lastUsed);
        return next;
    }

    private void pauseLottieAnimations() {
        lottieCyclingEnabled = false;
        if (lottieLogin.isAnimating()) {
            lottieLogin.pauseAnimation();
        }
        if (lottieRegister.isAnimating()) {
            lottieRegister.pauseAnimation();
        }
    }

    private void resumeLottieAnimations() {
        lottieCyclingEnabled = true;
        if (lottieLogin.isAnimating()) {
            lottieLogin.resumeAnimation();
        } else {
            playNextLottie(lottieLogin, true);
        }
        if (lottieRegister.isAnimating()) {
            lottieRegister.resumeAnimation();
        } else {
            playNextLottie(lottieRegister, false);
        }
    }

    private void imageClickAction() {
        imageLogin.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.auth_card_tap));
            openAuthOverlay(new LoginFragment(), "login");
        });

        imageRegister.setOnClickListener(v -> {
            v.startAnimation(AnimationUtils.loadAnimation(this, R.anim.auth_card_tap));
            openAuthOverlay(new SignupFragment(), "signup");
        });
    }

    private void setMottoVisible(boolean visible) {
        if (moto != null) {
            moto.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
        if (auth_moto != null) {
            auth_moto.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Slides the auth form overlay up over the welcome cards and loads the given fragment
     * (login, signup, or forget password).
     */
    private void openAuthOverlay(androidx.fragment.app.Fragment fragment, String tag) {
        pauseLottieAnimations();
        applyOverlayStatusBar();

        View fragmentContainer = findViewById(R.id.fragment_container);
        fragmentContainer.clearAnimation();
        fragmentContainer.setVisibility(View.GONE);

        setMottoVisible(false);

        if (slideUpSmall != null) {
            imageLayout.startAnimation(slideUpSmall);
        }

        imageLayout.postDelayed(() -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment, tag)
                    .addToBackStack(tag)
                    .commit();

            fragmentContainer.setVisibility(View.VISIBLE);

            if (slideUp != null) {
                fragmentContainer.startAnimation(slideUp);
            }
        }, 250);
    }

    private void bodyClickClose() {
        mainBody.setOnClickListener(v -> {
            View fragmentContainer = findViewById(R.id.fragment_container);
            if (fragmentContainer.getVisibility() != View.VISIBLE) {
                return;
            }
            closeAuthOverlay();
        });
    }

    /** Animates the overlay closed, pops the fragment back stack, and resumes Lottie on the welcome screen. */
    public void closeAuthOverlay() {
        View fragmentContainer = findViewById(R.id.fragment_container);
        if (fragmentContainer.getVisibility() != View.VISIBLE) {
            return;
        }

        fragmentContainer.startAnimation(slideDown);
        fragmentContainer.postDelayed(() -> {
            fragmentContainer.setVisibility(View.GONE);

            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            }

            if (slideDownSmall != null) {
                slideDownSmall.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        slideDownSmall.setAnimationListener(null);
                        setMottoVisible(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                imageLayout.startAnimation(slideDownSmall);
            } else {
                setMottoVisible(true);
            }

            resumeLottieAnimations();
            applyDefaultStatusBar();
        }, 300);
    }

    private void applyOverlayStatusBar() {
        getWindow().setStatusBarColor(getColor(R.color.midnight_black));
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        );
        if (controller != null) {
            controller.setAppearanceLightStatusBars(false);
        }
    }

    private void applyDefaultStatusBar() {
        getWindow().setStatusBarColor(getColor(R.color.white));
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(
                getWindow(),
                getWindow().getDecorView()
        );
        if (controller != null) {
            controller.setAppearanceLightStatusBars(true);
        }
    }

    /**
     * Replaces the current auth overlay fragment with another (e.g. login to signup)
     * using a horizontal slide animation. {@code forward} controls slide direction.
     */
    public void switchAuthOverlay(androidx.fragment.app.Fragment fragment, String tag, boolean forward) {
        int enter = forward
                ? R.anim.auth_switch_slide_in_right
                : R.anim.auth_switch_slide_in_left;
        int exit = forward
                ? R.anim.auth_switch_slide_out_left
                : R.anim.auth_switch_slide_out_right;

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(enter, exit)
                .replace(R.id.fragment_container, fragment, tag)
                .commit();
    }

    /** Opens the forget-password flow overlay from the login screen. */
    public void displayForgetPassword() {
        pauseLottieAnimations();
        applyOverlayStatusBar();

        View fragmentContainer = findViewById(R.id.fragment_container);
        fragmentContainer.clearAnimation();
        fragmentContainer.setVisibility(View.GONE);

        setMottoVisible(false);
        if (slideUpSmall != null) {
            imageLayout.startAnimation(slideUpSmall);
        }

        imageLayout.postDelayed(() -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new ForgetPasswordFragment(), "forgetPassword")
                    .addToBackStack("forgetPassword")
                    .commit();

            fragmentContainer.setVisibility(View.VISIBLE);

            if (slideUp != null) {
                fragmentContainer.startAnimation(slideUp);
            }
        }, 250);
    }

    /** Restores status bar and Lottie state depending on whether an auth overlay is visible. */
    @Override
    protected void onResume() {
        super.onResume();
        View fragmentContainer = findViewById(R.id.fragment_container);
        if (fragmentContainer.getVisibility() == View.VISIBLE) {
            applyOverlayStatusBar();
            setMottoVisible(false);
        } else {
            setMottoVisible(true);
            resumeLottieAnimations();
        }
    }

    @Override
    protected void onPause() {
        pauseLottieAnimations();
        super.onPause();
    }
}
