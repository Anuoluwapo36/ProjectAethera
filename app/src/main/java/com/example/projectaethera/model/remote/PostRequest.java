package com.example.projectaethera.model.remote;

import com.example.projectaethera.model.entity.Post;
import com.example.projectaethera.model.entity.UserProfile;

import java.sql.Timestamp;

/**
 * Post data model used when sending or receiving post info from the API.
 * Extends the base Post entity with Gson-friendly constructors.
 */
public class PostRequest extends Post {

    /**
     * @param postId      id of the post
     * @param userId      id of the post owner
     * @param caption     post caption text
     * @param createAt    when the post was created
     * @param userProfile basic user profile data
     */
    public PostRequest(int postId, int userId, String caption, Timestamp createAt, UserProfile userProfile) {
        super(postId, userId, caption, createAt, userProfile);
    }

    /** Empty constructor for Gson deserialization. */
    public PostRequest() {
    }


}
