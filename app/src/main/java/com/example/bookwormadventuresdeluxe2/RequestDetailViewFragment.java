package com.example.bookwormadventuresdeluxe2;

/**
 * Holds the view for seeing details on a book in the Requested tab
 * The user will be able to interact with status dependant request options on the book
 * <p>
 * Outstanding Issues: Still requires ISBN scan for handoff. Cannot view requester's profile
 */

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.NotificationUtility.NotificationHandler;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;

public class RequestDetailViewFragment extends DetailView
{
    private Button btn1;
    private Button btn2;
    private TextView exchange;
    private DocumentReference bookDocument;
    private RequestDetailViewFragment requestDetailViewFragment;
    private Resources resources;
    private ConstraintLayout dropdownContainer;

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
        resources = getResources();

        this.bookDetailView = inflater.inflate(R.layout.fragment_request_detail_view, null, false);
        ((TextView) bookDetailView.findViewById(R.id.app_header_title)).setText(R.string.requests_title);

        /* Get the fragment from the fragment manager */
        requestDetailViewFragment = (RequestDetailViewFragment) getFragmentManager().findFragmentByTag(getString(R.string.book_detail_fragment));

        // Setup back button
        super.onCreateView(inflater, container, savedInstanceState);

        this.btn1 = this.bookDetailView.findViewById(R.id.requestDetail_btn1);
        this.btn2 = this.bookDetailView.findViewById(R.id.requestDetail_btn2);
        this.exchange = this.bookDetailView.findViewById(R.id.request_exchange_location);
        this.dropdownContainer = this.bookDetailView.findViewById(R.id.dropdown_container);

        /* Update the UI based on the book's current status */
        switch (this.selectedBook.getStatus())
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

                if (this.selectedBook.getPickUpAddress().equals(""))
                {
                    setNotReadyToLend();
                }
                else
                {
                    setReadyToLend();
//                    this.bookDetailView.findViewById(R.id.borrow_exchange).setVisibility(View.VISIBLE);
                }

                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case bPending:
                this.btn1.setText(getString(R.string.wait_borrower));
                this.btn1.setBackgroundTintList(resources.getColorStateList(R.color.tempPhotoBackground));
                this.btn1.setTextColor(resources.getColorStateList(R.color.colorPrimary));

                this.btn1.setVisibility(View.VISIBLE);
                break;

            case rPending:
                this.btn1.setText(getString(R.string.accept_return));
                this.btn2.setText(getString(R.string.view_location));

                this.btn1.setOnClickListener(this::btnAcceptReturn);
                this.btn2.setOnClickListener(this::btnViewLocation);

                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            default:
                throw new InvalidParameterException("Bad status passed to RequestDetailView");
        }

        this.bookDocument = FirebaseFirestore
                .getInstance()
                .collection(getString(R.string.books_collection))
                .document(this.selectedBookId);

        return bookDetailView;
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
        //TODO: Launch Scan ISBN
        this.bookDocument.update(getString(R.string.status), getString(R.string.available));
        this.bookDocument.update(getString(R.string.requesters), new ArrayList<String>());
        this.bookDocument.update(getString(R.string.firestore_pick_up_address), "");
        onBackClick(view);
    }

    /**
     * Updates the status of the book
     *
     * @param view The view that was clicked on
     */
    private void btnLendBook(View view)
    {
        //TODO: launch scan
        this.bookDocument.update(getString(R.string.status), getString(R.string.bPending));
        onBackClick(view);
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
        sendRequestAcceptedNotification(borrowerUsername);
        onBackClick(view);
    }

    /**
     * Create hash map with notification info and  pass to Notification Handler process notification
     */
    private void sendRequestAcceptedNotification(String borrowerUsername)
    {

        /* Create notification for firestore collection */
        String message = "Borrow request accepted by: "
                + selectedBook.getOwner();
        HashMap<String, String> inAppNotification = new HashMap<>();
        inAppNotification.put(getString(R.string.firestore_user_notification_bookId_field), selectedBookId);
        inAppNotification.put(getString(R.string.firestore_user_notification_message_field), message);
        inAppNotification.put(getString(R.string.firestore_user_notification_timestamp_field), String.valueOf(Timestamp.now().getSeconds())); // to sort by latest
        /* Call notification handler to process notification */
        NotificationHandler.sendNotification("Request Accepted", message, borrowerUsername, inAppNotification);
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

        TextView user = bookDetailView.findViewById(R.id.book_request_user);
        user.setText(this.selectedBook.getRequesters().get(0));

        TextView status = bookDetailView.findViewById(R.id.book_details_status);
        switch (book.getStatus())
        {
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
                throw new InvalidParameterException("Invalid book status in RequestDetailView updateView");
        }

        /* Enables clicking of requester profile*/
        clickUsername(user, book.getRequesters().get(0), this.requestDetailViewFragment);
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
                /* Pulling UserProfileObject from database */
                FirebaseUserGetSet.getUser(spinner.getSelectedItem().toString(), new FirebaseUserGetSet.UserCallback()
                {
                    @Override
                    public void onCallback(UserProfileObject userObject)
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
     * Function to call when a location is set and the lend button should be set to pressable
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setReadyToLend()
    {
        this.btn2.setBackgroundTintList(resources.getColorStateList(R.color.colorPrimaryDark));
        this.btn2.setTextColor(resources.getColorStateList(R.color.colorBackground));
        this.btn2.setOnClickListener(this::btnLendBook);
    }

    /**
     * Function to call when a location is cancelled and the lend button should be set to not pressable
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setNotReadyToLend()
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
                setReadyToLend();
            }
            if (resultCode == Activity.RESULT_CANCELED)
            {
                this.bookDocument.update(getString(R.string.firestore_pick_up_address), "");
                this.selectedBook.setPickUpAddress("");
                setNotReadyToLend();
            }
        }

        requestDetailViewFragment.onFragmentInteraction(this.selectedBook, this.selectedBookId);
        getFragmentManager().beginTransaction()
                .show(requestDetailViewFragment)
                .commit();
    }
}
