package com.example.bookwormadventuresdeluxe2;

/**
 * A {@link Fragment} subclass for navbar menu search item. This fragment is responsible for
 * allowing the user to search for books.
 *
 * Outstanding Issues: Does not filter out owner's books and still displays all
 *                      borrowed and accepted books. Cannot search for a book
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

        // https://stackoverflow.com/questions/17670685/custom-searchview-whole-clickable-in-android/47826388
        searchView = (SearchView) view.findViewById(R.id.search_bar);
        searchView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchView.setIconified(false);
            }
        });
        return view;
    }

    // https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment#:~:text=Use%20getView%20%28%29%20or%20the%20View%20parameter%20from,method%29.%20With%20this%20you%20can%20call%20findViewById%20%28%29.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        UserCredentialAPI userCredentialApi = UserCredentialAPI.getInstance();

        Query availableBooksNotMine = rootRef.collection(getString(R.string.books_collection));
//                .whereNotEqualTo(getString(R.string.owner),
//                UserCredentialAPI.getInstance().getUsername())
//                .whereIn(getString(R.string.status),
//                Arrays.asList(getString(R.string.available), getString(R.string.requested)));

        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(availableBooksNotMine, Book.class)
                .build();

        searchBooksRecyclerView = (RecyclerView) view.findViewById(R.id.search_recycler_view);
        searchBooksRecyclerView.setHasFixedSize(true);

        searchBooksRecyclerLayoutManager = new LinearLayoutManager(this.getContext());
        searchBooksRecyclerView.setLayoutManager(searchBooksRecyclerLayoutManager);

        searchBooksRecyclerAdapter = new BookListAdapter(this.getContext(), options, R.id.search_books);
        searchBooksRecyclerView.setAdapter(searchBooksRecyclerAdapter);
    }

    // For listening to firebase for updates to the books list
    @Override
    public void onStart()
    {
        super.onStart();
        searchBooksRecyclerAdapter.startListening();
    }

    // Stops listening to the firebase on completion
    @Override
    public void onStop()
    {
        super.onStop();

        if (searchBooksRecyclerAdapter != null)
        {
            searchBooksRecyclerAdapter.stopListening();
        }
    }
}



