package com.example.projectaethera.model.remote;

import com.example.projectaethera.model.entity.Post;

/**
 * JSON response from post-related APIs such as upload, details, like, and delete.
 * Wraps a single Post object when the server returns full post data.
 */
public class PostResponse {
    private boolean success;
    private String message;

    private Post data;

    /** @return true if the post action succeeded */
    public boolean isSuccess() {
        return success;
    }

    /** @return server message about the result */
    public String getMessage() {
        return message;
    }

    /** @return post data returned by the server */
    public Post getData() {
        return data;
    }
}
