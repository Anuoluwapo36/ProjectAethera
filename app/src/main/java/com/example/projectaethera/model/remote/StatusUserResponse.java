package com.example.projectaethera.model.remote;



import java.util.List;



/**

 * JSON response from the user statuses API.

 * Returns one user's profile info and their list of status items.

 */

public class StatusUserResponse {

    private boolean success;

    private String message;

    private StatusUserData data;



    /** @return true if the request succeeded */

    public boolean isSuccess() {

        return success;

    }



    /** @return server message about the result */

    public String getMessage() {

        return message;

    }



    /** @return user info and their statuses */

    public StatusUserData getData() {

        return data;

    }



    /**

     * Nested data object with user details and their status list.

     */

    public static class StatusUserData {

        private int userId;

        private String username;

        private String profileImage;

        private int isOwn;

        private List<StatusItem> statuses;



        /** @return id of the user who owns these statuses */

        public int getUserId() {

            return userId;

        }



        /** @return username of the status owner */

        public String getUsername() {

            return username;

        }



        /** @return profile image URL of the status owner */

        public String getProfileImage() {

            return profileImage;

        }



        /** @return 1 if these are the logged-in user's statuses, 0 if not */

        public int getIsOwn() {

            return isOwn;

        }



        /** @return list of status items for this user */

        public List<StatusItem> getStatuses() {

            return statuses;

        }

    }

}

