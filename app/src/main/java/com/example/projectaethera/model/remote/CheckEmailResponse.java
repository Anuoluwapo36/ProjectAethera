package com.example.projectaethera.model.remote;

/**
 * JSON response from POST /api/checkEmail.
 * Fields: success, message.
 */
public class CheckEmailResponse {

    /** True if the check completed (email may or may not exist). */
    private boolean success;
    /** Explains result (e.g. email found or available). */
    private String message;

    /**
     * @return true if the API call succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return server message about the email
     */
    public String getMessage() {
        return message;
    }
}
