package com.example.bookwormadventuresdeluxe2.Fragments;

/**
 * Fragment responsible for showing the notifications that a user has received and allowing them
 * to interact with said notifications.
 */

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookwormadventuresdeluxe2.Controllers.NotificationListAdapter;
import com.example.bookwormadventuresdeluxe2.Fragments.NavigatonBar.MyBooksFragment;
import com.example.bookwormadventuresdeluxe2.Models.Book;
import com.example.bookwormadventuresdeluxe2.Models.Notification;
import com.example.bookwormadventuresdeluxe2.R;
import com.example.bookwormadventuresdeluxe2.Utilities.ActiveFragmentTracker;
import com.example.bookwormadventuresdeluxe2.Utilities.FirebaseUserGetSet;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class NotificationFragment extends Fragment
{
    public static final String TAG = "Notification Fragment";
    private RecyclerView notificationRecyclerView;
    private NotificationListAdapter notificationRecyclerAdapter;
    private RecyclerView.LayoutManager notificationRecyclerLayoutManager;
    private ArrayList<Notification> notificationList;

    private MaterialTextView appHeaderText;
    private ImageButton backButton;

    /**
     * Required empty public constructor
     */
    public NotificationFragment()
    {
//
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Fragment filterMenu = getFragmentManager().findFragmentByTag(getString(R.string.filter_menu_fragment));
        /* If the filtermenu is visible, then hide it */
        if ((filterMenu != null))
        {
            getFragmentManager().beginTransaction().remove(filterMenu).commit();
        }

        this.notificationList = new ArrayList<Notification>();
        // Inflate the layout for this fragment
        View notificationView = inflater.inflate(R.layout.fragment_notification, container, false);

        appHeaderText = notificationView.findViewById(R.id.app_header_title);
        appHeaderText.setText(R.string.notification_title);

        this.backButton = notificationView.findViewById(R.id.app_header_back_button);
        this.backButton.setVisibility(View.VISIBLE);
        this.backButton.setOnClickListener(this::onBackClick);

        return notificationView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {

        notificationRecyclerView = (RecyclerView) view.findViewById(R.id.notification_recycler_view);
        notificationRecyclerView.setHasFixedSize(true);

        notificationRecyclerLayoutManager = new LinearLayoutManager(this.getContext());
        notificationRecyclerView.setLayoutManager(notificationRecyclerLayoutManager);

        notificationRecyclerAdapter = new NotificationListAdapter(notificationList, this.getContext());
        notificationRecyclerView.setAdapter(notificationRecyclerAdapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = UserCredentialAPI.getInstance().getUserId();
        db
                .collection(getString(R.string.users_collection) + "/"
                        + userID + "/" + getString(R.string.notifications_collection))
                .orderBy(getString(R.string.firestore_user_notification_timestamp_field), Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            /* Get user notifications*/
                            for (DocumentSnapshot document : task.getResult())
                            {
                                String bookID = document.getString(getString(R.string.firestore_user_notification_bookId_field));
                                String message = document.getString(getString(R.string.firestore_user_notification_message_field));
                                final Book[] book = new Book[1];

                                /* Initialize as empty book */
                                book[0] = new Book();

                                /* Find Book information */
                                db.collection(getString(R.string.books_collection))
                                        .document(bookID)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>()
                                                               {
                                                                   @Override
                                                                   public void onComplete(@NonNull Task<DocumentSnapshot> task)
                                                                   {
                                                                       if (task.isSuccessful())
                                                                       {
                                                                           /* Create book associated with notification*/
                                                                           DocumentSnapshot document = task.getResult();
                                                                           book[0] = document.toObject(Book.class);
                                                                           if(book[0] != null) // check if book exists
                                                                           {
                                                                               /* Create Notification */
                                                                               Notification notification = new Notification(book[0], message);
                                                                               notificationList.add(notification);
                                                                               notificationRecyclerAdapter.notifyDataSetChanged();
                                                                           }
                                                                       }
                                                                   }
                                                               }
                                        );
                            }
                        }
                        else
                        {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    /**
     * Listener for when the back button is clicked
     *
     * @param v The view this is called from
     */
    public void onBackClick(View v)
    {
        /* Remove notifications*/
        Long notificationCount = UserCredentialAPI.getInstance().getNotificationCount();
        if (notificationCount != null && notificationCount > 0)
        {
            FirebaseUserGetSet.resetNotifications(UserCredentialAPI.getInstance().getUserId());
        }

        MyBooksFragment myBooksFragment = (MyBooksFragment) getFragmentManager().findFragmentByTag(getString(R.string.my_books_fragment));
        myBooksFragment.updateNotificationBadge(); // update notification badge
        getFragmentManager().beginTransaction().hide(this).show(ActiveFragmentTracker.activeFragment).commit();
    }

    @Override
    public void onHiddenChanged(boolean hidden)
    {
        super.onHiddenChanged(hidden);
        Fragment filterMenu = getFragmentManager().findFragmentByTag(getString(R.string.filter_menu_fragment));

        /* If we are not hidden, and the filtermenu is visible, then hide it */
        if ((filterMenu != null) && (hidden == false))
        {
            getFragmentManager().beginTransaction().remove(filterMenu).commit();
        }
    }
}