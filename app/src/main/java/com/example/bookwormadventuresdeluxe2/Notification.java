package com.example.bookwormadventuresdeluxe2;

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

    public Book getBook()
    {
        return this.book;
    }

}

