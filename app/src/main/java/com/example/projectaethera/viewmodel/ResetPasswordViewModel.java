package com.example.projectaethera.viewmodel;



import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;



import com.example.projectaethera.model.remote.ChangePasswordResponse;

import com.example.projectaethera.model.remote.PasswordResetRequest;

import com.example.projectaethera.repository.AuthRepository;



import retrofit2.Call;

import retrofit2.Callback;

import retrofit2.Response;



/**

 * ViewModel for setting a new password after the user verified their email code.

 * <p>

 * I use this on the reset-password screen. The fragment passes userId from

 * {@link VerifyCodeViewModel} plus the new password fields, and observes

 * success/error LiveData to show feedback or go back to login.

 */

public class ResetPasswordViewModel extends ViewModel {



    /** One-shot event so navigation to login only happens once. */

    private final SingleLiveEvent<Boolean> authRestPasswordSuccess = new SingleLiveEvent<>();

    /** Server or validation message for the UI. */

    private final MutableLiveData<String> authRestPassWordErrorMessage = new MutableLiveData<>();

    /** True while the update-password call is running. */

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);



    private final AuthRepository authRepository;



    /** Wires up {@link AuthRepository} for the reset-password API. */

    public ResetPasswordViewModel() {

        this.authRepository = new AuthRepository();

    }



    /**

     * Sends the new password to the backend for the given user.

     *

     * @param userId id from verify-code step

     * @param newPassword what the user typed

     * @param confirmPassword must match newPassword on the server

     */

    public void ResetPassword(int userId, String newPassword, String confirmPassword){

        PasswordResetRequest resetRequest = new PasswordResetRequest();

        resetRequest.setUserId(userId);

        resetRequest.setNewPassword(newPassword);

        resetRequest.setConfirmPassword(confirmPassword);

        isLoading.postValue(true);



        authRepository.updatePassword(resetRequest).enqueue(new Callback<ChangePasswordResponse>() {



            @Override

            public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {

                if (response.isSuccessful() && response.body() != null){



                    ChangePasswordResponse passwordResponse = response.body();



                    authRestPasswordSuccess.postValue(passwordResponse.isSuccess());

                    authRestPassWordErrorMessage.postValue(passwordResponse.getMessage());



                }else{

                    authRestPasswordSuccess.postValue(false);

                    authRestPassWordErrorMessage.postValue("something went wrong");

                }

                isLoading.postValue(false);

            }



            @Override

            public void onFailure(Call<ChangePasswordResponse> call, Throwable throwable) {

                isLoading.postValue(false);

                authRestPasswordSuccess.postValue(false);

                authRestPassWordErrorMessage.postValue("Network error" + throwable);

            }

        });





    }



    /**

     * One-shot success after password reset — observe to navigate away.

     *

     * @return SingleLiveEvent true when the server accepted the new password

     */

    public  SingleLiveEvent<Boolean> getAuthRestPasswordSuccess() {

        return authRestPasswordSuccess;

    }



    /**

     * Error or success message from the reset API.

     *

     * @return LiveData with text for Toast or TextView

     */

    public MutableLiveData<String> getAuthRestPassWordErrorMessage() {

        return authRestPassWordErrorMessage;

    }



    /**

     * Whether a reset request is in progress.

     *

     * @return LiveData true while loading

     */

    public MutableLiveData<Boolean> getIsLoading() {

        return isLoading;

    }

}

