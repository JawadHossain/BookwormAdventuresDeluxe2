package com.example.bookwormadventuresdeluxe2;

/**
 * A {@link Fragment} subclass for navbar menu search item. This fragment is responsible for
 * allowing the user to search for books.
 *
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class SearchFragment extends Fragment
{
    private RecyclerView searchBooksRecyclerView;
    private BookListAdapter searchBooksRecyclerAdapter;
    private RecyclerView.LayoutManager searchBooksRecyclerLayoutManager;

    private MaterialTextView appHeaderText;
    private SearchView searchView;

    public SearchFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        /* Set title */
        appHeaderText = view.findViewById(R.id.app_header_title);
        appHeaderText.setText(R.string.search_title);

        return view;
    }

    // https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment#:~:text=Use%20getView%20%28%29%20or%20the%20View%20parameter%20from,method%29.%20With%20this%20you%20can%20call%20findViewById%20%28%29.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        UserCredentialAPI userCredentialApi = UserCredentialAPI.getInstance();

        Query availableBooks = rootRef.collection(getString(R.string.books_collection))
                .whereIn(getString(R.string.status), Arrays.asList(getString(R.string.available), getString(R.string.requested)));

        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(availableBooks, Book.class)
                .build();

        searchBooksRecyclerView = (RecyclerView) view.findViewById(R.id.search_recycler_view);
        searchBooksRecyclerView.setHasFixedSize(true);

        searchBooksRecyclerLayoutManager = new LinearLayoutManager(this.getContext());
        searchBooksRecyclerView.setLayoutManager(searchBooksRecyclerLayoutManager);

        searchBooksRecyclerAdapter = new BookListAdapter(this.getContext(), options, R.id.search_books);
        searchBooksRecyclerView.setAdapter(searchBooksRecyclerAdapter);

        // Source: https://stackoverflow.com/questions/17670685/custom-searchview-whole-clickable-in-android/47826388
        searchView = (SearchView) view.findViewById(R.id.search_bar);

        // Source: https://stackoverflow.com/questions/19645366/searchview-customize-close-icon
        int clearSearchBtnID = searchView.getContext()
                                    .getResources()
                                    .getIdentifier("android:id/search_close_btn", null, null);

        ImageView clearSearchBtn = view.findViewById(clearSearchBtnID);

        searchClickListener(searchView, options);
        searchTextListener(searchView, options);
        searchClear(clearSearchBtn, options, view);
    }

    /**
     * For listening to firebase for updates to the books list
     */
    @Override
    public void onStart()
    {
        super.onStart();
        searchBooksRecyclerAdapter.startListening();
    }

    /**
     * Stops listening to firebase collection
     */
    @Override
    public void onStop()
    {
        super.onStop();

        if (searchBooksRecyclerAdapter != null)
        {
            searchBooksRecyclerAdapter.stopListening();
        }
    }

    /**
     * Functionality for clicking the SearchView bar
     *
     * @param searchView Textfield and icon to enter the search term
     * @param options Object to populate the FireStoreRecycler adapter
     */
    private void searchClickListener(SearchView searchView, FirestoreRecyclerOptions<Book> options)
    {
        searchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchView.setIconified(false);
            }
        });
    }

    /**
     * Functionality for submitting a search string
     *
     * @param searchView Textfield and icon to enter the search term
     * @param options Object to populate the BookListAdapter
     */
    private void searchTextListener(SearchView searchView, FirestoreRecyclerOptions<Book> options)
    {
        /*
         * Source: https://stackoverflow.com/questions/9327826/searchviews-oncloselistener-doesnt-work
         * */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String searchText)
            {
                searchBooksRecyclerAdapter.setSearch(searchText);
                searchBooksRecyclerAdapter.updateOptions(options);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText)
            {
                return false;
            }
        });
    }

    /**
     * Handles functionality for pressing 'X' to clear SearchView text
     *
     * @param clearSearchBtn Image of the icon
     * @param options Object to populate the FireStoreRecycler adapter
     * @param view Current view
     */
    private void searchClear(ImageView clearSearchBtn, FirestoreRecyclerOptions<Book> options, View view)
    {
        clearSearchBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (searchBooksRecyclerAdapter.getSearch().length() != 0)
                {
                    searchBooksRecyclerAdapter.setSearch("");
                    searchBooksRecyclerAdapter.updateOptions(options);
                }
                searchView.setQuery("", false);
                searchView.clearFocus();
            }
        });
    }
}



