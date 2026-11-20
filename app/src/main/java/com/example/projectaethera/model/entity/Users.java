package com.example.projectaethera.model.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Base class for user account data used in login and register flows.
 * Fields: username, email, password, reset_code, rest_code_expiry.
 * Subclasses implement validateUser() for form checks before API calls.
 */
public abstract class Users {
    /** User's chosen display/login name. */
    @SerializedName("username")
    protected String username;
    /** Email address used to sign in. */
    protected String email;
    /** Plain password sent to the server (hashed on backend). */
    protected String password;

    /** Code sent by email for password reset. */
    protected String reset_code;
    /** When the reset code expires (timestamp). */
    protected  long rest_code_expiry;

    /**
     * Creates a user with all account fields filled in.
     *
     * @param username         display name
     * @param email            email address
     * @param password         password
     * @param reset_code       password reset code
     * @param rest_code_expiry expiry time for reset code
     */
    public Users(String username,String email, String password, String reset_code , long rest_code_expiry) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.reset_code = reset_code;
        this.rest_code_expiry = rest_code_expiry;

    }

    /** Empty constructor for Gson / Retrofit. */
    public Users() {

    }

    /**
     * @return username
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
     * @return email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email new email
     */
    public void setEmail(String email) {
        this.email = email;

    }

    /**
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return password reset verification code
     */
    public String getReset_code() {
        return reset_code;
    }

    /**
     * @param reset_code new reset code
     */
    public void setReset_code(String reset_code) {
        this.reset_code = reset_code;
    }

    /**
     * @return when the reset code expires
     */
    public long getRest_code_expiry() {
        return rest_code_expiry;
    }

    /**
     * @param rest_code_expiry new expiry timestamp
     */
    public void setRest_code_expiry(long rest_code_expiry) {
        this.rest_code_expiry = rest_code_expiry;
    }

    /**
     * Validates form fields before sending to the API.
     *
     * @return error message string, or null if everything is valid
     */
    public abstract String validateUser();
}
