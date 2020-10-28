package com.example.bookwormadventuresdeluxe2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.security.InvalidParameterException;

/**
 * Fragment class for the filter menu
 */
public class FilterMenu extends Fragment implements View.OnClickListener
{
    private BookListAdapter bookAdapter;
    private Query rootQuery;

    Button availableButton;
    Button requestedButton;
    Button acceptedButton;
    Button borrowedButton;
    Button allButton;

    public FilterMenu(BookListAdapter bookAdapter, Query rootQuery)
    {
        this.bookAdapter = bookAdapter;
        this.rootQuery = rootQuery;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        /* Inflate the layout for this fragment */
        View filterView = inflater.inflate(R.layout.fragment_filter_menu, container, false);

        /* Get all the buttons */
        availableButton = filterView.findViewById(R.id.available_button);
        requestedButton = filterView.findViewById(R.id.requested_button);
        acceptedButton = filterView.findViewById(R.id.accepted_button);
        borrowedButton = filterView.findViewById(R.id.borrowed_button);
        allButton = filterView.findViewById(R.id.all_button);

        /* Set all the buttons' listeners */
        availableButton.setOnClickListener(this);
        requestedButton.setOnClickListener(this);
        acceptedButton.setOnClickListener(this);
        borrowedButton.setOnClickListener(this);
        allButton.setOnClickListener(this);

        return filterView;
    }

    /**
     * Handle click on Profile Edit and SignOut button
     *
     * @param view View containing layout resources
     */
    @Override
    public void onClick(View view)
    {
        Query nextQuery;
        switch (view.getId())
        {
            case R.id.available_button:
                nextQuery = rootQuery.whereEqualTo(getString(R.string.status), getString(R.string.available));
                break;
            case R.id.requested_button:
                nextQuery = rootQuery.whereEqualTo(getString(R.string.status), getString(R.string.requested));
                break;
            case R.id.accepted_button:
                nextQuery = rootQuery.whereEqualTo(getString(R.string.status), getString(R.string.accepted));
                break;
            case R.id.borrowed_button:
                nextQuery = rootQuery.whereEqualTo(getString(R.string.status), getString(R.string.borrowed));
                break;
            case R.id.all_button:
                nextQuery = rootQuery;
                break;
            default:
                throw new InvalidParameterException("Unknown ID passed into Filter Menu onClick Listener");
        }

        /* Update the query in the recyclerView */
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(nextQuery, Book.class)
                .build();

        /* Update book results and close filter */
        bookAdapter.updateOptions(options);
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}