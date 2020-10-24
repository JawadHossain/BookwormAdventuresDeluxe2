package com.example.bookwormadventuresdeluxe2.Utilities;

import android.app.Application;

/**
 * Global Api to access user credentials
 */
public class UserCredentialAPI extends Application
{
    private static UserCredentialAPI instance;
    private String username;
    private String userId;

    public static UserCredentialAPI getInstance()
    {
        if (instance == null)
        {
            instance = new UserCredentialAPI();
        }
        return instance;
    }

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
