package com.example.bookwormadventuresdeluxe2.NotificationUtility;

/**
 * Stores information to display in a notification
 */

public class Data
{
    private String Message;
    private String ReceiverUserId;

    public Data(String message, String receiverUserId)
    {
        Message = message;
        ReceiverUserId = receiverUserId;
    }

    public String getMessage()
    {
        return Message;
    }

    public void setMessage(String message)
    {
        Message = message;
    }

    public String getReceiverUserId()
    {
        return ReceiverUserId;
    }

    public void setReceiverUserId(String receiverUserId)
    {
        ReceiverUserId = receiverUserId;
    }
}
