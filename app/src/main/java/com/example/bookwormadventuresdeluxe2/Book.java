package com.example.bookwormadventuresdeluxe2;

import android.graphics.PorterDuff;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

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

    // BookListAdapter which is now a FirestoreRecyclerAdapter requires empty constructor
    public Book()
    {

    }

    public Book(String title, String author, String description, String isbn, Status status)
    {
        this.title = title;
        this.author = author;
        this.description = description;
        this.isbn = isbn;
        this.description = description;
        this.status = status;
    }

    /**
     * Returns true if the attributes provided is valid for an instance of a book object
     *
     * @param title
     * @param author
     * @param description
     * @param isbn
     * @return
     */
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

    /**
     * Sets the color of an image view based on the given status
     *
     * @param status       The status of the item displaying the imageView
     * @param statusCircle The reference to the imageView to re-color
     */
    public void setStatusCircleColor(Status status, ImageView statusCircle)
    {
        switch (status)
        {
            case Available:
                statusCircle.getDrawable().setColorFilter(ResourcesCompat.getColor(GlobalApplication.getAppContext().getResources(), R.color.available, null), PorterDuff.Mode.SRC_ATOP);
                break;
            case Borrowed:
                statusCircle.getDrawable().setColorFilter(ResourcesCompat.getColor(GlobalApplication.getAppContext().getResources(), R.color.borrowed, null), PorterDuff.Mode.SRC_ATOP);
                break;
            case Requested:
                statusCircle.getDrawable().setColorFilter(ResourcesCompat.getColor(GlobalApplication.getAppContext().getResources(), R.color.requested, null), PorterDuff.Mode.SRC_ATOP);
                break;
            case Accepted:
                statusCircle.getDrawable().setColorFilter(ResourcesCompat.getColor(GlobalApplication.getAppContext().getResources(), R.color.accepted, null), PorterDuff.Mode.SRC_ATOP);
                break;
            default:
                /* We would not expect any other id */
                throw new IllegalArgumentException();
        }
    }
}


