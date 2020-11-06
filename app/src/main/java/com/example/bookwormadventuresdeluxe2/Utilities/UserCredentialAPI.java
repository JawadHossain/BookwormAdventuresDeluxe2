package com.example.bookwormadventuresdeluxe2.Utilities;

/**
 * Global API to access user credentials
 */

import android.app.Application;

public class UserCredentialAPI extends Application
{
    private static UserCredentialAPI instance;
    private String username;
    private String userId;

    /**
     * Get the instance of the UserCredentialAPI
     *
     * @return the instance of the UserCredentialAPI
     */
    public static UserCredentialAPI getInstance()
    {
        if (instance == null)
        {
            instance = new UserCredentialAPI();
        }
        return instance;
    }

    /**
     * Required empty constructor
     */
    public UserCredentialAPI()
    {
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

}
