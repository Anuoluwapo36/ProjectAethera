package com.example.projectaethera.model.remote;

/**
 * JSON response from POST /api/verifyCode.
 * Fields: success, message, userId (set when code is valid).
 */
public class VerifyCodeResponse {
    /** True if the code matched. */
    private boolean success;
    /** Server message. */
    private String message;
    /** User id to use for the next reset-password step. */
    private int userId;

    /**
     * @return true if code verification passed
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
     * @return user id linked to this reset flow
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId user id from server
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
