package com.example.projectaethera.repository;

import com.example.projectaethera.model.remote.SearchResponse;
import com.example.projectaethera.network.ApiService;
import com.example.projectaethera.network.RetrofitClient;

import retrofit2.Call;

/**
 * Wraps the search API call used to find users by name or username.
 */
public class SearchRepository {

    private ApiService apiService;

    /** Creates the repository and gets the shared Retrofit API client. */
    public SearchRepository() {
        this.apiService = RetrofitClient.getClient();
    }

    /**
     * Searches for users matching the given query text.
     *
     * @param userId id of the user performing the search
     * @param query  text to search for
     * @return Retrofit call with matching user profiles
     */
    public Call<SearchResponse> searching( int userId, String query){
        return apiService.searching(userId, query);
    }
}
