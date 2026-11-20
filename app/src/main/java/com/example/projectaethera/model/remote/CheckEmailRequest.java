package com.example.projectaethera.model.remote;

import com.example.projectaethera.model.entity.Users;

/**
 * Request body for POST /api/checkEmail — asks if an email is already taken.
 * Fields: email.
 */
public class CheckEmailRequest  {
    /** Email address to look up on the server. */
    private String email;

    /**
     * @param email email to check
     */
    public CheckEmailRequest( String email) {
        this.email = email;

    }

    /** Empty constructor for Gson. */
    public CheckEmailRequest() {

    }

    /**
     * @return email being checked
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email new email value
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
