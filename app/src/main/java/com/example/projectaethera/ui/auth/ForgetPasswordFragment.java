package com.example.projectaethera.ui.auth;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.projectaethera.R;
import com.example.projectaethera.ui.widgets.ForgetPasswordClickListener;
import com.example.projectaethera.utils.PasswordToggleHelper;
import com.example.projectaethera.utils.ViewUtils;
import com.example.projectaethera.viewmodel.CheckEmailViewModel;
import com.example.projectaethera.viewmodel.ResetPasswordViewModel;
import com.example.projectaethera.viewmodel.VerifyCodeViewModel;

/**
 * Multi-step forget-password screen inside {@link AuthActivity}'s overlay.
 * Walks the user through email check, verification code, and new password reset using
 * {@link CheckEmailViewModel}, {@link VerifyCodeViewModel}, and {@link ResetPasswordViewModel}.
 */
public class ForgetPasswordFragment extends Fragment {
    private TextView nodeCode, displayText_tv;
    private EditText confirmEmail, emailcode, newPassword, confirmPassword;
    private View emailLayout, emailCodeLayout, passwordLayout, confirmPasswordLayout;
    private TextView genericDefaultText;
    private Button confirmBtn;
    private CheckEmailViewModel checkEmailViewModel;
    private VerifyCodeViewModel verifyCodeViewModel;
    private ProgressBar progressBar;


    private int userId = -1;
    private ResetPasswordViewModel resetPasswordViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.forget_password_fragment, container, false);
    }

    /**
     * called after the view is created to initialize UI components,
     * View models , click listeners , and observers for the forget password flow.
     *
     * @param view               The inflated view of the fragment
     * @param savedInstanceState saved state for configuration chnages
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        checkEmailViewModel = new ViewModelProvider(this).get(CheckEmailViewModel.class);
        verifyCodeViewModel = new ViewModelProvider(this).get(VerifyCodeViewModel.class);
        resetPasswordViewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);


        confirmEmail = view.findViewById(R.id.checkEmail);
        emailcode = view.findViewById(R.id.emailCode_id);
        newPassword = view.findViewById(R.id.passwordChange_id);
        emailLayout = view.findViewById(R.id.inputEmail_text);
        emailCodeLayout = view.findViewById(R.id.inputEmail_code);
        passwordLayout = view.findViewById(R.id.InputChangePassword);
        confirmPasswordLayout = view.findViewById(R.id.InputConfirmPassword);
        confirmPassword = view.findViewById(R.id.confirmPassword);
        nodeCode = view.findViewById(R.id.noteCodeText);
        displayText_tv = view.findViewById(R.id.tv_forgetPassword);
        confirmBtn = view.findViewById(R.id.confirmId);
        genericDefaultText = view.findViewById(R.id.forget_subtitle);
        progressBar = view.findViewById(R.id.forget_progress);
        observeLoading();

        AuthOverlayHelper.bindDismiss(view, () -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).closeAuthOverlay();
            }
        });

        TextView backToLogin = view.findViewById(R.id.back_to_login);
        if (backToLogin != null) {
            backToLogin.setOnClickListener(v -> {
                if (getActivity() instanceof AuthActivity) {
                    ((AuthActivity) getActivity()).closeAuthOverlay();
                }
            });
        }
        PasswordToggleHelper.attach(
                newPassword,
                view.findViewById(R.id.password_toggle_new_password));

        PasswordToggleHelper.attach(
                confirmPassword,
                view.findViewById(R.id.password_toggle_confirm_password));

        ForgetPasswordClickListener listener = new ForgetPasswordClickListener(confirmEmail, emailcode, newPassword, emailLayout,
                emailCodeLayout, passwordLayout, confirmBtn, checkEmailViewModel, this, requireContext(), verifyCodeViewModel, nodeCode,
                confirmPasswordLayout, confirmPassword, displayText_tv, resetPasswordViewModel, userId,genericDefaultText, progressBar
        );
        confirmBtn.setOnClickListener(listener);

    }

    /** Shows loading state on the Continue button for each step of the reset flow. */
    private void observeLoading() {
        checkEmailViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            ViewUtils.setButtonLoading(confirmBtn, progressBar, isLoading, "Continue");
            confirmEmail.setEnabled(!isLoading);
        });

        verifyCodeViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            ViewUtils.setButtonLoading(confirmBtn, progressBar, isLoading, "verify Code");
            emailcode.setEnabled(!isLoading);
        });

        resetPasswordViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            ViewUtils.setButtonLoading(confirmBtn, progressBar, isLoading, "Reset Password");
            newPassword.setEnabled(!isLoading);
            confirmPassword.setEnabled(!isLoading);
        });
    }




}
