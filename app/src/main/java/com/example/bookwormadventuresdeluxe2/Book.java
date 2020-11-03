package com.example.bookwormadventuresdeluxe2;

/**
 * Book holds all of the relevant information pertaining to a book in the library. It has
 * a set of private fields which define its attributes along with corresponding getters
 * and setters to retrieve and manipulate the information.
 */

import android.graphics.PorterDuff;
import android.util.Log;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

import com.example.bookwormadventuresdeluxe2.Utilities.DownloadImageTask;
import com.example.bookwormadventuresdeluxe2.Utilities.Status;
import com.google.firebase.firestore.auth.User;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Book holds all of the relevant information pertaining to a book in the library. It has
 * a set of private fields which define its attributes along with corresponding getters
 * and setters to retrieve and manipulate the information.
 */
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
    private String imageUrl = "";

    // BookListAdapter which is now a FirestoreRecyclerAdapter requires empty constructor
    public Book()
    {

    }

    public Book(String owner, String title, String author, String description, String isbn, Status status, String imageUrl)
    {
        this.owner = owner;
        this.title = title;
        this.author = author;
        this.description = description;
        this.isbn = isbn;
        this.description = description;
        this.status = status;
        this.requesters = new ArrayList<String>();
        this.imageUrl = imageUrl;
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

    public String getImageUrl()
    {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl)
    {
        this.imageUrl = imageUrl;
    }

    /**
     * Sets the color of an image view based on the given status
     *
     * @param statusCircle The reference to the imageView to re-color
     * @param user         The user to find the status for
     */
    public void setStatusCircleColor(ImageView statusCircle, String user)
    {
        switch (getAugmentStatus(user))
        {
            case Available:
                statusCircle.getDrawable().setColorFilter(ResourcesCompat.getColor(
                        GlobalApplication.getAppContext().getResources(),
                        R.color.available, null), PorterDuff.Mode.SRC_ATOP);
                break;
            case Requested:
                statusCircle.getDrawable().setColorFilter(ResourcesCompat.getColor(
                        GlobalApplication.getAppContext().getResources(),
                        R.color.requested, null), PorterDuff.Mode.SRC_ATOP);
                break;
            case Accepted:
                statusCircle.getDrawable().setColorFilter(ResourcesCompat.getColor(
                        GlobalApplication.getAppContext().getResources(),
                        R.color.accepted, null), PorterDuff.Mode.SRC_ATOP);
                break;
            case Borrowed:
                statusCircle.getDrawable().setColorFilter(ResourcesCompat.getColor(
                        GlobalApplication.getAppContext().getResources(),
                        R.color.borrowed, null), PorterDuff.Mode.SRC_ATOP);
                break;
            default:
                /* you broke getAugmentStatus() */
                throw new InvalidParameterException("Unknown status passed to Book.setCircleStatus()");
        }
    }

    /**
     * Returns the status of the book the given user should see
     *
     * @param user The string of the user looking at the book
     */
    public Status getAugmentStatus(String user)
    {
        switch (status)
        {
            case Available:
            case Requested:
                if (this.requesters != null && (this.requesters.contains(user) || user.equals(this.owner) && this.requesters.size() > 0))
                {
                    return Status.Requested;
                }
                else
                {
                    return Status.Available;
                }
            case Accepted:
                return Status.Accepted;
            case bPending:
                if (user.equals(this.owner))
                {
                    return Status.Borrowed;
                }
                else
                {
                    return Status.Accepted;
                }
            case Borrowed:
                return Status.Borrowed;
            case rPending:
                if (user.equals(this.owner))
                {
                    return Status.Borrowed;
                }
                else
                {
                    return Status.Available;
                }
            default:
                /* We would not expect any other id */
                throw new InvalidParameterException("Unknown status passed to Book.getAugmentStatus()");
        }
    }

    /**
     * Returns a notification with this book and the given message
     *
     * @param message The string to be displayed on notification
     */
    public Notification createNotification(String message)
    {
        return new Notification(this, message);
    }

    /**
     * Sets the photo corresponding to the given imageView with the image url of the given book
     *
     * @param book
     * @param imageView
     */
    public void setPhoto(Book book, ImageView imageView)
    {
        if (book.getImageUrl().compareTo("") == 0)
        {
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageDrawable(ResourcesCompat.getDrawable(GlobalApplication.getAppContext().getResources(), R.drawable.ic_camera, null));
            return;
        }
        new DownloadImageTask(imageView).execute(book.getImageUrl());
    }
}


