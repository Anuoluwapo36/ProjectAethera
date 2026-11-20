package com.example.projectaethera.model.remote;

import com.example.projectaethera.model.entity.Post;
import com.example.projectaethera.model.entity.UserProfile;

import java.sql.Timestamp;

/**
 * One post item in the home feed response.
 * Extends Post with username and profile image fields from the API.
 */
public class HomeFeedRequest extends Post {

    private String username;
    private String fullname;
    private String profile_image;

    /**
     * Builds a UserProfile from the feed-specific username and image fields.
     *
     * @return user profile for display in the feed
     */
    @Override
    public UserProfile getUserProfile() {
        UserProfile profile = new UserProfile();
        profile.setUsername(username);
        profile.setFullname(fullname);
        profile.setImage_uri(profile_image);
        return profile;
    }

    /**
     * @param postId      id of the post
     * @param userId      id of the post owner
     * @param caption     post caption text
     * @param createAt    when the post was created
     * @param userProfile basic user profile data
     */
    public HomeFeedRequest(int postId, int userId, String caption, Timestamp createAt, UserProfile userProfile) {
        super(postId, userId, caption, createAt, userProfile);
    }
}
