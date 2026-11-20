package com.example.projectaethera.ui.auth;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.ui.MainActivity;
import com.example.projectaethera.R;
import com.example.projectaethera.utils.KeyboardScrollHelper;
import com.example.projectaethera.utils.PasswordToggleHelper;
import com.example.projectaethera.utils.ViewUtils;
import com.example.projectaethera.viewmodel.LoginViewModel;


/**
 * Login form shown inside {@link AuthActivity}'s sliding overlay.
 * Uses {@link LoginViewModel} to authenticate; on success saves the session and opens {@link MainActivity}.
 * Links navigate to {@link SignupFragment} and the forget-password flow.
 */
public class LoginFragment extends Fragment {

    private EditText emailLogin, passwordLogin;
    private Button loginBtn;
    private ProgressBar loginProgress;
    private LoginViewModel loginViewModel;
    private TextView newAccount, forgetPassword;
    private int userId = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login_fragment, container, false);
    }

    /**
     * Binds email/password fields, {@link LoginViewModel}, dismiss gestures, and navigation links.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emailLogin = view.findViewById(R.id.email_login_id);
        passwordLogin = view.findViewById(R.id.password_login_id);
        loginBtn = view.findViewById(R.id.login_btn_id);
        loginProgress = view.findViewById(R.id.login_progress);
        newAccount = view.findViewById(R.id.newAccount);
        forgetPassword = view.findViewById(R.id.forget_Password_id);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        PasswordToggleHelper.attach(
                passwordLogin,
                view.findViewById(R.id.password_toggle_login));

        NestedScrollView scrollView = view.findViewById(R.id.auth_form_scroll);
        KeyboardScrollHelper.attach(scrollView, emailLogin, passwordLogin);

        AuthOverlayHelper.bindDismiss(view, () -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).closeAuthOverlay();
            }
        });

        redirectNewAccount();
        redirectForgetPassword();
        validateLogin();
        observeLoading();
    }

    /** Disables inputs and shows a spinner on the login button while the API call runs. */
    private void observeLoading() {
        loginViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            ViewUtils.setButtonLoading(loginBtn, loginProgress, isLoading, "login");
            emailLogin.setEnabled(!isLoading);
            passwordLogin.setEnabled(!isLoading);
        });
    }

    /** Validates credentials via {@link LoginViewModel} and navigates to main on success. */
    public void validateLogin() {
        loginBtn.setOnClickListener(v -> {
            String emailParam = emailLogin.getText().toString().trim();
            String passwordParam = passwordLogin.getText().toString().trim();
            loginViewModel.authenticateUser(emailParam, passwordParam);
        });

        loginViewModel.getAuthSuccess().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                if (isSuccess) {
                    userId = loginViewModel.getUserId().getValue() != null
                            ? loginViewModel.getUserId().getValue()
                            : 0;
                    String emailParam = emailLogin.getText().toString().trim();

                    AppConfig.saveSession(requireContext(), userId, emailParam);

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });

        loginViewModel.getAuthErrorMessage().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    public void redirectNewAccount() {
        newAccount.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).switchAuthOverlay(new SignupFragment(), "signup", true);
            }
        });
    }

    public void redirectForgetPassword() {
        forgetPassword.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).displayForgetPassword();
            }
        });
    }
}
