package com.example.projectaethera.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.projectaethera.model.remote.UserResponse;
import com.example.projectaethera.repository.UserRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel that loads user profile info from the API.
 * <p>
 * I split this into TWO LiveData buckets on purpose:
 * <ul>
 *   <li>{@link #loggedInUserData} = the person who is logged in (me)</li>
 *   <li>{@link #viewedUserData} = someone else's profile I opened from feed/search</li>
 * </ul>
 * Before, there was only one bucket, so when I opened Mercy's profile her info
 * also showed on Home and on my own Profile tab. That was the bug.
 */
public class UserViewModel extends ViewModel {

    private final UserRepository userRepository;

    /** Info for the logged-in user only (Home story row, my Profile tab, Settings). */
    private final MutableLiveData<UserResponse> loggedInUserData = new MutableLiveData<>();

    /** Info for another user's profile opened as an overlay (e.g. from a post). */
    private final MutableLiveData<UserResponse> viewedUserData = new MutableLiveData<>();

    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    /** Creates the ViewModel and wires up the user API repository. */
    public UserViewModel() {
        this.userRepository = new UserRepository();
    }

    /**
     * Loads profile details for the logged-in user.
     * Use this on Home, Settings, Edit Profile, and your own Profile tab.
     *
     * @param userId the id saved in AppConfig after login
     */
    public void fetchLoggedInUserDetails(int userId) {
        if (userId <= 0) {
            return;
        }
        userRepository.getDetails(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse body = response.body();
                    stampUserIdIfMissing(body, userId);
                    loggedInUserData.postValue(body);
                } else {
                    errorMessage.postValue("something went wrong");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable throwable) {
                errorMessage.postValue("network error");
            }
        });
    }

    /**
     * Loads profile details for someone else (not necessarily the logged-in user).
     * Use this when ProfileFragment opens with a userId in the arguments bundle.
     *
     * @param userId the profile we are viewing (e.g. Mercy)
     */
    public void fetchViewedUserDetails(int userId) {
        if (userId <= 0) {
            return;
        }
        userRepository.getDetails(userId).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse body = response.body();
                    stampUserIdIfMissing(body, userId);
                    viewedUserData.postValue(body);
                } else {
                    errorMessage.postValue("something went wrong");
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable throwable) {
                errorMessage.postValue("network error");
            }
        });
    }

    /**
     * Pushes updated profile info into the logged-in user bucket after edit/save.
     * I use this so Home and Profile tab update without restarting the app.
     *
     * @param response fresh user payload from edit API or photo upload
     */
    public void applyLoggedInUserResponse(UserResponse response) {
        if (response != null && response.getData() != null) {
            loggedInUserData.postValue(response);
        }
    }

    /**
     * Called after the user saves their profile in EditProfileFragment.
     * Updates local data first, then re-fetches from the server to stay in sync.
     *
     * @param userId logged-in user's id
     * @param updatedResponse profile data returned from the save call
     */
    public void notifyProfileUpdated(int userId, UserResponse updatedResponse) {
        if (updatedResponse != null) {
            applyLoggedInUserResponse(updatedResponse);
        }
        fetchLoggedInUserDetails(userId);
    }

    /**
     * LiveData for the logged-in user's profile. Home and my Profile tab observe this.
     *
     * @return LiveData holding my profile info
     */
    public MutableLiveData<UserResponse> getLoggedInUserData() {
        return loggedInUserData;
    }

    /**
     * LiveData for another user's profile opened from feed/search/notifications.
     *
     * @return LiveData holding the profile I am currently viewing
     */
    public MutableLiveData<UserResponse> getViewedUserData() {
        return viewedUserData;
    }

    /**
     * Error messages from any user fetch (logged-in user or viewed user).
     *
     * @return LiveData with a human-readable error string, or null when clear
     */
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sometimes the API gives username and photo but userId comes back as 0 in the app.
     * I stamp the id we asked for so ProfileFragment can match it to the right person.
     */
    private void stampUserIdIfMissing(UserResponse response, int requestedUserId) {
        if (response != null && response.getData() != null && response.getData().getUserId() == 0) {
            response.getData().setUserId(requestedUserId);
        }
    }
}
