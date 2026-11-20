package com.example.projectaethera.model.remote;

import com.example.projectaethera.model.entity.Post;

import java.util.List;

/**
 * JSON response from the home feed API.
 * Contains a list of posts from users the current user follows.
 */
public class HomeFeedResponse {

    private boolean success;
    private  String message;
    private List<HomeFeedRequest> data;

    /** @return true if the feed loaded successfully */
    public boolean isSuccess() {
        return success;
    }

    /** @return server message about the result */
    public String getMessage() {
        return message;
    }

    /** @return list of feed posts with user info */
    public List<HomeFeedRequest> getData() {
        return data;
    }
}
