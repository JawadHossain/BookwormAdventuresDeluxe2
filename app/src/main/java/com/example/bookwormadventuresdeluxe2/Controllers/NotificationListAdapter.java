package com.example.bookwormadventuresdeluxe2.Controllers;

/**
 * NotificationListAdapter is a FirestoreRecycler data which acts as middleware between the notifications
 * on Firestore and the UI that displays them by providing view updaters and onClickListeners
 * for items in the RecyclerView.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookwormadventuresdeluxe2.Models.Book;
import com.example.bookwormadventuresdeluxe2.Models.Notification;
import com.example.bookwormadventuresdeluxe2.R;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;

import java.util.ArrayList;

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationListViewHolder>
{
    private ArrayList<Notification> notifications;
    private Context context;
    public NotificationListAdapter.NotificationListViewHolder NotificationListViewHolder;

    // Reference to the views for each item
    public static class NotificationListViewHolder extends RecyclerView.ViewHolder
    {
        public TextView message;
        public TextView title;
        public TextView author;
        public TextView isbn;
        public ImageView statusCircle;
        public ImageView bookPhoto;

        /**
         * A viewHolder for notifications
         *
         * @param notificationItemLayout The layout to reference
         */
        public NotificationListViewHolder(ConstraintLayout notificationItemLayout)
        {
            super(notificationItemLayout);
            this.message = (TextView) notificationItemLayout.getViewById(R.id.notification_message);
            this.title = (TextView) (notificationItemLayout.getViewById(R.id.book_item)).findViewById(R.id.book_item_title);
            this.author = (TextView) (notificationItemLayout.getViewById(R.id.book_item)).findViewById(R.id.book_item_author);
            this.isbn = (TextView) (notificationItemLayout.getViewById(R.id.book_item)).findViewById(R.id.book_item_isbn);
            this.statusCircle = (ImageView) (notificationItemLayout.getViewById(R.id.book_item)).findViewById(R.id.book_item_status);
            this.bookPhoto = (ImageView) (notificationItemLayout.getViewById(R.id.book_item)).findViewById(R.id.book_item_image);
        }
    }

    /**
     * A list adapter for the notifications
     *
     * @param notifications the list of notifications
     * @param context the context
     */
    public NotificationListAdapter(ArrayList<Notification> notifications, Context context)
    {
        this.notifications = notifications;
        this.context = context;
    }

    /**
     * method to get the ViewHolder for the notification list adapter
     * @param parent used to get the context
     * @param viewType unused
     * @return the ViewHolder
     */
    public NotificationListAdapter.NotificationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ConstraintLayout notificationItem = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        NotificationListAdapter.NotificationListViewHolder NotificationListViewHolder = new NotificationListAdapter.NotificationListViewHolder((notificationItem));
        this.NotificationListViewHolder = NotificationListViewHolder;
        return NotificationListViewHolder;
    }

    /**
     * method to return the ViewHolder for this list adapter
     * @return the ViewHolder
     */
    public NotificationListAdapter.NotificationListViewHolder getViewHolder()
    {
        return this.NotificationListViewHolder;
    }

    /**
     * method to fill out the notifications
     * @param NotificationListViewHolder the ViewHolder for the notification list
     * @param position the position of the notification in the list
     */
    public void onBindViewHolder(NotificationListAdapter.NotificationListViewHolder NotificationListViewHolder, int position)
    {
        NotificationListViewHolder.message.setText(notifications.get(position).getMessage());
        NotificationListViewHolder.title.setText(notifications.get(position).getBook().getTitle());
        NotificationListViewHolder.author.setText(notifications.get(position).getBook().getAuthor());
        NotificationListViewHolder.isbn.setText(notifications.get(position).getBook().getIsbn());

        Book book = notifications.get(position).getBook();
        /* Set book status colour*/
        book.setStatusCircleColor(
                NotificationListViewHolder.statusCircle,
                UserCredentialAPI.getInstance().getUsername());
        /* Set book photo */
        book.setPhoto(book, NotificationListViewHolder.bookPhoto);
    }

    @Override
    public int getItemCount()
    {
        return notifications.size();
    }
}
