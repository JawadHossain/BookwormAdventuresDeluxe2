package com.example.bookwormadventuresdeluxe2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

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

        public NotificationListViewHolder(ConstraintLayout notificationItemLayout)
        {
            super(notificationItemLayout);
            this.message = (TextView) notificationItemLayout.getViewById(R.id.notification_message);
            this.title = (TextView) (notificationItemLayout.getViewById(R.id.book_item)).findViewById(R.id.book_item_title);
            this.author = (TextView) (notificationItemLayout.getViewById(R.id.book_item)).findViewById(R.id.book_item_author);
            this.isbn = (TextView) (notificationItemLayout.getViewById(R.id.book_item)).findViewById(R.id.book_item_isbn);
            this.statusCircle = (ImageView) (notificationItemLayout.getViewById(R.id.book_item)).findViewById(R.id.book_item_status);
        }
    }

    public NotificationListAdapter(ArrayList<Notification> notifications, Context context)
    {
        this.notifications = notifications;
        this.context = context;
    }

    // Create new views
    public NotificationListAdapter.NotificationListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ConstraintLayout notificationItem = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_item, parent, false);
        NotificationListAdapter.NotificationListViewHolder NotificationListViewHolder = new NotificationListAdapter.NotificationListViewHolder((notificationItem));
        this.NotificationListViewHolder = NotificationListViewHolder;
        return NotificationListViewHolder;
    }

    public NotificationListAdapter.NotificationListViewHolder getViewHolder()
    {
        return this.NotificationListViewHolder;
    }

    // Replace the contents of the view with the appropriate data
    public void onBindViewHolder(NotificationListAdapter.NotificationListViewHolder NotificationListViewHolder, int position)
    {
        NotificationListViewHolder.message.setText(notifications.get(position).getMessage());
        NotificationListViewHolder.title.setText(notifications.get(position).getTitle());
        NotificationListViewHolder.author.setText(notifications.get(position).getAuthor());
        NotificationListViewHolder.isbn.setText(notifications.get(position).getIsbn());

        notifications.get(position).setStatusCircleColor(notifications.get(position).getStatus(), NotificationListViewHolder.statusCircle);
    }

    @Override
    public int getItemCount()
    {
        return notifications.size();
    }
}
