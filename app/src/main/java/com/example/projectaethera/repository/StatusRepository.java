package com.example.projectaethera.repository;



import com.example.projectaethera.model.remote.StatusFeedResponse;

import com.example.projectaethera.model.remote.StatusResponse;

import com.example.projectaethera.model.remote.StatusUserResponse;

import com.example.projectaethera.network.ApiService;

import com.example.projectaethera.network.RetrofitClient;



import okhttp3.MultipartBody;

import okhttp3.RequestBody;

import retrofit2.Call;



/**

 * Wraps status (story) API calls for loading feeds, creating statuses,

 * marking them viewed, liking, unliking, and deleting.

 */

public class StatusRepository {



    private final ApiService apiService;



    /** Creates the repository and gets the shared Retrofit API client. */

    public StatusRepository() {

        apiService = RetrofitClient.getClient();

    }



    /**

     * Loads the status feed grouped by user for the home screen.

     *

     * @param userId id of the logged-in viewer

     * @return Retrofit call with grouped status feed data

     */

    public Call<StatusFeedResponse> getStatusFeed(int userId) {

        return apiService.getStatusFeed(userId);

    }



    /**

     * Loads all statuses posted by one user.

     *

     * @param targetUserId id of the user whose statuses to load

     * @param viewerId     id of the user viewing the statuses

     * @return Retrofit call with that user's status list

     */

    public Call<StatusUserResponse> getUserStatuses(int targetUserId, int viewerId) {

        return apiService.getUserStatuses(targetUserId, viewerId);

    }



    /**

     * Creates a new status with an image and optional caption.

     *

     * @param userId  id of the user posting the status

     * @param caption text caption for the status

     * @param image   image file as multipart data

     * @return Retrofit call with create result

     */

    public Call<StatusResponse> createStatus(RequestBody userId, RequestBody caption, MultipartBody.Part image) {

        return apiService.createStatus(userId, caption, image);

    }



    /**

     * Marks a status as viewed by the current user.

     *

     * @param statusId id of the status that was viewed

     * @param viewerId id of the user who viewed it

     * @return Retrofit call with mark-viewed result

     */

    public Call<StatusResponse> markStatusViewed(int statusId, int viewerId) {

        return apiService.markStatusViewed(statusId, viewerId);

    }



    /**

     * Adds a like from the user to a status.

     *

     * @param userId   id of the user liking the status

     * @param statusId id of the status to like

     * @return Retrofit call with updated like count

     */

    public Call<StatusResponse> likeStatus(int userId, int statusId) {

        return apiService.likeStatus(userId, statusId);

    }



    /**

     * Removes a like from the user on a status.

     *

     * @param userId   id of the user unliking the status

     * @param statusId id of the status to unlike

     * @return Retrofit call with updated like count

     */

    public Call<StatusResponse> unlikeStatus(int userId, int statusId) {

        return apiService.unlikeStatus(userId, statusId);

    }



    /**

     * Deletes a status owned by the given user.

     *

     * @param userId   id of the user who owns the status

     * @param statusId id of the status to delete

     * @return Retrofit call with delete result

     */

    public Call<StatusResponse> deleteStatus(int userId, int statusId) {

        return apiService.deleteStatus(userId, statusId);

    }

}

