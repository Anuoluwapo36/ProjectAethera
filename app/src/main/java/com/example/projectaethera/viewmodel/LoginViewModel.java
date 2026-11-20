package com.example.projectaethera.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.model.remote.LoginRequest;
import com.example.projectaethera.model.remote.LoginResponse;
import com.example.projectaethera.network.ApiService;
import com.example.projectaethera.network.RetrofitClient;
import com.example.projectaethera.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for the login screen — validates input and calls the login API.
 * <p>
 * I keep auth state in LiveData so the Activity/Fragment can show errors,
 * a loading spinner, and navigate to Home when {@link #getAuthSuccess()} is true.
 * The user id is posted separately so we can save it to {@link AppConfig} after login.
 */
public class LoginViewModel extends ViewModel {

    /** True when the server says login succeeded. */
    private final MutableLiveData<Boolean>  authSuccess = new MutableLiveData<>();
    /** Validation error or server message for the user. */
    private final  MutableLiveData<String>  authErrorMessage = new MutableLiveData<>();
    /** True while the login Retrofit call is in flight. */
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    /** User id from a successful login — observe to persist session. */
    private MutableLiveData<Integer> userId = new MutableLiveData<>();

    /**
     * LiveData for the logged-in user's id after a successful response.
     *
     * @return LiveData with user id, or null until login completes
     */
    public MutableLiveData<Integer> getUserId() {
        return userId;
    }

    private AuthRepository repository;

    /** Creates the ViewModel and auth repository. */
    public LoginViewModel( ) {
        this.repository = new AuthRepository();
    }

    /**
     * Validates email/password locally, then calls the login API if input is OK.
     *
     * @param email what the user typed in the email field
     * @param password what the user typed in the password field
     */
    public void authenticateUser(String email, String password){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);


        String loginError = loginRequest.validateUser();

        if(loginError != null){
            authErrorMessage.postValue(loginError);
        }else {
            isLoading.postValue(true);
            repository.loginUser(loginRequest).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if(response.isSuccessful() && response.body() != null){
                        LoginResponse body = response.body();

                        userId.postValue(body.getUserId());

                        authSuccess.postValue(body.isSuccess());
                        authErrorMessage.postValue(body.getMessage());

                        if (!body.isSuccess()) {
                            isLoading.postValue(false);
                        }
                    }else {
                        isLoading.postValue(false);
                        authSuccess.postValue(false);
                        authErrorMessage.postValue("something went wrong try again later");
                    }

                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable throwable) {
                    isLoading.postValue(false);

                    authSuccess.postValue(false);
                    authErrorMessage.postValue("Network error" + throwable.getMessage() );

                }
            });
        }


    }

    /**
     * Whether login succeeded — observe to open the main app.
     *
     * @return LiveData true on successful auth
     */
    public MutableLiveData<Boolean> getAuthSuccess() {
        return authSuccess;
    }

    /**
     * Error or info message from validation or the server.
     *
     * @return LiveData with text for the UI
     */
    public MutableLiveData<String> getAuthErrorMessage() {
        return authErrorMessage;
    }

    /**
     * Whether a login request is currently running.
     *
     * @return LiveData true while loading
     */
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }





}
