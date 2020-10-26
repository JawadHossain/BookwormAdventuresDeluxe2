package com.example.bookwormadventuresdeluxe2;

import android.widget.ImageView;

import com.example.bookwormadventuresdeluxe2.Utilities.Status;

public class Notification extends Book
{
    private String message;

    public Notification(String title, String author, String description, String isbn, Status status, String message)
    {
        super(title, author, description, isbn, status);
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }
}

