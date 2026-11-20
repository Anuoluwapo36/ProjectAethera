package com.example.projectaethera.model.remote;

/**
 * JSON response from POST /api/login.
 * Fields: success, message, userId.
 */
public class LoginResponse {
    /** True if login worked. */
    private boolean success;
    /** Server message (error or success text). */
    private  String message;
    /** Id of the logged-in user. */
    private int userId;

    /**
     * @return true if login succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return server message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return logged-in user's id
     */
    public int getUserId() {
        return userId;
    }
}
