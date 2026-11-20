package com.example.projectaethera.repository;

import com.example.projectaethera.model.remote.PostRequest;
import com.example.projectaethera.model.remote.PostResponse;
import com.example.projectaethera.network.ApiService;
import com.example.projectaethera.network.RetrofitClient;

import retrofit2.Call;

/**
 * Wraps like-related API calls for liking, unliking, and checking
 * whether a user already liked a post.
 */
public class LikeRepository{
    private ApiService apiService;

    /** Creates the repository and gets the shared Retrofit API client. */
    public LikeRepository() {
        this.apiService = RetrofitClient.getClient();
    }

    /**
     * Adds a like from the user to the given post.
     *
     * @param userId id of the user who is liking
     * @param postId id of the post to like
     * @return Retrofit call with updated post like info
     */
    public Call<PostResponse> likePost(int userId, int postId){

        return apiService.likePost(userId, postId);
    }

    /**
     * Removes a like from the user on the given post.
     *
     * @param userId id of the user who is unliking
     * @param postId id of the post to unlike
     * @return Retrofit call with updated post like info
     */
    public Call<PostResponse> unlikePost(int userId, int postId){

        return apiService.unlikePost(userId, postId);
    }

    /**
     * Checks if the user has already liked the post.
     *
     * @param userId id of the user to check
     * @param postId id of the post to check
     * @return Retrofit call with true or false
     */
    public Call<Boolean> isLiked(int userId, int postId){
        return  apiService.isLiked(userId, postId) ;
    }
}
