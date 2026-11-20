package com.example.projectaethera.repository;



import com.example.projectaethera.model.entity.Post;

import com.example.projectaethera.model.remote.PostResponse;

import com.example.projectaethera.model.remote.UserPostResponse;

import com.example.projectaethera.network.ApiService;

import com.example.projectaethera.network.RetrofitClient;



import java.util.List;



import okhttp3.MultipartBody;

import okhttp3.RequestBody;

import retrofit2.Call;



/**

 * Repository for creating, reading, and deleting posts.

 * <p>

 * ViewModels call these methods and enqueue the returned {@link Call} objects.

 * Upload uses multipart because posts can have multiple images.

 */

public class PostRepository {



    private ApiService apiService;



    /** Creates the repository with the shared Retrofit client. */

    public PostRepository() {

        this.apiService = RetrofitClient.getClient();

    }



    /**

     * Uploads a new post with caption and one or more images.

     *

     * @param userId form field for author id

     * @param caption form field for post text

     * @param image multipart parts for each selected photo

     * @return Retrofit call with {@link PostResponse}

     */

    public Call<PostResponse> uploadPost(

            RequestBody userId,

            RequestBody caption,

            List<MultipartBody.Part> image

    ) {

        return apiService.uploadPost(userId, caption, image);

    }



    /**

     * Gets all posts for a user's profile grid.

     *

     * @param userId profile owner

     * @return Retrofit call with list of posts

     */

    public Call<UserPostResponse> getUserPost(int userId){

        return apiService.getUserPost(userId);

    }



    /**

     * Loads one post for the detail screen.

     *

     * @param postId post to show

     * @param userId viewer (for like state on some backends)

     * @return Retrofit call with single {@link Post}

     */

    public Call<PostResponse> getPostDetails(int postId, int userId){

        return  apiService.postDetails(postId, userId);

    }



    /**

     * Deletes a post owned by the user.

     *

     * @param userId owner id (server checks permission)

     * @param postId post to remove

     * @return Retrofit call with success flag

     */

    public Call<PostResponse> deletePost(int userId, int postId) {

        return apiService.deletePost(userId, postId);

    }



}

