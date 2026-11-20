package com.example.projectaethera.model.remote;



import java.util.List;



/**

 * Groups one user's statuses together in the status feed.

 * Used to show each person's story ring on the home screen.

 */

public class StatusGroup {

    private int userId;

    private String username;

    private String profileImage;

    private int isOwn;

    private int hasUnseen;

    private List<StatusItem> statuses;



    /** @return id of the user in this group */

    public int getUserId() {

        return userId;

    }



    /** @return username shown under the status ring */

    public String getUsername() {

        return username;

    }



    /** @return profile image URL for the status ring */

    public String getProfileImage() {

        return profileImage;

    }



    /** @return 1 if this group is the logged-in user's own statuses */

    public int getIsOwn() {

        return isOwn;

    }



    /** @return 1 if there are unseen statuses in this group */

    public int getHasUnseen() {

        return hasUnseen;

    }



    /** @return list of status items for this user */

    public List<StatusItem> getStatuses() {

        return statuses;

    }

}

