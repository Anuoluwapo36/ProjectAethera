package com.example.projectaethera.model.remote;



/**

 * One status (story) item returned from the status feed or user status APIs.

 * Holds media info, like/view flags, and basic user details.

 */

public class StatusItem {    private int statusId;

    private int userId;

    private String mediaUri;

    private String mediaType;

    private String caption;

    private String createdAt;

    private String expiresAt;

    private int likeCount;

    private int isLiked;

    private int hasViewed;

    private String username;

    private String profileImage;

    private int isOwn;



    /** @return id of this status */

    public int getStatusId() {

        return statusId;

    }



    /** @return id of the user who posted this status */

    public int getUserId() {

        return userId;

    }



    /** @return URL or path to the status media file */

    public String getMediaUri() {

        return mediaUri;

    }



    /** @return caption text for the status */

    public String getCaption() {

        return caption;

    }



    /** @return when the status was created */

    public String getCreatedAt() {

        return createdAt;

    }



    /** @return number of likes on this status */

    public int getLikeCount() {

        return likeCount;

    }



    /** @return 1 if the current user liked it, 0 if not */

    public int getIsLiked() {

        return isLiked;

    }



    /** @return 1 if the current user viewed it, 0 if not */

    public int getHasViewed() {

        return hasViewed;

    }



    /** @return username of the status owner */

    public String getUsername() {

        return username;

    }



    /** @return profile image URL of the status owner */

    public String getProfileImage() {

        return profileImage;

    }



    /** @return 1 if this status belongs to the logged-in user, 0 if not */

    public int getIsOwn() {

        return isOwn;

    }



    /**

     * Updates whether the current user liked this status.

     *

     * @param isLiked 1 for liked, 0 for not liked

     */

    public void setIsLiked(int isLiked) {

        this.isLiked = isLiked;

    }



    /**

     * Updates the like count shown on this status.

     *

     * @param likeCount new like count

     */

    public void setLikeCount(int likeCount) {

        this.likeCount = likeCount;

    }

}

