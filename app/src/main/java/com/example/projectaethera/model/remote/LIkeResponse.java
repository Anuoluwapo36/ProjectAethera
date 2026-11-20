package com.example.projectaethera.model.remote;

/**
 * JSON response from like and unlike post APIs.
 * Tells whether the like action worked.
 */
public class LIkeResponse {
    private boolean success;
    private String message;

    /** @return true if the like action succeeded */
    public boolean isSuccess() {
        return success;
    }

    /** @return server message about the result */
    public String getMessage() {
        return message;
    }
}
