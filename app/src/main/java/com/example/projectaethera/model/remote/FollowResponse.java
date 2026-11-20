package com.example.projectaethera.model.remote;

/**
 * JSON response from the follow/unfollow toggle API.
 * Tells whether the action worked and what happened (followed or unfollowed).
 */
public class FollowResponse {
    private boolean success;
    private String message;
    private String action;

    /** @return true if the follow toggle succeeded */
    public boolean isSuccess() {
        return success;
    }

    /** @return server message about the result */
    public String getMessage() {
        return message;
    }

    /** @return action taken, such as "followed" or "unfollowed" */
    public String getAction() {
        return action;
    }
}
