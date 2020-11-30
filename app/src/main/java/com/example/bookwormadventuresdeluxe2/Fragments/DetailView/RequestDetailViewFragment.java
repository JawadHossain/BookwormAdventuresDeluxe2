package com.example.bookwormadventuresdeluxe2.Fragments.DetailView;

/**
 * Holds the view for seeing details on a book in the Requested tab
 * The user will be able to interact with status dependant request options on the book
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.Activities.Location.SetLocationActivity;
import com.example.bookwormadventuresdeluxe2.Activities.Location.ViewLocationActivity;
import com.example.bookwormadventuresdeluxe2.Fragments.NavigatonBar.ProfileFragment;
import com.example.bookwormadventuresdeluxe2.Models.Book;
import com.example.bookwormadventuresdeluxe2.Models.User;
import com.example.bookwormadventuresdeluxe2.R;
import com.example.bookwormadventuresdeluxe2.Utilities.FirebaseUserGetSet;
import com.example.bookwormadventuresdeluxe2.Utilities.NotificationUtility.NotificationHandler;
import com.example.bookwormadventuresdeluxe2.Utilities.Status;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestDetailViewFragment extends DetailView
{
    private Button btn1;
    private Button btn2;
    private DocumentReference bookDocument;
    private RequestDetailViewFragment requestDetailViewFragment;
    private ConstraintLayout dropdownContainer;

    public static final int REQUEST_GIVE_SCAN = 5;
    public static final int REQUEST_RECEIVE_SCAN = 6;
    private static int SetLocationActivityResultCode = 7;

    public RequestDetailViewFragment()
    {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {

        this.bookDetailView = inflater.inflate(R.layout.fragment_request_detail_view, null, false);
        ((TextView) bookDetailView.findViewById(R.id.app_header_title)).setText(R.string.requests_title);

        /* Get the fragment from the fragment manager */
        requestDetailViewFragment = (RequestDetailViewFragment) getFragmentManager().findFragmentByTag(getString(R.string.book_detail_fragment));

        // Setup back button
        super.onCreateView(inflater, container, savedInstanceState);

        this.btn1 = this.bookDetailView.findViewById(R.id.requestDetail_btn1);
        this.btn2 = this.bookDetailView.findViewById(R.id.requestDetail_btn2);
        this.dropdownContainer = this.bookDetailView.findViewById(R.id.dropdown_container);

        /* Update the UI based on the book's current status */
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
                    /* Update book */
                    selectedBook = snapshot.toObject(Book.class);

                    /* Close fragment if all requests are cancelled */
                    if (selectedBook.getStatus().equals(Status.Available) && RequestDetailViewFragment.this.isVisible())
                    {
                        closeFragment(RequestDetailViewFragment.this, getString(R.string.request_cancelled_message));
                    }
                    else
                    {
                        /* Draw new book */
                        Activity activity = getActivity();
                        if (isAdded() && activity != null)
                        {
                            redraw();
                        }
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
        switch (selectedBook.getStatus())
        {
            case Requested:
                this.dropdownContainer.setVisibility(View.VISIBLE);
                Spinner requesters = this.bookDetailView.findViewById(R.id.chose_request);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, this.selectedBook.getRequesters());
                requesters.setAdapter(adapter);
                requesters.setVisibility(View.VISIBLE);
                bookDetailView.findViewById(R.id.book_request_user).setVisibility(View.GONE);

                /* Enables viewing profile of selected requester*/
                TextView viewProfileBtn = bookDetailView.findViewById(R.id.view_profile_button);
                viewProfileBtn.setVisibility(View.VISIBLE);
                sliderProfileButton(viewProfileBtn, requesters);

                this.btn1.setText(getString(R.string.accept));
                this.btn2.setText(getString(R.string.deny));

                this.btn1.setOnClickListener(this::btnAccept);
                this.btn2.setOnClickListener(this::btnDeny);

                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case Accepted:
                this.dropdownContainer.setVisibility(View.GONE);
                this.btn1.setText(getString(R.string.set_location_label));
                this.btn2.setText(getString(R.string.lend_book));
                this.btn1.setOnClickListener(this::btnSetLocation);

                TextView user;
                user = bookDetailView.findViewById(R.id.book_request_user);
                if (this.selectedBook.getRequesters().size() > 0)
                {
                    user.setText(this.selectedBook.getRequesters().get(0));
                    /* Enables clicking of requester profile*/
                    clickUsername(user, this.selectedBook.getRequesters().get(0), this.requestDetailViewFragment);
                }
                user.setVisibility(View.VISIBLE);

                String pickUpAddress = this.selectedBook.getPickUpAddress();
                if (pickUpAddress == null || pickUpAddress.equals(""))
                {
                    this.disableButton(this.btn2);
                }
                else
                {
                    this.enableButton(this.btn2);
                    this.btn2.setOnClickListener(this::btnLendBook);
                }
                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case Borrowed:
                this.btn1.setText(getString(R.string.view_location));
                this.btn2.setText(R.string.book_lent);
                this.disableButton(this.btn1);
                this.disableButton(this.btn2);
                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case bPending:
                this.btn1.setText(getString(R.string.view_location));
                this.btn2.setText(getString(R.string.wait_borrower));

                this.disableButton(this.btn1);
                this.disableButton(this.btn2);

                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case rPending:
                this.btn1.setText(getString(R.string.view_location));
                this.btn2.setText(getString(R.string.accept_return));

                this.enableButton(this.btn1);
                this.enableButton(this.btn2);
                this.btn1.setOnClickListener(this::btnViewLocation);
                this.btn2.setOnClickListener(this::btnAcceptReturn);

                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case Available:
                this.btn1.setVisibility(View.INVISIBLE);
                this.btn2.setVisibility(View.INVISIBLE);
                break;

            default:
                throw new InvalidParameterException("Bad status passed to RequestDetailView");
        }
    }

    /**
     * Start view location Activity to allow user to view a marked location
     *
     * @param view
     */
    private void btnViewLocation(View view)
    {
        Intent viewLocationIntent = new Intent(getActivity(), ViewLocationActivity.class);
        viewLocationIntent.putExtra("location", this.selectedBook.getPickUpAddress());
        startActivity(viewLocationIntent);
    }

    /**
     * Updates the status of the book, resets
     * books requesters list
     *
     * @param view The view that was clicked on
     */
    private void btnAcceptReturn(View view)
    {
        onScanCall(REQUEST_RECEIVE_SCAN);
    }

    /**
     * Updates the status of the book
     *
     * @param view The view that was clicked on
     */
    private void btnLendBook(View view)
    {
        onScanCall(REQUEST_GIVE_SCAN);
    }

    private void btnSetLocation(View view)
    {
        Intent setLocationActivityIntent = new Intent(getActivity(), SetLocationActivity.class);
        startActivityForResult(setLocationActivityIntent, SetLocationActivityResultCode);
    }

    /**
     * Updates the status of the book, removes the
     * requester from books requesters list
     *
     * @param view The view that was clicked on
     */
    private void btnDeny(View view)
    {

        String requester = ((Spinner) bookDetailView.findViewById(R.id.chose_request)).getSelectedItem().toString();
        ArrayList<String> requesters = this.selectedBook.getRequesters();
        requesters.remove(requester);

        this.bookDocument.update(getString(R.string.requesters), requesters);
        if (requesters.size() == 0)
        {
            this.bookDocument.update(getString(R.string.status), getString(R.string.available));
        }
        onBackClick(view);
    }

    /**
     * Updates the status of the book, sends a notification
     * to the borrower
     *
     * @param view The view that was clicked on
     */
    private void btnAccept(View view)
    {

        ArrayList<String> borrower = new ArrayList<String>();
        borrower.add(((Spinner) bookDetailView.findViewById(R.id.chose_request)).getSelectedItem().toString());

        this.bookDocument.update(getString(R.string.requesters), borrower);
        this.bookDocument.update(getString(R.string.status), getString(R.string.accepted));
        String borrowerUsername = borrower.get(0);
        // Send In-app and Push notification to Borrower
        sendNotification(borrowerUsername, getString(R.string.request_accepted_message));
    }

    /**
     * Create hash map with notification info and  pass to Notification Handler process notification
     */
    private void sendNotification(String borrowerUsername, String notificationMessage)
    {

        /* Create notification for firestore collection */
        String message = notificationMessage + " "
                + selectedBook.getOwner();
        HashMap<String, String> inAppNotification = new HashMap<>();
        inAppNotification.put(getString(R.string.firestore_user_notification_bookId_field), selectedBookId);
        inAppNotification.put(getString(R.string.firestore_user_notification_message_field), message);
        inAppNotification.put(getString(R.string.firestore_user_notification_timestamp_field), String.valueOf(Timestamp.now().getSeconds())); // to sort by latest
        /* Call notification handler to process notification */
        NotificationHandler.sendNotification(message, borrowerUsername, inAppNotification);
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

        TextView user;
        user = bookDetailView.findViewById(R.id.book_request_user);
        if (this.selectedBook.getRequesters().size() > 0)
        {
            user.setText(this.selectedBook.getRequesters().get(0));
            /* Enables clicking of requester profile*/
            clickUsername(user, book.getRequesters().get(0), this.requestDetailViewFragment);
        }

        TextView status = bookDetailView.findViewById(R.id.book_details_status);
        switch (book.getStatus())
        {
            case Available:
                /* Never reached */
                break;
            case Requested:
            case Accepted:
                status.setText(getString(R.string.request_detail_requested));
                break;
            case bPending:
            case Borrowed:
                status.setText(getString(R.string.request_detail_borrowed));
                break;
            case rPending:
                status.setText(getString(R.string.request_detail_return));
                break;
            default:
                throw new InvalidParameterException("Invalid book status in RequestDetailView updateView: " + book.getStatus());
        }
    }

    /**
     * Opens selected user profile on Button click
     *
     * @param viewProfileButton TextView in view
     * @param spinner           Spinner for selecting requester
     */
    private void sliderProfileButton(TextView viewProfileButton, Spinner spinner)
    {
        viewProfileButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /* Pulling User from database */
                FirebaseUserGetSet.getUser(spinner.getSelectedItem().toString(), new FirebaseUserGetSet.UserCallback()
                {
                    @Override
                    public void onCallback(User userObject)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(getString(R.string.profile_object), userObject);
                        ProfileFragment profileFragment = new ProfileFragment();
                        profileFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                .add(R.id.frame_container, profileFragment, getString(R.string.other_profile_fragment))
                                .hide(requestDetailViewFragment)
                                .commit();
                    }
                });
            }
        });
    }

    /**
     * Takes the user back to the main Requests screen
     *
     * @param v The view that was clicked on
     */
    public void onBackClick(View v)
    {
        Fragment requestsFragment = getFragmentManager().findFragmentByTag(getString(R.string.requests_fragment));
        Bundle args = new Bundle();
        requestsFragment.setArguments(args);
        getFragmentManager().beginTransaction().remove(this).show(requestsFragment).commit();
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
            if (requestCode == REQUEST_GIVE_SCAN)
            {
                this.bookDocument.update(getString(R.string.status), getString(R.string.bPending));
                this.selectedBook.setStatus(Status.bPending);
                message = getString(R.string.hand_to_borrower);
                this.redraw();
                // Send In-app and Push notification to Borrower
                sendNotification(selectedBook.getRequesters().get(0), getString(R.string.my_requests_book_confirm_lend_message));
            }
            else if (requestCode == REQUEST_RECEIVE_SCAN)
            {
                this.bookDocument.update(getString(R.string.status), getString(R.string.available));
                this.bookDocument.update(getString(R.string.requesters), new ArrayList<String>());
                this.bookDocument.update(getString(R.string.firestore_pick_up_address), "");
                this.selectedBook.setStatus(Status.Available);
                this.selectedBook.setRequesters(new ArrayList<String>());
                message = getString(R.string.book_received);
                this.redraw();
            }
            else
            {
                throw new IllegalStateException("BorrowDetailViewFragment processBookHandOff(...) error");
            }
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(getActivity(), getString(R.string.unsuccessful_scan), Toast.LENGTH_LONG).show();
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
        else if (requestCode == REQUEST_GIVE_SCAN || requestCode == REQUEST_RECEIVE_SCAN)
        {
            processBookHandOff(requestCode, resultCode, data);
        }
        requestDetailViewFragment.onFragmentInteraction(this.selectedBook, this.selectedBookId);
        getFragmentManager().beginTransaction()
                .show(requestDetailViewFragment)
                .commitAllowingStateLoss();
    }
}
