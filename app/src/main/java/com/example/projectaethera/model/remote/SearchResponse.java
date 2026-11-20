package com.example.projectaethera.model.remote;

import com.example.projectaethera.model.entity.UserProfile;

import java.util.List;

/**
 * JSON response from the user search API.
 * Returns a list of user profiles matching the search query.
 */
public class SearchResponse {

    private boolean success;
    private String message;

    private List<UserProfile> data;

    /** @return true if the search succeeded */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success whether the search call succeeded
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /** @return server message about the result */
    public String getMessage() {
        return message;
    }

    /**
     * @param message server message to store
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /** @return list of matching user profiles */
    public List<UserProfile> getData() {
        return data;
    }
}
