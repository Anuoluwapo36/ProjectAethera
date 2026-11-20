package com.example.projectaethera.model.remote;

import com.example.projectaethera.model.entity.Comment;
import com.example.projectaethera.model.entity.UserProfile;

import java.util.List;

/**
 * JSON response from the get-comments API.
 * Returns the full comment list for a post.
 */
public class GetCommentResponse {
    private boolean success;
    private String message;

    private List<Comment> comments;

    /** @return true if comments loaded successfully */
    public boolean isSuccess() {
        return success;
    }

    /** @return server message about the result */
    public String getMessage() {
        return message;
    }

    /** @return list of comments on the post */
    public List<Comment> getComments() {
        return comments;
    }

}
