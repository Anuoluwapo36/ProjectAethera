package com.example.projectaethera.viewmodel;



import androidx.lifecycle.MutableLiveData;

import androidx.lifecycle.ViewModel;



import com.example.projectaethera.model.remote.CheckEmailRequest;

import com.example.projectaethera.model.remote.CheckEmailResponse;

import com.example.projectaethera.repository.AuthRepository;



import retrofit2.Call;

import retrofit2.Callback;

import retrofit2.Response;



/**

 * ViewModel for the first step of forgot-password: checking if an email exists.

 * <p>

 * I use this before sending a reset code. If the email is registered, the UI

 * moves to the verify-code screen; if not, we show the server message.

 */

public class CheckEmailViewModel extends ViewModel {

    /** One-shot true when the email exists and the server is ready to send a code. */

    private final SingleLiveEvent<Boolean> authCheckEmailSuccess = new SingleLiveEvent<>();

    /** Message from the server (error or "check your inbox"). */

    private final  MutableLiveData<String>  authCheckEmailErrorMessage = new MutableLiveData<>();



    /** Spinner while we ask the backend if this email is registered. */

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);



    private AuthRepository authRepository;



    /** Creates the ViewModel and auth repository. */

    public CheckEmailViewModel() {

        this.authRepository =  new AuthRepository();

    }



    /**

     * Asks the server whether this email belongs to a registered user.

     *

     * @param email address the user typed on forgot-password screen

     */

    public void confirmUserExistEmail (String email){

        //create object for email

        CheckEmailRequest emailRequest = new CheckEmailRequest();

        emailRequest.setEmail(email);

        isLoading.postValue(true);



        //call repository

        authRepository.confirmEmail(emailRequest).enqueue(new Callback<CheckEmailResponse>() {

            @Override

            public void onResponse(Call<CheckEmailResponse> call, Response<CheckEmailResponse> response) {

                //is success

                if (response.isSuccessful() && response.body() != null){

                    CheckEmailResponse body = response.body();



                    authCheckEmailSuccess.postValue(body.isSuccess());

                    authCheckEmailErrorMessage.postValue(body.getMessage());

                }else {

                    authCheckEmailSuccess.postValue(false);

                    authCheckEmailErrorMessage.postValue("something went wrong");

                }

                isLoading.postValue(false);

            }

            //on failure message

            @Override

            public void onFailure(Call<CheckEmailResponse> call, Throwable throwable) {

                isLoading.postValue(false);

                authCheckEmailSuccess.postValue(false);

                authCheckEmailErrorMessage.postValue("Network error" + throwable);

            }

        });

    }



    /**

     * One-shot result of the email check.

     *

     * @return SingleLiveEvent true if email exists and flow can continue

     */

    public SingleLiveEvent<Boolean> getAuthCheckEmailSuccess() {

        return authCheckEmailSuccess;

    }



    /**

     * Message to show after check-email (success hint or error).

     *

     * @return LiveData with user-facing text

     */

    public MutableLiveData<String> getAuthCheckEmailErrorMessage() {

        return authCheckEmailErrorMessage;

    }



    /**

     * Whether the check-email request is still running.

     *

     * @return LiveData true while loading

     */

    public MutableLiveData<Boolean> getIsLoading() {

        return isLoading;

    }

}

