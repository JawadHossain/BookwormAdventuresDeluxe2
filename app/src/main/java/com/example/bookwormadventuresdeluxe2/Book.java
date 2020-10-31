package com.example.bookwormadventuresdeluxe2;

import android.graphics.PorterDuff;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

import com.example.bookwormadventuresdeluxe2.Utilities.Status;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Book implements Serializable
{
    // Basic attributes for now, rest added as needed
    private String owner;
    private String title;
    private String author;
    private String isbn;
    private String description;
    private Status status;
    private String pickUpAddress;
    private ArrayList<String> requesters;
    private String borrower;

    // BookListAdapter which is now a FirestoreRecyclerAdapter requires empty constructor
    public Book()
    {

    }

    public Book(String owner, String title, String author, String description, String isbn, Status status)
    {
        this.owner = owner;
        this.title = title;
        this.author = author;
        this.description = description;
        this.isbn = isbn;
        this.description = description;
        this.status = status;
    }

    public String getOwner()
    {
        return owner;
    }

    public void setOwner(String owner)
    {
        this.owner = owner;
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

    public String getPickUpAddress()
    {
        return pickUpAddress;
    }

    public void setPickUpAddress(String pickUpAddress)
    {
        this.pickUpAddress = pickUpAddress;
    }

    public ArrayList<String> getRequesters()
    {
        return requesters;
    }

    public void setRequesters(ArrayList<String> requesters)
    {
        this.requesters = requesters;
    }

    public void addRequester(String requester)
    {
        this.requesters.add(requester);
    }

    public void deleteRequester(String requester)
    {
        this.requesters.remove(requester);
    }

    public String getBorrower()
    {
        return borrower;
    }

    public void setBorrower(String borrower)
    {
        this.borrower = borrower;
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

    public Notification createNotification(String message)
    {
        return new Notification(this, message);
    }
}


