package com.example.projectaethera.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.example.projectaethera.viewmodel.RegisterViewModel;


/**
 * Sign-up form shown inside {@link AuthActivity}'s overlay.
 * Uses {@link RegisterViewModel} to create an account; requires accepting terms.
 * On success saves the session and opens {@link MainActivity}. Links back to {@link LoginFragment}.
 */
public class SignupFragment extends Fragment {
    private EditText username, email, password;
    private Button signUpBtn;
    private ProgressBar signupProgress;
    private TextView termsCondition, haveAccount;
    private CheckBox isChecked;

    private RegisterViewModel registerViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.register_fragment, container, false);
    }

    /**
     * Binds sign-up fields, terms checkbox, {@link RegisterViewModel}, and overlay dismiss behavior.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = view.findViewById(R.id.name_id_signUp);
        email = view.findViewById(R.id.email_id_signUp);
        password = view.findViewById(R.id.password_id_signUp);
        signUpBtn = view.findViewById(R.id.signUp_btn_id);
        signupProgress = view.findViewById(R.id.signup_progress);
        isChecked = view.findViewById(R.id.isChecked_btn_id);
        termsCondition = view.findViewById(R.id.textView_terms_text);
        haveAccount = view.findViewById(R.id.haveAccount_id);

        registerViewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        PasswordToggleHelper.attach(
                password,
                view.findViewById(R.id.password_toggle_signup));

        NestedScrollView scrollView = view.findViewById(R.id.auth_form_scroll);
        KeyboardScrollHelper.attach(scrollView, username, email, password);

        AuthOverlayHelper.bindDismiss(view, () -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).closeAuthOverlay();
            }
        });

        termAndCondition();
        createUser();
        redirectHaveAccount();
        observeLoading();
    }

    private void observeLoading() {
        registerViewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            boolean isLoading = Boolean.TRUE.equals(loading);
            ViewUtils.setButtonLoading(signUpBtn, signupProgress, isLoading, "Sign up");
            username.setEnabled(!isLoading);
            email.setEnabled(!isLoading);
            password.setEnabled(!isLoading);
            isChecked.setEnabled(!isLoading);
        });
    }

    public void createUser() {
        signUpBtn.setOnClickListener(v -> {
            String usernameParam = username.getText().toString().trim();
            String emailParam = email.getText().toString().trim();
            String passwordParma = password.getText().toString().trim();
            boolean termAccepted = isChecked.isChecked();
            registerViewModel.registerUser(usernameParam, emailParam, termAccepted, passwordParma);
        });

        registerViewModel.getRegistrationSuccess().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isSuccess) {
                if (isSuccess) {
                    Integer userId = registerViewModel.getRegisteredUserId().getValue();
                    String emailParam = email.getText().toString().trim();
                    if (userId != null && userId > 0) {
                        AppConfig.saveSession(requireContext(), userId, emailParam);
                    }

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        });

        registerViewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });
    }

    public void termAndCondition() {
        termsCondition.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Terms & Conditions")
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage("By using Aethera, you agree to the following terms:\n" +
                            "You must be at least 13 years old to create an account and use this platform.\n" +
                            "You are responsible for safeguarding your account credentials and all activity under your account.\n" +
                            "You will not post harmful, abusive, discriminatory, illegal, infringing, or inappropriate content on the platform.\n" +
                            "You respect the privacy and rights of other users and will not engage in harassment, bullying, or unauthorized data collection.\n" +
                            "We reserve the right to remove content, restrict accounts, or suspend access for violations of these terms.\n" +
                            "We may collect and use your personal information as outlined in our privacy policy to improve and secure our services.\n" +
                            "You grant Aethera a non-exclusive license to display, store, and distribute content you publicly share on the platform.\n" +
                            "These terms may be updated occasionally, and continued use of the app constitutes acceptance of the revised terms.\n")
                    .setPositiveButton("Accept", ((dialog, which) -> isChecked.setChecked(true)))
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    public void redirectHaveAccount() {
        haveAccount.setOnClickListener(v -> {
            if (getActivity() instanceof AuthActivity) {
                ((AuthActivity) getActivity()).switchAuthOverlay(new LoginFragment(), "login", false);
            }
        });
    }
}
