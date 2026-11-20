package com.example.projectaethera.model.entity;



import com.example.projectaethera.model.entity.UserProfile;

import com.google.gson.annotations.SerializedName;



/**

 * Holds public profile info for a user shown on posts, search, and profile screens.

 * Fields: userId, username, fullname, bio, profession, image_uri, isFollowing.

 */

public class UserProfile {

    /** Unique id of this user on the server. */

    private int userId;

    /** Display handle / login name. */

    private String username;

    /** User's real or display full name. */

    private String fullname;

    /** Short about text on the profile. */

    private String bio;

    /** Job or role text on the profile. */

    private String profession;



    /** URL or path to the profile picture (JSON may use image_uri or imageUri). */

    @SerializedName(value = "image_uri", alternate = {"imageUri"})

    private String image_uri;

    /** 1 if the current viewer follows this user, 0 if not. */

    private int isFollowing;



    /**

     * @return this user's id

     */

    public int getUserId() {

        return userId;

    }



    /**

     * @return the username

     */

    public String getUsername() {

        return username;

    }



    /**

     * @param username new username

     */

    public void setUsername(String username) {

        this.username = username;

    }



    /**

     * @return full name shown on profile

     */

    public String getFullname() {

        return fullname;

    }



    /**

     * @param fullname new full name

     */

    public void setFullname(String fullname) {

        this.fullname = fullname;

    }



    /**

     * @return profile bio text

     */

    public String getBio() {

        return bio;

    }



    /**

     * @param bio new bio text

     */

    public void setBio(String bio) {

        this.bio = bio;

    }



    /**

     * @return profession or job title

     */

    public String getProfession() {

        return profession;

    }



    /**

     * @param profession new profession text

     */

    public void setProfession(String profession) {

        this.profession = profession;

    }



    /**

     * @return profile image URI

     */

    public String getImage_uri() {

        return image_uri;

    }



    /**

     * @param image_uri new profile image URI

     */

    public void setImage_uri(String image_uri) {

        this.image_uri = image_uri;

    }



    /**

     * @param userId new user id

     */

    public void setUserId(int userId) {

        this.userId = userId;

    }



    /**

     * @return 1 if followed by viewer, 0 otherwise

     */

    public int getIsFollowing() {

        return isFollowing;

    }



    /**

     * @param isFollowing follow flag (0 or 1)

     */

    public void setIsFollowing(int isFollowing) {

        this.isFollowing = isFollowing;

    }

}

