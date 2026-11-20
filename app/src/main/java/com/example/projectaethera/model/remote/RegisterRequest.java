package com.example.projectaethera.model.remote;

import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.model.entity.Users;

/**
 * Request body sent to POST /api/register.
 * Fields: inherited username/email/password plus termsAccepted flag.
 */
public class RegisterRequest extends Users {

    /** Whether the user checked "I agree to terms". */
    private boolean termsAccepted;

    /**
     * @param username         chosen username
     * @param email            email address
     * @param password         password
     * @param reset_code       reset code (unused at register)
     * @param rest_code_expiry expiry (unused at register)
     * @param termsAccepted    terms checkbox state
     */
    public RegisterRequest(String username, String email, String password, String reset_code, long rest_code_expiry, boolean termsAccepted) {
        super(username, email, password, reset_code, rest_code_expiry);
        this.termsAccepted = termsAccepted;
    }

    /** Empty constructor for Gson. */
    public RegisterRequest() {
        super();
    }

    /**
     * Validates username, email, password, and terms before register API call.
     *
     * @return error message from AppConfig, or null if valid
     */
    @Override
    public String validateUser() {
        String usernameData = getUsername();
        String emailData = getEmail();
        String passwordData = getPassword();

        if(usernameData == null || usernameData.trim().isEmpty()){
            return AppConfig.ERROR_EMPTY_USERNAME;
        }


        if(emailData == null || emailData.trim().isEmpty()){
            return AppConfig.ERROR_EMPTY_EMAIL;
        }
        if(!emailData.matches(AppConfig.EMAIL_REGEX)){
            return AppConfig.ERROR_INVALID_EMAIL;
        }

        if(passwordData == null || passwordData.trim().isEmpty()){
            return AppConfig.ERROR_EMPTY_PASSWORD;

        }

        if(!isTermsAccepted()){
            return  AppConfig.ERROR_UNCHECK_TERMS_CONDITION;
        }

        return null;
    }

    /**
     * @return true if user accepted terms and conditions
     */
    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    /**
     * @param termsAccepted new terms accepted flag
     */
    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }
}
