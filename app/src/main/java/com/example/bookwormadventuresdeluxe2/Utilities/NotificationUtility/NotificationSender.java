package com.example.bookwormadventuresdeluxe2.Utilities.NotificationUtility;

/**
 * Stores notification data and receiver as expected by FCM post request api
 */

public class NotificationSender
{
    public Data data;
    public String to;

    /**
     * Constructor to create NotificationSender
     *
     * @param data The notification's information
     * @param to   The receiver: FCM token Or Group token for multi-device notification
     */
    public NotificationSender(Data data, String to)
    {
        this.data = data;
        this.to = to;
    }

    public NotificationSender()
    {
    }
}
