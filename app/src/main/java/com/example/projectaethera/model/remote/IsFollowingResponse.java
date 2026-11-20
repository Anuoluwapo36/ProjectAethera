package com.example.projectaethera.model.remote;



import com.google.gson.annotations.SerializedName;



/**

 * JSON response from the is-following check API.

 * Tells whether one user already follows another.

 */

public class IsFollowingResponse {

    @SerializedName("following")

    private boolean following;



    /** @return true if the follower is already following the other user */

    public boolean isFollowing() {

        return following;

    }

}

