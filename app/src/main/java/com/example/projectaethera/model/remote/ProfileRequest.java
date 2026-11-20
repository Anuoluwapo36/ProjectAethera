package com.example.projectaethera.model.remote;

import com.example.projectaethera.model.entity.UserProfile;

/**
 * Request body sent when editing a user profile.
 * Extends UserProfile so fields like username and bio map directly to JSON.
 */
public class ProfileRequest extends UserProfile {

    /** Empty constructor for Gson deserialization. */
    public ProfileRequest() {
    }
}
