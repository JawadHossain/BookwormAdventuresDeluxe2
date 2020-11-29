package com.example.bookwormadventuresdeluxe2.Models;

/**
 * User models a user and stores the user details pulled from firestore.
 */

import java.io.Serializable;

public class User implements Serializable
{
    private String username;
    private String email;
    private String phoneNumber;
    private String userId;
    private String documentId;
    private String FCMtoken;

    public User()
    {
    }

    /**
     * Default constructor for User
     *
     * @param username    username pulled from database
     * @param email       email pulled from database
     * @param phoneNumber phone number pulled from database
     * @param userId      userId pulled from database, unique to FirebaseAuth account
     * @param documentId  documentId pulled from database, used to target object
     */
    public User(String username, String email, String phoneNumber, String userId, String documentId, String FCMtoken)
    {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userId = userId;
        this.documentId = documentId;
        this.FCMtoken = FCMtoken;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPhoneNumber()
    {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber)
    {
        this.phoneNumber = phoneNumber;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getDocumentId()
    {
        return documentId;
    }

    public void setDocumentId(String documentId)
    {
        this.documentId = documentId;
    }

    public String getFCMtoken()
    {
        return FCMtoken;
    }

    public void setFCMtoken(String FCMtoken)
    {
        this.FCMtoken = FCMtoken;
    }
}