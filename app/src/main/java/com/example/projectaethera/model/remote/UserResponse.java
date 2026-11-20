package com.example.projectaethera.model.remote;

import com.example.projectaethera.model.entity.UserProfile;
import com.example.projectaethera.model.entity.Users;

/**
 * JSON response from user profile APIs such as get details and edit profile.
 * Wraps a UserProfile with success and message fields.
 */
public class UserResponse {
    private boolean success;
    private  String message;
    private UserProfile data;

    /** @return true if the user request succeeded */
    public boolean isSuccess() {
        return success;
    }

    /** @return server message about the result */
    public String getMessage() {
        return message;
    }

    /** @return user profile data from the server */
    public UserProfile getData() {
        return data;
    }
}
