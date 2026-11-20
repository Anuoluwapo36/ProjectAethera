package com.example.projectaethera.model.remote;

/**
 * JSON response from POST /api/register.
 * Fields: success, message, user (nested object with id).
 */
public class RegisterResponse {
    /** True if account was created. */
    private boolean success;
    /** Server message. */
    private String message;
    /** Nested user object from the server. */
    private RegisteredUser user;

    /**
     * @return true if registration succeeded
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return server message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return new user's id, or 0 if user object is missing
     */
    public int getUserId() {
        return user != null ? user.getId() : 0;
    }

    /** Inner class matching the user object in the register JSON. */
    private static class RegisteredUser {
        /** New account id. */
        private int id;

        /**
         * @return registered user id
         */
        public int getId() {
            return id;
        }
    }
}
