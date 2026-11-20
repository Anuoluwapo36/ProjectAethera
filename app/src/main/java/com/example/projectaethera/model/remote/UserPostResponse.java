package com.example.projectaethera.model.remote;

import com.example.projectaethera.model.entity.Post;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import kotlinx.serialization.Serializable;

/**
 * JSON response from the get-user-posts API.
 * Returns all posts created by one user.
 */
public class UserPostResponse {
    private boolean success;
    private String message;
    private List<Post> data;

    /** @return true if the posts loaded successfully */
    public boolean isSuccess() {
        return success;
    }

    /** @return server message about the result */
    public String getMessage() {
        return message;
    }

    /** @return list of posts for the requested user */
    public List<Post> getData() {
        return data;
    }


}
