package com.example.projectaethera.model.remote;

/**
 * JSON response from the add-comment API.
 * Tells whether the comment was saved successfully.
 */
public class CommentResponse {

    private boolean success;
    private String message;

    /** @return true if the comment was added successfully */
    public boolean isSuccess() {
        return success;
    }

    /** @return server message about the result */
    public String getMessage() {
        return message;
    }
}
