package com.example.projectaethera.repository;



import com.example.projectaethera.model.remote.MarkReadRequest;

import com.example.projectaethera.model.remote.NotificationsResponse;

import com.example.projectaethera.model.remote.UnreadCountResponse;

import com.example.projectaethera.network.ApiService;

import com.example.projectaethera.network.RetrofitClient;



import retrofit2.Call;



/**

 * Wraps notification API calls for loading notifications, counting

 * unread items, and marking one or all as read.

 */

public class NotificationRepository {



    private final ApiService apiService;



    /** Creates the repository and gets the shared Retrofit API client. */

    public NotificationRepository() {

        this.apiService = RetrofitClient.getClient();

    }



    /**

     * Loads a page of notifications for the user.

     *

     * @param userId id of the user whose notifications to fetch

     * @param page   page number for pagination

     * @param limit  max number of notifications per page

     * @return Retrofit call with notification list

     */

    public Call<NotificationsResponse> getNotifications(int userId, int page, int limit) {

        return apiService.getNotifications(userId, page, limit);

    }



    /**

     * Gets how many unread notifications the user has.

     *

     * @param userId id of the user to count unread items for

     * @return Retrofit call with unread count

     */

    public Call<UnreadCountResponse> getUnreadCount(int userId) {

        return apiService.getUnreadCount(userId);

    }



    /**

     * Marks a single notification as read.

     *

     * @param notificationId id of the notification to mark read

     * @param userId         id of the user who owns the notification

     * @return Retrofit call with updated notification data

     */

    public Call<NotificationsResponse> markAsRead(int notificationId, int userId) {

        return apiService.markNotificationRead(notificationId, new MarkReadRequest(userId));

    }



    /**

     * Marks every notification as read for the user.

     *

     * @param userId id of the user whose notifications to clear

     * @return Retrofit call with updated notification data

     */

    public Call<NotificationsResponse> markAllAsRead(int userId) {

        return apiService.markAllNotificationsRead(userId);

    }

}

