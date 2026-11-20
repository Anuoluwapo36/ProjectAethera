package com.example.projectaethera.model.remote;



/**

 * JSON response from status action APIs such as create, like, unlike,

 * mark viewed, and delete. May include an updated like count.

 */

public class StatusResponse {

    private boolean success;

    private String message;

    private int likeCount;



    /** @return true if the status action succeeded */

    public boolean isSuccess() {

        return success;

    }



    /** @return server message about the result */

    public String getMessage() {

        return message;

    }



    /** @return updated like count after like/unlike actions */

    public int getLikeCount() {

        return likeCount;

    }

}

