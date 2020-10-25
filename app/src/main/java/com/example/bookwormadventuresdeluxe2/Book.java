package com.example.bookwormadventuresdeluxe2;

import com.example.bookwormadventuresdeluxe2.Utilities.Status;

import java.io.Serializable;

public class Book implements Serializable
{
    // Basic attributes for now, rest added as needed
    private String title;
    private String author;
    private String isbn;
    private String description;
    private Status status;

    public Book(String title, String author, String description, String isbn, Status status)
    {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.description = description;
        this.status = status;
    }

    public static boolean fieldsValid(String title, String author, String description, String isbn)
    {
        // TODO: implement this properly later
        return true;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public String getIsbn()
    {
        return isbn;
    }

    public void setIsbn(String isbn)
    {
        this.isbn = isbn;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }
}


