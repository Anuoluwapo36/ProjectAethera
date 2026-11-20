package com.example.projectaethera.model.remote;

/**
 * JSON response from the comment count API.
 * Returns the total number of comments on a post.
 */
public class CommentCountResponse {

    private boolean success;
    private int total;

    /** @return true if the count loaded successfully */
    public boolean isSuccess() { return success; }

    /** @return total number of comments on the post */
    public int getTotal() { return total; }
}
