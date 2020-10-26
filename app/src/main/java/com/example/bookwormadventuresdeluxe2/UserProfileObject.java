package com.example.bookwormadventuresdeluxe2;

/**
 * UserObject is required to display information pulled from Firebase
 */
public class UserProfileObject
{
    private String username;
    private String email;
    private String phoneNumber;
    private String userId;
    private String documentId;

    public UserProfileObject()
    {

    }

    /**
     * Default constructor for UserObject
     * @param username username pulled from database
     * @param email email pulled from database
     * @param userId userId pulled from database, unique to FirebaseAuth account
     * @param documentId documentId pulled from database, used to target object
     */
    public UserProfileObject(String username, String email, String userId, String documentId)
    {
        this.username = username;
        this.email = email;
        this.userId = userId;
        this.documentId = documentId;
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
}
