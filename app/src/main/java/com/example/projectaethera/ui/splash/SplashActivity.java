package com.example.projectaethera.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.projectaethera.R;
import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.config.SettingsPrefs;
import com.example.projectaethera.ui.MainActivity;
import com.example.projectaethera.ui.auth.AuthActivity;

/**
 * First screen when the app opens. Shows the Aethera logo and a loading bar,
 * then routes to {@link MainActivity} if the user is already logged in,
 * or {@link AuthActivity} for first-time / logged-out users.
 */
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DURATION_MS = 2200L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean navigated;

    private final Runnable goNext = new Runnable() {
        @Override
        public void run() {
            if (navigated || isFinishing()) {
                return;
            }
            navigated = true;
            Intent intent = AppConfig.isLoggedIn(SplashActivity.this)
                    ? new Intent(SplashActivity.this, MainActivity.class)
                    : new Intent(SplashActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        SettingsPrefs.applyTheme(this);
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_splash);
        LottieAnimationView splashLottie = findViewById(R.id.splash_lottie);
        splashLottie.setScaleType(ImageView.ScaleType.FIT_CENTER);
        splashLottie.setClipToCompositionBounds(false);
        handler.postDelayed(goNext, SPLASH_DURATION_MS);
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(goNext);
        super.onDestroy();
    }
}
