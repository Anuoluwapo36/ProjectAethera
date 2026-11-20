package com.example.projectaethera.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectaethera.model.remote.RegisterResponse;
import com.example.projectaethera.model.remote.RegisterRequest;
import com.example.projectaethera.network.ApiService;
import com.example.projectaethera.network.RetrofitClient;
import com.example.projectaethera.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for the sign-up / register screen.
 * <p>
 * I validate the form locally first (password rules, terms checkbox), then call
 * the register API. On success the UI gets the new user id and can go to login
 * or straight into the app depending on our flow.
 */
public class RegisterViewModel extends ViewModel {

    /** True when the server created the account successfully. */
    private final MutableLiveData<Boolean> registrationSuccess = new MutableLiveData<>();
    /** New user's id from the server — useful for auto-login or analytics. */
    private final MutableLiveData<Integer> registeredUserId = new MutableLiveData<>();
    /** Validation error or server message. */
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    /** True while the register call is in flight. */
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final AuthRepository repository;

    /** Creates the ViewModel and auth repository. */
    public RegisterViewModel() {

        repository= new AuthRepository();
    }

    /**
     * Validates input and registers a new user with the backend.
     *
     * @param username display name chosen by the user
     * @param email email address
     * @param termsAccepted whether they checked the terms box
     * @param password chosen password
     */
    public void registerUser(String username, String email, boolean termsAccepted, String password) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername(username);
        registerRequest.setEmail(email);
        registerRequest.setPassword(password);
        registerRequest.setTermsAccepted(termsAccepted);


        String error = registerRequest.validateUser();

        if (error != null) {
            errorMessage.postValue(error);
        } else {
            isLoading.postValue(true);
            repository.registerUser(registerRequest).enqueue(new Callback<RegisterResponse>() {
                @Override
                public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.e("API_DEBUG", "SUCCESS: " + response.body().isSuccess());
                        Log.e("API_DEBUG", "MESSAGE: " + response.body().getMessage());
                        RegisterResponse body = response.body();

                        Log.d("server message ", "got" + response.message());
                        if (body.isSuccess()) {
                            registeredUserId.postValue(body.getUserId());
                            registrationSuccess.postValue(true);
                        } else {
                            registrationSuccess.postValue(false);
                        }
                        errorMessage.postValue(body.getMessage());
                        isLoading.postValue(false);
                    } else {
                        isLoading.postValue(false);
                        registrationSuccess.postValue(false);
                        errorMessage.postValue("something went wrong ");
                    }


                }

                @Override
                public void onFailure(Call<RegisterResponse> call, Throwable throwable) {
                    isLoading.postValue(false);

                    registrationSuccess.postValue(false);
                    errorMessage.postValue("Network error" + throwable.getMessage());

                }
            });
        }
    }

    /**
     * Whether registration succeeded.
     *
     * @return LiveData true on success
     */
    public MutableLiveData<Boolean> getRegistrationSuccess() {
        return registrationSuccess;
    }

    /**
     * Id of the newly registered user.
     *
     * @return LiveData with user id after success
     */
    public MutableLiveData<Integer> getRegisteredUserId() {
        return registeredUserId;
    }

    /**
     * Error or server message from register.
     *
     * @return LiveData with user-facing text
     */
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Whether a register request is running.
     *
     * @return LiveData true while loading
     */
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
