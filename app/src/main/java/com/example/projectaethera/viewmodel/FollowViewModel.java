package com.example.projectaethera.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.projectaethera.model.remote.CountResponse;
import com.example.projectaethera.model.remote.FollowResponse;
import com.example.projectaethera.model.remote.IsFollowingResponse;
import com.example.projectaethera.repository.FollowRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ViewModel for follow/unfollow on someone else's profile.
 * <p>
 * I use this to show whether I follow a user, toggle follow with one button,
 * and keep follower/following counts in sync after each action. Extends
 * {@link AndroidViewModel} because I may need Application context later.
 */
public class FollowViewModel extends AndroidViewModel {

    private FollowRepository repository;
    /** Full server response from follow/unfollow toggle. */
    private MutableLiveData<FollowResponse> followResponse;
    /** Whether the logged-in user currently follows the profile being viewed. */
    private MutableLiveData<Boolean> isFollowing;
    /** Follower count for the profile on screen. */
    private MutableLiveData<Integer> followersCount = new MutableLiveData<>();
    /** Following count for the profile on screen. */
    private MutableLiveData<Integer> followingCount = new MutableLiveData<>();

    /**
     * Creates the ViewModel and initializes follow LiveData buckets.
     *
     * @param application Android application (required by AndroidViewModel)
     */
    public FollowViewModel(@NonNull Application application) {
        super(application);
        repository = new FollowRepository();
        followResponse = new MutableLiveData<>();
        isFollowing = new MutableLiveData<>();
    }

    /**
     * Asks the server if followerId already follows followingId.
     *
     * @param followerId logged-in user (me)
     * @param followingId profile I am viewing
     */
    public void checkIfFollowing(int followerId, int followingId) {
        repository.checkIsFollowing(followerId, followingId).enqueue(new Callback<IsFollowingResponse>() {
            @Override
            public void onResponse(Call<IsFollowingResponse> call, Response<IsFollowingResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    boolean followingStatus = response.body().isFollowing();
                    isFollowing.postValue(followingStatus);
                }
            }

            @Override
            public void onFailure(Call<IsFollowingResponse> call, Throwable t) {
                isFollowing.postValue(false);
            }
        });
    }

    /**
     * Follows or unfollows in one API call, then refreshes counts and follow state.
     *
     * @param followerId logged-in user (me)
     * @param followingId user whose Follow button was tapped
     */
    public void toggleFollow(int followerId, int followingId) {
        repository.followToggle(followerId, followingId).enqueue(new Callback<FollowResponse>() {
            @Override
            public void onResponse(Call<FollowResponse> call, Response<FollowResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FollowResponse body = response.body();
                    followResponse.postValue(body);

                    if (body.isSuccess()) {
                        isFollowing.postValue("follow".equals(body.getAction()));
                        refreshCounts(followingId);
                    }

                    checkIfFollowing(followerId, followingId);
                }
            }

            @Override
            public void onFailure(Call<FollowResponse> call, Throwable t) {
                followResponse.postValue(null);
            }
        });
    }

    /**
     * Loads how many users follow this profile.
     *
     * @param userId profile whose follower count we want
     */
    public void loadFollowersCount(int userId) {
        repository.getFollowersCount(userId).enqueue(new Callback<CountResponse>() {
            @Override
            public void onResponse(Call<CountResponse> call, Response<CountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    followersCount.postValue(response.body().getCount());
                }
            }
            @Override
            public void onFailure(Call<CountResponse> call, Throwable t) {}
        });
    }

    /**
     * Loads how many users this profile follows.
     *
     * @param userId profile whose following count we want
     */
    public void loadFollowingCount(int userId) {
        repository.getFollowingCount(userId).enqueue(new Callback<CountResponse>() {
            @Override
            public void onResponse(Call<CountResponse> call, Response<CountResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    followingCount.postValue(response.body().getCount());
                }
            }
            @Override
            public void onFailure(Call<CountResponse> call, Throwable t) {}
        });
    }

    /**
     * Reloads both follower and following counts — I call this after toggle follow.
     *
     * @param userId profile to refresh counts for
     */
    public void refreshCounts(int userId) {
        loadFollowersCount(userId);
        loadFollowingCount(userId);
    }

    /**
     * Raw follow/unfollow API response (action, message, etc.).
     *
     * @return LiveData with {@link FollowResponse}
     */
    public LiveData<FollowResponse> getFollowResponse() {
        return followResponse;
    }

    /**
     * Whether I am currently following the viewed user.
     *
     * @return LiveData true if following
     */
    public LiveData<Boolean> getIsFollowing() {
        return isFollowing;
    }

    /**
     * Follower count for the profile on screen.
     *
     * @return LiveData with count
     */
    public LiveData<Integer> getFollowersCount() { return followersCount; }

    /**
     * Following count for the profile on screen.
     *
     * @return LiveData with count
     */
    public LiveData<Integer> getFollowingCount() { return followingCount; }
}
