package com.example.bookwormadventuresdeluxe2;

/**
 * Class responsible for modelling a notification object.
 */

public class Notification
{
    private Book book;
    private String message;

    /**
     * Constructor for an object representing a notification
     *
     * @param book    The books that is associated with the notification
     * @param message The notification message
     */
    public Notification(Book book, String message)
    {
        this.book = book;
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public Book getBook()
    {
        return this.book;
    }

}

