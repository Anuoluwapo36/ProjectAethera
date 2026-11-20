package com.example.projectaethera.model.remote;

/**
 * Request body for POST /api/resetPassword.
 * Fields: userId, newPassword, confirmPassword.
 */
public class PasswordResetRequest {
    /** User who is resetting their password. */
    private int userId;
    /** The new password they chose. */
    private String newPassword;
    /** Same password typed again for confirmation. */
    private String confirmPassword;

    /**
     * @param userId          user id from verify-code step
     * @param newPassword     new password
     * @param confirmPassword confirmation field
     */
    public PasswordResetRequest(int userId, String newPassword, String confirmPassword) {
        this.userId = userId;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }

    /** Empty constructor for Gson. */
    public PasswordResetRequest() {

    }

    /**
     * @return user id
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId new user id
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * @return new password
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * @param newPassword new password value
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * @return confirm password field
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * @param confirmPassword confirm password value
     */
    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
