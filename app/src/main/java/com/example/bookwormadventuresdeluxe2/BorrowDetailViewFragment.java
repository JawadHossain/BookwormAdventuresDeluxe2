package com.example.bookwormadventuresdeluxe2;

/**
 * Holds the view for seeing details on a book in the borrowed tab
 * The user will be able to interact with borrow options on the book
 * <p>
 * Outstanding Issues: Still requires ISBN scan for handoff
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.NotificationUtility.NotificationHandler;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.InvalidParameterException;
import java.util.HashMap;

public class BorrowDetailViewFragment extends DetailView
{
    private Button btn1;
    private Button btn2;
    private TextView exchange;
    private DocumentReference bookDocument;
    private BorrowDetailViewFragment borrowDetailViewFragment;
    private Resources resources;

    private static int SetLocationActivityResultCode = 7;

    private String source = "";

    public BorrowDetailViewFragment()
    {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        resources = getResources();

        /* Grabbing source fragment of book item after click*/
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            source = bundle.getString(getString(R.string.book_click_source_fragment));
        }
        else
        {
            source = getString(R.string.borrow);
        }

        this.bookDetailView = inflater.inflate(R.layout.fragment_borrow_detail_view, null, false);
        ((TextView) bookDetailView.findViewById(R.id.app_header_title)).setText(source);

        /* Get the fragment from the fragment manager */
        borrowDetailViewFragment = (BorrowDetailViewFragment) getFragmentManager().findFragmentByTag(getString(R.string.book_detail_fragment));

        // setup back button
        super.onCreateView(inflater, container, savedInstanceState);

        this.btn1 = this.bookDetailView.findViewById(R.id.borrowDetail_btn1);
        this.btn2 = this.bookDetailView.findViewById(R.id.borrowDetail_btn2);
        this.exchange = this.bookDetailView.findViewById(R.id.borrow_exchange_location);

        String pickUpAddress = this.selectedBook.getPickUpAddress();

        switch (selectedBook.getStatus())
        {
            case Available:
            case Requested:
                if ((!selectedBook.getRequesters().contains(UserCredentialAPI.getInstance().getUsername())))
                {
                    this.btn1.setText(getString(R.string.request_book));

                    this.btn1.setOnClickListener(this::btnRequestBook);

                    this.btn1.setVisibility(View.VISIBLE);
                }
                break;

            case Accepted:
                this.btn1.setText(getString(R.string.view_location));

                if (pickUpAddress == null || pickUpAddress.equals("")) // null.equals is invalid
                {
                    this.btn1.setBackgroundTintList(resources.getColorStateList(R.color.tempPhotoBackground));
                    this.btn1.setTextColor(resources.getColorStateList(R.color.colorPrimary));
                }
                else
                {
                    this.btn1.setOnClickListener(this::btnViewLocation);
                }

                this.btn1.setVisibility(View.VISIBLE);
                break;

            case bPending:
                this.btn1.setText(getString(R.string.view_location));
                this.btn2.setText(getString(R.string.scan));

                this.btn1.setOnClickListener(this::btnViewLocation);
                this.btn2.setOnClickListener(this::btnScan);

                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case Borrowed:
                this.btn1.setText(getString(R.string.set_location));
                this.btn2.setText(getString(R.string.return_book));

                if (pickUpAddress == null || pickUpAddress.equals("")) // null.equals is invalid
                {
                    setNotReadyToReturn();
                }
                else
                {
                    setReadyToReturn();
//                    this.bookDetailView.findViewById(R.id.borrow_exchange).setVisibility(View.VISIBLE);
                }

                this.btn1.setOnClickListener(this::btnSetLocation);

                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case rPending:
                this.btn1.setText(getString(R.string.wait_owner));
                this.btn1.setBackgroundTintList(resources.getColorStateList(R.color.tempPhotoBackground));
                this.btn1.setTextColor(resources.getColorStateList(R.color.colorPrimary));

                this.btn1.setVisibility(View.VISIBLE);
                break;

            default:
                throw new InvalidParameterException("Bad status passed to BorrowDetailView");
        }

        this.bookDocument = FirebaseFirestore
                .getInstance()
                .collection(getString(R.string.books_collection))
                .document(this.selectedBookId);

        return bookDetailView;
    }

    /**
     * Send notification and request to book owner
     *
     * @param view The view that was clicked on
     */
    private void btnRequestBook(View view)
    {
        this.bookDocument.update(getString(R.string.requesters),
                FieldValue.arrayUnion(UserCredentialAPI.getInstance().getUsername()));
        this.bookDocument.update(getString(R.string.status), getString(R.string.requested));

        // Send In-app and Push notification to owner
        sendNotification(getString(R.string.borrow_request_message));
        onBackClick(view);
    }

    /**
     * Create hash map with notification info pass to Notification Handler process notification
     */
    private void sendNotification(String notificationMessage)
    {
        /* Create notification for firestore collection */
        String message = notificationMessage + " "
                + UserCredentialAPI.getInstance().getUsername();
        HashMap<String, String> inAppNotification = new HashMap<>();
        inAppNotification.put(getString(R.string.firestore_user_notification_bookId_field), selectedBookId);
        inAppNotification.put(getString(R.string.firestore_user_notification_message_field), message);
        inAppNotification.put(getString(R.string.firestore_user_notification_timestamp_field), String.valueOf(Timestamp.now().getSeconds()));
        /* Call notification handler to process notification */
        NotificationHandler.sendNotification(message, selectedBook.getOwner(), inAppNotification);
    }

    /**
     * Start set location Activity to allow user to pick a location
     *
     * @param view
     */
    private void btnSetLocation(View view)
    {
        Intent setLocationActivityIntent = new Intent(getActivity(), SetLocationActivity.class);
        startActivityForResult(setLocationActivityIntent, SetLocationActivityResultCode);
    }

    /**
     * Updates the status of the book
     *
     * @param view The view that was clicked on
     */
    private void btnReturnBook(View view)
    {
        //TODO: actually do the stuff
        // Launch Scan ISBN
        this.bookDocument.update(getString(R.string.status), getString(R.string.rPending));
        // notify owner
        onBackClick(view);
        // Send In-app and Push notification to owner
        sendNotification(getString(R.string.return_request_message));
    }

    /**
     * Updates the status of the book
     *
     * @param view The view that was clicked on
     */
    private void btnScan(View view)
    {
        //TODO: actually do the stuff
        // Launch Scan ISBN
        this.bookDocument.update(getString(R.string.status), getString(R.string.borrowed));
        onBackClick(view);
    }

    private void btnViewLocation(View view)
    {
        Intent viewLocationIntent = new Intent(getActivity(), ViewLocationActivity.class);
        viewLocationIntent.putExtra("location", this.selectedBook.getPickUpAddress());
        startActivity(viewLocationIntent);
    }

    /**
     * Update the textfields for book detail view based on the given book
     *
     * @param book The book containing the data to populate the textfields with
     */
    public void updateView(Book book)
    {
        // Set the content based on the book that was selected
        super.updateView(book);

        TextView status = bookDetailView.findViewById(R.id.book_details_status);
        status.setText(getString(R.string.owned_by));

        TextView user = bookDetailView.findViewById(R.id.book_request_user);
        user.setText(book.getOwner());

        clickUsername(user, book.getOwner(), borrowDetailViewFragment);
    }

    /**
     * Takes the user back to the main Requests screen
     *
     * @param v The view that was clicked on
     */
    public void onBackClick(View v)
    {
        /* Source fragment was Search, return to search books*/
        if (source.equals(getString(R.string.search_title)))
        {
            Fragment searchFragment = getFragmentManager().findFragmentByTag(getString(R.string.search_fragment));
            getFragmentManager().beginTransaction().remove(this).show(searchFragment).commit();
        }
        /* Source fragment was Borrow, return to Borrow */
        else
        {
            Fragment requestsFragment = getFragmentManager().findFragmentByTag(getString(R.string.requests_fragment));
            Bundle args = new Bundle();
            requestsFragment.setArguments(args);
            args.putBoolean(getString(R.string.borrow), true);
            getFragmentManager().beginTransaction().remove(this).show(requestsFragment).commit();
        }
    }

    /**
     * Function to call when a location is set and the return button should be set to pressable
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setReadyToReturn()
    {
        this.btn2.setBackgroundTintList(resources.getColorStateList(R.color.colorPrimaryDark));
        this.btn2.setTextColor(resources.getColorStateList(R.color.colorBackground));
        this.btn2.setOnClickListener(this::btnReturnBook);
    }

    /**
     * Function to call when a location is cancelled and the return button should be set to not pressable
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setNotReadyToReturn()
    {
        this.btn2.setBackgroundTintList(resources.getColorStateList(R.color.tempPhotoBackground));
        this.btn2.setTextColor(resources.getColorStateList(R.color.colorPrimary));
        this.btn2.setOnClickListener(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if (requestCode == SetLocationActivityResultCode)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                String pickUpLocation = data.getStringExtra("pickUpLocation");
                this.bookDocument.update(getString(R.string.firestore_pick_up_address), pickUpLocation);
                this.selectedBook.setPickUpAddress(pickUpLocation);
                setReadyToReturn();
            }
            if (resultCode == Activity.RESULT_CANCELED)
            {
                this.bookDocument.update(getString(R.string.firestore_pick_up_address), "");
                this.selectedBook.setPickUpAddress("");
                setNotReadyToReturn();
            }
        }

        borrowDetailViewFragment.onFragmentInteraction(this.selectedBook, this.selectedBookId);
        getFragmentManager().beginTransaction()
                .show(borrowDetailViewFragment)
                .commit();
    }
}