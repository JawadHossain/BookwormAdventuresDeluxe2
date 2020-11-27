package com.example.bookwormadventuresdeluxe2.Fragments.DetailView;

/**
 * Holds the view for seeing details on a book in the borrowed tab
 * The user will be able to interact with borrow options on the book
 * <p>
 * Outstanding Issues: Still requires ISBN scan for handoff
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.Models.Book;
import com.example.bookwormadventuresdeluxe2.R;
import com.example.bookwormadventuresdeluxe2.Activities.Location.SetLocationActivity;
import com.example.bookwormadventuresdeluxe2.Utilities.NotificationUtility.NotificationHandler;
import com.example.bookwormadventuresdeluxe2.Utilities.Status;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.example.bookwormadventuresdeluxe2.Activities.Location.ViewLocationActivity;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.security.InvalidParameterException;
import java.util.HashMap;

public class BorrowDetailViewFragment extends DetailView
{
    private Button btn1;
    private Button btn2;
    private DocumentReference bookDocument;
    private BorrowDetailViewFragment borrowDetailViewFragment;

    private static int SetLocationActivityResultCode = 7;

    private String source = "";
    public static int BORROW_RECIEVE_SCAN = 8;
    public static int BORROW_RETURN_SCAN = 9;

    public BorrowDetailViewFragment()
    {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

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

        this.redraw();

        this.bookDocument = FirebaseFirestore
                .getInstance()
                .collection(getString(R.string.books_collection))
                .document(this.selectedBookId);

        this.bookDocument.addSnapshotListener(new EventListener<DocumentSnapshot>()
        {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e)
            {
                if (snapshot != null && snapshot.exists())
                {
                    selectedBook = snapshot.toObject(Book.class);
                    Activity activity = getActivity();
                    if (isAdded() && activity != null)
                    {
                        redraw();
                    }
                }
            }
        });

        return bookDetailView;
    }

    /**
     * Redraws the screen to adjust for the book state and information
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void redraw()
    {
        this.updateView(this.selectedBook);
        String pickUpAddress = this.selectedBook.getPickUpAddress();
        switch (selectedBook.getStatus())
        {
            case Available:
            case Requested:
                if ((!selectedBook.getRequesters().contains(UserCredentialAPI.getInstance().getUsername())))
                {
                    this.enableButton(this.btn1);
                    this.btn1.setText(getString(R.string.request_book));

                    this.btn1.setOnClickListener(this::btnRequestBook);

                    this.btn1.setVisibility(View.VISIBLE);
                }
                this.btn2.setVisibility(View.GONE);
                break;

            case Accepted:
                this.btn1.setText(getString(R.string.view_location));
                this.btn2.setText(getString(R.string.scan));

                if (pickUpAddress == null || pickUpAddress.equals("")) // null.equals is invalid
                {
                    this.disableButton(this.btn1);
                }
                else
                {
                    this.enableButton(this.btn1);
                    this.btn1.setOnClickListener(this::btnViewLocation);
                }

                this.disableButton(this.btn2);
                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case bPending:
                this.btn1.setText(getString(R.string.view_location));
                this.btn2.setText(getString(R.string.scan));
                this.enableButton(this.btn2);

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
                    this.enableButton(this.btn1);
                    this.disableButton(this.btn2);
                }
                else
                {
                    this.enableButton(this.btn1);
                    this.enableButton(this.btn2);
                    this.btn2.setOnClickListener(this::btnReturnBook);
                }


                this.btn1.setOnClickListener(this::btnSetLocation);

                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case rPending:
                this.btn1.setText(getString(R.string.set_location));
                this.btn2.setText(getString(R.string.wait_owner));
                this.disableButton(this.btn1);
                this.disableButton(this.btn2);

                this.btn2.setVisibility(View.VISIBLE);
                this.btn1.setVisibility(View.VISIBLE);
                break;

            default:
                throw new InvalidParameterException("Bad status passed to BorrowDetailView");
        }
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
        // Launch Scan ISBN
        onScanCall(BORROW_RETURN_SCAN);
    }

    /**
     * Updates the status of the book
     *
     * @param view The view that was clicked on
     */
    private void btnScan(View view)
    {
        // Launch Scan ISBN
        onScanCall(BORROW_RECIEVE_SCAN);
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
     * Process the book handoff
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void processBookHandOff(int requestCode, int resultCode, @Nullable Intent data)
    {
        String message = "";
        IntentResult result = IntentIntegrator.parseActivityResult(resultCode, data);
        if (result != null && result.getContents() != null &&
                this.selectedBook.getIsbn().equals(result.getContents()))
        {
            // scan successful
            if (requestCode == BORROW_RECIEVE_SCAN)
            {
                this.bookDocument.update(getString(R.string.status), getString(R.string.borrowed));
                this.selectedBook.setStatus(Status.Borrowed);
                message = "Book received";
                this.updateView(this.selectedBook);
                this.disableButton(this.btn1);
                this.disableButton(this.btn2);
            }
            else if (requestCode == BORROW_RETURN_SCAN)
            {
                this.bookDocument.update(getString(R.string.status), getString(R.string.rPending));
                this.selectedBook.setStatus(Status.rPending);
                message = "Give book to owner";
                this.updateView(this.selectedBook);
                // notify owner
                // Send In-app and Push notification to owner
                sendNotification(getString(R.string.return_request_message));

            }
            else
            {
                throw new IllegalStateException("BorrowDetailViewFragment processBookHandOff(...) error");
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getActivity(), "Scan was unsuccessful", Toast.LENGTH_LONG).show();
        }

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
                this.enableButton(this.btn2);
            }
            if (resultCode == Activity.RESULT_CANCELED)
            {
                this.bookDocument.update(getString(R.string.firestore_pick_up_address), "");
                this.selectedBook.setPickUpAddress("");
                this.disableButton(this.btn2);
            }
        }
        else if (requestCode == BORROW_RECIEVE_SCAN || requestCode == BORROW_RETURN_SCAN)
        {
            processBookHandOff(requestCode, resultCode, data);
        }


        borrowDetailViewFragment.onFragmentInteraction(this.selectedBook, this.selectedBookId);
        getFragmentManager().beginTransaction()
                .show(borrowDetailViewFragment)
                .commitAllowingStateLoss();
    }
}