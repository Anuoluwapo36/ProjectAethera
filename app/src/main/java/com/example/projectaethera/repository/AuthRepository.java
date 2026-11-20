package com.example.projectaethera.repository;

import com.example.projectaethera.model.remote.ChangePasswordResponse;
import com.example.projectaethera.model.remote.CheckEmailRequest;
import com.example.projectaethera.model.remote.CheckEmailResponse;
import com.example.projectaethera.model.remote.LoginRequest;
import com.example.projectaethera.model.remote.LoginResponse;
import com.example.projectaethera.model.remote.PasswordResetRequest;
import com.example.projectaethera.model.remote.RegisterRequest;
import com.example.projectaethera.model.remote.RegisterResponse;
import com.example.projectaethera.model.remote.VerifyCodeRequest;
import com.example.projectaethera.model.remote.VerifyCodeResponse;
import com.example.projectaethera.network.ApiService;
import com.example.projectaethera.network.RetrofitClient;

import retrofit2.Call;

/**
 * Wraps authentication API calls such as login, register, email check,
 * password reset code verification, and password update.
 */
public class AuthRepository {

    private final ApiService apiService;


    /** Creates the repository and gets the shared Retrofit API client. */
    public AuthRepository() {
        this.apiService = RetrofitClient.getClient();
    }

    /**
     * Sends a login request to POST /api/login.
     *
     * @param request email and password for the user
     * @return Retrofit call with login result and user id
     */
    public Call<LoginResponse> loginUser(LoginRequest request){
        return apiService.login(request);
    }

    /**
     * Sends a register request to POST /api/register.
     *
     * @param request new user details to create an account
     * @return Retrofit call with register result
     */
    public  Call<RegisterResponse> registerUser(RegisterRequest request){
        return  apiService.register(request);
    }

    /**
     * Checks whether an email exists via the check-email API.
     *
     * @param request email address to look up
     * @return Retrofit call with whether the email is registered
     */
    public Call<CheckEmailResponse> confirmEmail(CheckEmailRequest request){
        return apiService.checkEmail(request);
    }

    /**
     * Verifies a password reset code sent to the user's email.
     *
     * @param request email and reset code to verify
     * @return Retrofit call with verification result
     */
    public Call<VerifyCodeResponse> verifyPasswordCode(VerifyCodeRequest request){
        return  apiService.verifyCode(request);
    }

    /**
     * Updates the user's password after a successful code verification.
     *
     * @param resetRequest new password and related reset data
     * @return Retrofit call with password change result
     */
    public Call<ChangePasswordResponse> updatePassword(PasswordResetRequest resetRequest){
        return  apiService.resetPassword(resetRequest);
    }
}
