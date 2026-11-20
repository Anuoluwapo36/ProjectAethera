package com.example.projectaethera.repository;

import com.example.projectaethera.model.remote.HomeFeedResponse;
import com.example.projectaethera.network.ApiService;
import com.example.projectaethera.network.RetrofitClient;

import retrofit2.Call;

/**
 * Wraps the home feed API call that loads posts from users the
 * current user follows, with paging support.
 */
public class HomeFeedRepository {

    private ApiService apiService;

    /** Creates the repository and gets the shared Retrofit API client. */
    public HomeFeedRepository() {
        this.apiService = RetrofitClient.getClient();
    }

    /**
     * Loads a page of posts for the user's home feed.
     *
     * @param userId id of the logged-in user
     * @param page   page number for pagination
     * @return Retrofit call with feed posts
     */
    public Call<HomeFeedResponse> homeFeed(int userId, int page){
        return apiService.homeFeed(userId,page);
    }
}
