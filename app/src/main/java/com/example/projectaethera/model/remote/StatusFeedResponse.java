package com.example.projectaethera.model.remote;



import java.util.List;



/**

 * JSON response from the status feed API.

 * Returns statuses grouped by user for the home screen stories row.

 */

public class StatusFeedResponse {

    private boolean success;

    private String message;

    private List<StatusGroup> data;



    /** @return true if the feed loaded successfully */

    public boolean isSuccess() {

        return success;

    }



    /** @return server message about the result */

    public String getMessage() {

        return message;

    }



    /** @return list of users with their status groups */

    public List<StatusGroup> getData() {

        return data;

    }

}

