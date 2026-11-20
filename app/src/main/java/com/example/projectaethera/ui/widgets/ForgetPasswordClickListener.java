package com.example.projectaethera.ui.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.ui.MainActivity;
import com.example.projectaethera.utils.ViewUtils;
import com.example.projectaethera.viewmodel.CheckEmailViewModel;
import com.example.projectaethera.viewmodel.ResetPasswordViewModel;
import com.example.projectaethera.viewmodel.VerifyCodeViewModel;

/**
 * Click listener that runs the forgot-password flow step by step.
 * It checks the email, verifies a code, collects a new password, and logs the user in on success.
 */
public class ForgetPasswordClickListener implements View.OnClickListener {

    private EditText emailText, emailCode, passwordText, passwordConfirm;
    private View emailInputLay, emailCodeInoutLay, passwordInoutLay, confirmPasswordInputLayout;
    private TextView noteCode, displayText , genericDefaultText;

    private Button button;
    private int userId;
    private String savedNewPassword;

    private final CheckEmailViewModel checkEmailViewModel;
    private final LifecycleOwner lifecycleOwner;

    private final Context context;
    private final VerifyCodeViewModel verifyCodeViewModel;
    private final ResetPasswordViewModel resetPasswordViewModel;
    private ProgressBar progressBar;


    private int currentState = 0;


    /**
     * Sets up all views and ViewModels needed for the password reset wizard.
     * Also watches each ViewModel for error messages and shows them as toasts.
     *
     * @param emailText                  email input field
     * @param emailCode                  verification code input field
     * @param passwordText               new password input field
     * @param emailInputLay              layout for the email step
     * @param emailCodeInoutLay          layout for the code step
     * @param passwordInoutLay           layout for the new password step
     * @param button                     main action button (Next, Verify, Reset, etc.)
     * @param checkEmailViewModel        checks whether the email exists
     * @param lifecycleOwner             used to observe LiveData from ViewModels
     * @param context                    app context for toasts and navigation
     * @param verifyCodeViewModel        verifies the email code
     * @param noteCode                   helper or error text under the inputs
     * @param confirmPasswordInputLayout layout for confirm-password step
     * @param passwordConfirm            confirm password field
     * @param displayText                title text for the current step
     * @param resetPasswordViewModel     sends the new password to the server
     * @param userId                     user id returned after code verification
     * @param genericDefaultText         longer instructions shown on each step
     * @param progressBar                loading indicator for network calls
     */
    public ForgetPasswordClickListener(EditText emailText, EditText emailCode, EditText passwordText,
                                       View emailInputLay, View emailCodeInoutLay,
                                       View passwordInoutLay, Button button,
                                       CheckEmailViewModel checkEmailViewModel, LifecycleOwner lifecycleOwner,
                                       Context context, VerifyCodeViewModel verifyCodeViewModel, TextView noteCode,
                                       View confirmPasswordInputLayout, EditText passwordConfirm, TextView displayText,
                                       ResetPasswordViewModel resetPasswordViewModel, int userId,TextView genericDefaultText, ProgressBar progressBar) {
        this.emailText = emailText;
        this.emailCode = emailCode;
        this.passwordText = passwordText;
        this.emailInputLay = emailInputLay;
        this.emailCodeInoutLay = emailCodeInoutLay;
        this.passwordInoutLay = passwordInoutLay;
        this.button = button;
        this.checkEmailViewModel = checkEmailViewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
        this.verifyCodeViewModel = verifyCodeViewModel;
        this.noteCode = noteCode;
        this.confirmPasswordInputLayout = confirmPasswordInputLayout;
        this.passwordConfirm = passwordConfirm;
        this.displayText = displayText;
        this.resetPasswordViewModel = resetPasswordViewModel;
        this.userId = userId;
        this.genericDefaultText = genericDefaultText;
        this.progressBar = progressBar;

        //observer errors
        checkEmailViewModel.getAuthCheckEmailErrorMessage().observe(lifecycleOwner, message -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        });

        verifyCodeViewModel.getAuthCodeMessageError().observe(lifecycleOwner, message -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        });

        resetPasswordViewModel.getAuthRestPassWordErrorMessage().observe(lifecycleOwner, message -> {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

        });


    }


    /**
     * Handles the main button click for the current step of the reset flow.
     * State 0 checks email, 1 verifies code, 2 asks for password, 3 confirms and resets.
     *
     * @param v the view that was clicked (the action button)
     */
    @Override
    public void onClick(View v) {
        switch (currentState) {
            case 0:
                //step 1: enter email and receive code to verify the user to change password for
                String email = emailText.getText().toString().trim();

                checkEmailViewModel.confirmUserExistEmail(email);
                checkEmailViewModel.getAuthCheckEmailSuccess().observe(lifecycleOwner, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isSuccess) {
                        if (isSuccess) {
                            currentState = 1;
                            emailInputLay.setVisibility(View.GONE);
                            emailCodeInoutLay.setVisibility(View.VISIBLE);
                            noteCode.setVisibility(View.VISIBLE);
                            displayText.setText("Code Sent to email");
                            genericDefaultText.setText("please enter the verification code sent to your email, to complete the process, if this is not you please contact our support.");
                            genericDefaultText.setTextSize(10);
                            button.setText("verify Code");




                        }
                    }
                });

                break;

            case 1:
                //step 2: verify code and then pass userid who email belong to the after code is been verified
                String code = emailCode.getText().toString().trim();

                verifyCodeViewModel.verifyChangePasswordCode(code);
                verifyCodeViewModel.getAuthCodeSuccess().observe(lifecycleOwner, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isSuccess) {
                        if (isSuccess) {
                            userId = verifyCodeViewModel.getUserId();
                            currentState = 2;
                            emailCodeInoutLay.setVisibility(View.GONE);
                            passwordInoutLay.setVisibility(View.VISIBLE);


                            displayText.setVisibility(View.GONE);
                            genericDefaultText.setText("please enter stronge password this help us prevent your account from being hacked and protect your information.");
                            genericDefaultText.setTextSize(10);
                            noteCode.setText("set up new password");
                            noteCode.setTextSize(10);
                            button.setText("Change Password");

                        }
                    }
                });

                break;

            case 2:
                //step 3: enter password and

                savedNewPassword = passwordText.getText().toString().trim();
                if (savedNewPassword.isEmpty()) {
                    noteCode.setText("New password required");
                    noteCode.setTextColor(Color.RED);

                } else if (savedNewPassword != null) {

                    //show confirm input if new password is not null
                    passwordInoutLay.setVisibility(View.VISIBLE);
                    confirmPasswordInputLayout.setVisibility(View.VISIBLE);

                    displayText.setText("Confirm Your Password");
                    genericDefaultText.setText("Confirm your new password to complete password reset process");

                    noteCode.setText("confirm your password");

                    button.setText("Reset Password");

                    currentState = 3;
                }


                break;


            case 3:
                //step 4: confirm password and reset
                //redirect to the home page
                String confirmPassword = passwordConfirm.getText().toString().trim();


                resetPasswordViewModel.ResetPassword(userId, savedNewPassword, confirmPassword);

                resetPasswordViewModel.getAuthRestPasswordSuccess().observe(lifecycleOwner, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isSuccess) {
                        if (isSuccess) {
                            String email = emailText.getText().toString().trim();
                            AppConfig.saveSession(context, userId, email);

                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            context.startActivity(intent);
                            ((AppCompatActivity) context).finish();
                        }
                    }
                });


                break;


        }

    }



}
