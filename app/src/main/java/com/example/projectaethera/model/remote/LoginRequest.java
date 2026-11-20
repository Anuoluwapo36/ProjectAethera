package com.example.projectaethera.model.remote;

import com.example.projectaethera.config.AppConfig;
import com.example.projectaethera.model.entity.Users;

/**
 * Request body sent to POST /api/login.
 * Extends Users with email/password validation before the API call.
 */
public class LoginRequest extends Users {

    /**
     * @param username         username (optional for login)
     * @param email            login email
     * @param password         login password
     * @param reset_code       not used for login
     * @param rest_code_expiry not used for login
     */
    public LoginRequest(String username, String email, String password, String reset_code, long rest_code_expiry) {
        super(username, email, password, reset_code, rest_code_expiry);
    }

    /** Empty constructor for Gson. */
    public LoginRequest() {
    }

    /**
     * Checks email and password are filled and email format is valid.
     *
     * @return error message from AppConfig, or null if OK
     */
    @Override
    public String validateUser() {


        String emailData = getEmail();
        String  passwordData = getPassword();


        if(emailData == null || emailData.isEmpty()){
            return AppConfig.ERROR_EMPTY_EMAIL;
        }
        if(!emailData.matches(AppConfig.EMAIL_REGEX)){
            return AppConfig.ERROR_INVALID_EMAIL;
        }

        if (passwordData == null || passwordData.isEmpty()){
            return AppConfig.ERROR_EMPTY_PASSWORD;
        }

        return null;
    }
}
