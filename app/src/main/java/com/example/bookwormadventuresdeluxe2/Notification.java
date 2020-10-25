package com.example.bookwormadventuresdeluxe2;

import android.widget.ImageView;

import com.example.bookwormadventuresdeluxe2.Utilities.Status;

public class Notification
{
    private Book book;
    private String message;

    public Notification(Book book, String message)
    {
        this.book = book;
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public String getTitle() {
        return this.book.getTitle();
    }

    public String getAuthor() {
        return this.book.getAuthor();
    }

    public String getIsbn() {
        return this.book.getIsbn();
    }

    public Book getBook() {
        return this.book;
    }

    // Might have to change later
    public Status getStatus() {
        return this.book.getStatus();
    }

    public void setStatusCircleColor(Status status, ImageView statusCircle) {
        this.book.setStatusCircleColor(status, statusCircle);
    }
}

