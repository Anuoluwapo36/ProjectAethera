package com.example.projectaethera.model.remote;



/**

 * Request body sent when marking a notification as read.

 * Tells the server which user owns the notification.

 */

public class MarkReadRequest {

    private final int userId;



    /**

     * @param userId id of the user marking the notification read

     */

    public MarkReadRequest(int userId) {

        this.userId = userId;

    }



    /** @return id of the user who owns the notification */

    public int getUserId() {

        return userId;

    }

}

