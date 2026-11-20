package com.example.projectaethera.model.remote;



/**

 * JSON response from the unread notification count API.

 * Returns how many notifications the user has not read yet.

 */

public class UnreadCountResponse {

    private boolean success;

    private String message;

    private int unreadCount;



    /** @return true if the count loaded successfully */

    public boolean isSuccess() {

        return success;

    }



    /** @return server message about the result */

    public String getMessage() {

        return message;

    }



    /** @return number of unread notifications */

    public int getUnreadCount() {

        return unreadCount;

    }

}

