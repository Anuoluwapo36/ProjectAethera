package com.example.projectaethera.model.remote;

import com.example.projectaethera.model.entity.Users;

/**
 * Request body for POST /api/verifyCode during password reset.
 * Fields: code — the verification code from the user's email.
 */
public class VerifyCodeRequest {

    /** The reset code the user typed in. */
    private String code;

    /**
     * @param code verification code from email
     */
    public VerifyCodeRequest(String code) {
        this.code = code;
    }

    /** Empty constructor for Gson. */
    public VerifyCodeRequest( ) {

    }

    /**
     * @return the verification code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code new code value
     */
    public void setCode(String code) {
        this.code = code;
    }
}
