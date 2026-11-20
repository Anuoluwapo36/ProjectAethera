package com.example.projectaethera.model.remote;

/**
 * JSON response from POST /api/resetPassword (and similar password change endpoints).
 * Fields: success, message.
 */
public class ChangePasswordResponse {
    /** True if password was updated. */
    private boolean success;
    /** Server message. */
    private String message;

    /**
     * @return true if password change succeeded
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
}
