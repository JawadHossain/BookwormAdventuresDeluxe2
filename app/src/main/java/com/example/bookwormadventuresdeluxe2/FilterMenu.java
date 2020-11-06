package com.example.bookwormadventuresdeluxe2;

/**
 * Fragment class for the filter menu. This fragment is responsible for handling events related
 * to clicking buttons inside the "filter by" dropdown menu.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.security.InvalidParameterException;
import java.util.Arrays;

public class FilterMenu extends Fragment implements View.OnClickListener
{
    private BookListAdapter bookAdapter;
    private Query rootQuery;
    private int caller;

    private Button availableButton;
    private Button requestedButton;
    private Button acceptedButton;
    private Button borrowedButton;
    private Button allButton;

    /**
     * Public constructor for the menu
     *
     * @param bookAdapter The bookAdapter that we will need to update with the specialized query
     * @param rootQuery   The rootQuery to build our query off of
     * @param caller      The resource id that called this constructor
     */
    public FilterMenu(BookListAdapter bookAdapter, Query rootQuery, int caller)
    {
        this.bookAdapter = bookAdapter;
        this.rootQuery = rootQuery;
        this.caller = caller;
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
     * Handle clicking on any of the buttons in the FilterMenu
     *
     * @param view View containing layout resources
     */
    @Override
    public void onClick(View view)
    {
        Query nextQuery;
        switch (this.caller)
        {
            case R.id.my_books:
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
                        nextQuery = rootQuery.whereIn(getString(R.string.status), Arrays.asList(
                                getString(R.string.bPending),
                                getString(R.string.borrowed),
                                getString(R.string.rPending)));
                        break;
                    case R.id.all_button:
                        nextQuery = rootQuery;
                        break;
                    default:
                        throw new InvalidParameterException("Unknown ID passed into Filter Menu onClick Listener");
                }
                break;
            case R.id.requests:
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
                        nextQuery = FirebaseFirestore.getInstance().collection(getString(R.string.books_collection)).whereEqualTo(
                                getString(R.string.owner), UserCredentialAPI.getInstance().getUsername()).whereIn(getString(R.string.status), Arrays.asList(
                                getString(R.string.bPending),
                                getString(R.string.rPending)));
                        break;
                    case R.id.all_button:
                        nextQuery = rootQuery;
                        break;
                    default:
                        throw new InvalidParameterException("Unknown ID passed into Filter Menu onClick Listener");
                }
                break;
            case R.id.borrow:
                switch (view.getId())
                {
                    case R.id.available_button:
                        nextQuery = rootQuery.whereEqualTo(getString(R.string.status), getString(R.string.rPending));
                        break;
                    case R.id.requested_button:
                        nextQuery = rootQuery.whereEqualTo(getString(R.string.status), getString(R.string.requested));
                        break;
                    case R.id.accepted_button:
                        nextQuery = rootQuery.whereIn(getString(R.string.status), Arrays.asList(
                                getString(R.string.bPending),
                                getString(R.string.accepted)));
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
                break;
            default:
                throw new InvalidParameterException("Unknown caller ID to FilterMenu");
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