package com.example.projectaethera.repository;

import com.example.projectaethera.model.remote.CountResponse;
import com.example.projectaethera.model.remote.FollowResponse;
import com.example.projectaethera.model.remote.IsFollowingResponse;
import com.example.projectaethera.network.ApiService;
import com.example.projectaethera.network.RetrofitClient;

import retrofit2.Call;

/**
 * Wraps follow-related API calls for toggling follow state, checking
 * if one user follows another, and getting follower/following counts.
 */
public class FollowRepository {

    private ApiService apiService;

    /** Creates the repository and gets the shared Retrofit API client. */
    public FollowRepository(){
        this.apiService = RetrofitClient.getClient();
    }

    /**
     * Follows or unfollows a user depending on current state.
     *
     * @param followerId  id of the user doing the follow action
     * @param followingId id of the user to follow or unfollow
     * @return Retrofit call with follow/unfollow result
     */
    public Call<FollowResponse> followToggle(int followerId, int followingId){
        return apiService.toggleFollow(followerId, followingId);
    }

    /**
     * Checks whether one user is following another.
     *
     * @param followerId  id of the potential follower
     * @param followingId id of the user being checked
     * @return Retrofit call with true if already following
     */
    public Call<IsFollowingResponse> checkIsFollowing(int followerId, int followingId) {
        return apiService.isFollowing(followerId, followingId);
    }

    /**
     * Gets how many users follow the given account.
     *
     * @param userId id of the user whose follower count to fetch
     * @return Retrofit call with follower count
     */
    public Call<CountResponse> getFollowersCount(int userId) {
        return apiService.getFollowersCount(userId);
    }

    /**
     * Gets how many users the given account is following.
     *
     * @param userId id of the user whose following count to fetch
     * @return Retrofit call with following count
     */
    public Call<CountResponse> getFollowingCount(int userId) {
        return apiService.getFollowingCount(userId);
    }
}
