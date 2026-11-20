package com.example.projectaethera.repository;

import com.example.projectaethera.model.remote.ProfileRequest;
import com.example.projectaethera.model.remote.UserResponse;
import com.example.projectaethera.network.ApiService;
import com.example.projectaethera.network.RetrofitClient;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

/**
 * Wraps user profile API calls for fetching details, editing text fields,
 * and uploading a new profile image.
 */
public class UserRepository {
    private ApiService apiService ;

    /** Creates the repository and gets the shared Retrofit API client. */
    public UserRepository() {
        this.apiService = RetrofitClient.getClient();
    }

    /**
     * Fetches a user's profile details from the server.
     *
     * @param userId id of the user to load
     * @return Retrofit call with user profile data
     */
    public Call<UserResponse> getDetails(int userId){
        return apiService.getUserDetails(userId);
    }

    /**
     * Updates profile text fields such as username and bio.
     *
     * @param userId id of the user being edited
     * @param body   updated profile fields
     * @return Retrofit call with updated user profile
     */
    public Call<UserResponse> editProfile(int userId, ProfileRequest body){
        return apiService.editProfile(userId, body);
    }

    /**
     * Uploads a new profile image for the user.
     *
     * @param userId       id of the user being edited
     * @param profileImage image file as multipart data
     * @return Retrofit call with updated user profile
     */
    public  Call<UserResponse> updateProfile(int userId,MultipartBody.Part profileImage){
        return apiService.updateProfile(userId, profileImage);
    }
}
