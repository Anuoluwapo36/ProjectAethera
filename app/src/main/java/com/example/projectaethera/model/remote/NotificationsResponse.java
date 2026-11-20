package com.example.projectaethera.model.remote;



import java.util.List;



/**

 * JSON response from the notifications list API.

 * Contains a page of notification items for the user.

 */

public class NotificationsResponse {

    private boolean success;

    private String message;

    private List<NotificationItem> data;



    /** @return true if notifications loaded successfully */

    public boolean isSuccess() {

        return success;

    }



    /** @return server message about the result */

    public String getMessage() {

        return message;

    }



    /** @return list of notification items */

    public List<NotificationItem> getData() {

        return data;

    }

}

