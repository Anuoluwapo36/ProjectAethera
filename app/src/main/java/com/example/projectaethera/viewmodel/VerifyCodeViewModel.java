package com.example.projectaethera.viewmodel;



import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectaethera.model.remote.VerifyCodeRequest;
import com.example.projectaethera.model.remote.VerifyCodeResponse;
import com.example.projectaethera.repository.AuthRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for the "enter reset code" step of forgot-password.
 * <p>
 * I use this when the user types the code from their email. It talks to
 * {@link AuthRepository}, saves the userId from the server on success, and
 * exposes one-shot success so the UI can navigate to the new-password screen
 * without firing again after rotation.
 */
public class VerifyCodeViewModel extends ViewModel {

    private AuthRepository authRepository;

    /** Set after a valid code — needed for the reset-password API call. */
    private  int userId = -1;

    /**
     * Creates the ViewModel and sets up the auth repository.
     */
    public VerifyCodeViewModel() {
        this.authRepository = new AuthRepository();
    }

    /** One-shot true/false when the code check finishes (success or fail). */
    private final SingleLiveEvent<Boolean> authCodeSuccess = new SingleLiveEvent<>();
    /** Error or server message shown in the UI. */
    private final MutableLiveData<String> authCodeMessageError = new MutableLiveData<>();
    /** True while the verify request is in flight. */
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    /**
     * Sends the verification code to the server for validation.
     *
     * @param code the digits the user typed from their email
     */
    public void verifyChangePasswordCode(String code){
        VerifyCodeRequest codeRequest = new VerifyCodeRequest();
        codeRequest.setCode(code);
        isLoading.postValue(true);
        authRepository.verifyPasswordCode(codeRequest).enqueue(new Callback<VerifyCodeResponse>() {
            @Override
            public void onResponse(Call<VerifyCodeResponse> call, Response<VerifyCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null){
                    VerifyCodeResponse body =  response.body();

                    userId = body.getUserId();
                    authCodeSuccess.postValue(body.isSuccess());

                    authCodeMessageError.postValue(body.getMessage());
                }else {
                    authCodeSuccess.postValue(false);
                    authCodeMessageError.postValue("something went wrong");
                }
                isLoading.postValue(false);
            }

            @Override
            public void onFailure(Call<VerifyCodeResponse> call, Throwable throwable) {
                isLoading.postValue(false);
                authCodeSuccess.postValue(false);
                authCodeMessageError.postValue("Network error" + throwable);

            }
        });



    }

    /**
     * Error or info message from the verify-code API.
     *
     * @return LiveData with the message to show the user
     */
    public MutableLiveData<String> getAuthCodeMessageError() {
        return authCodeMessageError;
    }

    /**
     * One-shot event when verification succeeds or fails.
     *
     * @return SingleLiveEvent with true if the code was valid
     */
    public SingleLiveEvent<Boolean> getAuthCodeSuccess() {
        return authCodeSuccess;
    }

    /**
     * User id returned by the server after a valid code — pass this to reset password.
     *
     * @return user id, or -1 if verify has not succeeded yet
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Whether a verify request is currently running (for showing a spinner).
     *
     * @return LiveData true while loading
     */
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
