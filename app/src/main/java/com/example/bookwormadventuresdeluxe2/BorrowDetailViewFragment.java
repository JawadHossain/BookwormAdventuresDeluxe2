package com.example.bookwormadventuresdeluxe2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookwormadventuresdeluxe2.Utilities.DetailView;

/**
 * Holds the view for seeing details on a book in the borrowed tab
 * The user will be able to interact with borrow options on the book
 */
//TODO: add status specific buttons, functions, and labels
public class BorrowDetailViewFragment extends DetailView
{

    public BorrowDetailViewFragment()
    {
        // Required empty public constructor
    }

    public static BorrowDetailViewFragment newInstance(String param1, String param2)
    {
        BorrowDetailViewFragment fragment = new BorrowDetailViewFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.bookDetailView = inflater.inflate(R.layout.fragment_borrow_detail_view, null, false);
        ((TextView) bookDetailView.findViewById(R.id.app_header_title)).setText(R.string.borrow);

        // setup back button
        super.onCreateView(inflater, container, savedInstanceState);

        return bookDetailView;
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
        user.setText("TODO: get owner");

        ImageView statusCircle = bookDetailView.findViewById(R.id.book_details_status_circle);
        book.setStatusCircleColor(book.getStatus(), statusCircle);
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