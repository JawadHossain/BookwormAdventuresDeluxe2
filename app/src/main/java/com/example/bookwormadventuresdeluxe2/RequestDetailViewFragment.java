package com.example.bookwormadventuresdeluxe2;

import android.os.Build;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.bookwormadventuresdeluxe2.Utilities.DetailView;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Holds the view for seeing details on a book in the Requested tab
 * The user will be able to interact with status dependant request options on the book
 */
public class RequestDetailViewFragment extends DetailView
{
    private Button btn1;
    private Button btn2;
    private TextView exchange;
    private DocumentReference bookDocument;

    public RequestDetailViewFragment()
    {
        // Required empty public constructor
    }

    public static RequestDetailViewFragment newInstance(String param1, String param2)
    {
        RequestDetailViewFragment fragment = new RequestDetailViewFragment();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.bookDetailView = inflater.inflate(R.layout.fragment_request_detail_view, null, false);
        ((TextView) bookDetailView.findViewById(R.id.app_header_title)).setText(R.string.requests_title);

        // Setup back button
        super.onCreateView(inflater, container, savedInstanceState);

        this.btn1 = this.bookDetailView.findViewById(R.id.requestDetail_btn1);
        this.btn2 = this.bookDetailView.findViewById(R.id.requestDetail_btn2);
        this.exchange = this.bookDetailView.findViewById(R.id.request_exchange_location);

        switch (selectedBook.getStatus())
        {

            case Requested:
                Spinner requesters = this.bookDetailView.findViewById(R.id.chose_request);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, this.selectedBook.getRequesters());
                requesters.setAdapter(adapter);
                requesters.setVisibility(View.VISIBLE);
                bookDetailView.findViewById(R.id.book_request_user).setVisibility(View.GONE);

                this.btn1.setText(getString(R.string.accept));
                this.btn2.setText(getString(R.string.deny));

                this.btn1.setOnClickListener(this::btnAccept);
                this.btn2.setOnClickListener(this::btnDeny);

                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case Accepted:
                this.btn1.setText(getString(R.string.set_location_label));
                this.btn2.setText(getString(R.string.lend_book));

                this.btn1.setOnClickListener(this::btnSetLocation);
                this.btn2.setOnClickListener(this::btnLendBook);

                //TODO: get pickup location from book
//        this.bookDetailView.findViewById(R.id.request_exchange).setVisibility(View.VISIBLE);

                this.btn1.setVisibility(View.VISIBLE);
                this.btn2.setVisibility(View.VISIBLE);
                break;

            case bPending:
                this.btn1.setText(getString(R.string.wait_borrower));
                this.btn1.setBackgroundTintList(getResources().getColorStateList(R.color.tempPhotoBackground));
                this.btn1.setTextColor(getResources().getColorStateList(R.color.colorPrimary));

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

    private void btnViewLocation(View view)
    {
        //TODO: actually do the stuff
        // launch ViewLocation
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
        //TODO: actually do the stuff
        // launch SetLocation
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
        onBackClick(view);
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
        switch(book.getStatus()) {
            case Requested:
            case Accepted: status.setText(getString(R.string.request_detail_requested)); break;
            case bPending:
            case Borrowed: status.setText(getString(R.string.request_detail_borrowed)); break;
            case rPending: status.setText(getString(R.string.request_detail_return)); break;
            default: throw new InvalidParameterException("Invalid book status in RequestDetailView updateView");
        }

        TextView user = bookDetailView.findViewById(R.id.book_request_user);
        user.setText(this.selectedBook.getRequesters().get(0));
    }

    /**
     * Takes the user back to the main Requests screen
     *
     * @param v The view that was clicked on
     */
    public void onBackClick(View v)
    {
        RequestsFragment fragment = new RequestsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

}