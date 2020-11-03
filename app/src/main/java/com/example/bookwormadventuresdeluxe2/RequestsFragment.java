package com.example.bookwormadventuresdeluxe2;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.FileFilter;
import java.util.Arrays;

/**
 * Holds a list of books that the current user owns that require require action
 * to proceed with the borrow/return process, and another list of books that the
 * user has requested or are currently in the process of borrowing. From here the
 * user may click on the books to view status specific tasks
 *
 * A {@link Fragment} subclass for navbar menu item 2.
 */
public class RequestsFragment extends Fragment implements View.OnClickListener
{
    private RecyclerView requestsRecyclerView;
    private BookListAdapter requestsRecyclerAdapter;
    private RecyclerView.LayoutManager requestsRecyclerLayoutManager;

    private RecyclerView borrowRecyclerView;
    private BookListAdapter borrowRecyclerAdapter;
    private RecyclerView.LayoutManager borrowRecyclerLayoutManager;
    private FilterMenu borrowFilterMenu;
    private FilterMenu requestFilterMenu;

    View view;
    Button toggle;
    ImageButton filterButton;
    TextView current;
    MaterialTextView appHeaderText;
    boolean borrow;

    public RequestsFragment()
    {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_requests, container, false);
        current = view.findViewById(R.id.current);
        toggle = view.findViewById(R.id.toggle_btn);
        borrow = false;

        /* Set title */
        appHeaderText = view.findViewById(R.id.app_header_title);
        appHeaderText.setText(R.string.requests_title);

        /* Show filter button */
        this.filterButton = this.view.findViewById(R.id.app_header_filter_button);
        this.filterButton.setVisibility(View.VISIBLE);
        this.filterButton.setOnClickListener(this::onFilterClick);

        GradientDrawable shape = new GradientDrawable();
        toggle.setBackground(shape);
        toggle.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query requests = rootRef.collection(getString(R.string.books_collection)).whereEqualTo(
                getString(R.string.owner), UserCredentialAPI.getInstance().getUsername()).whereIn(getString(R.string.status), Arrays.asList(
                getString(R.string.requested),
                getString(R.string.accepted),
                getString(R.string.bPending),
                getString(R.string.rPending)));

        FirestoreRecyclerOptions<Book> requestsOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(requests, Book.class)
                .build();

        requestsRecyclerView = (RecyclerView) view.findViewById(R.id.requests_recycler_view);
        requestsRecyclerView.setHasFixedSize(true);

        requestsRecyclerLayoutManager = new LinearLayoutManager(this.getContext());
        requestsRecyclerView.setLayoutManager(requestsRecyclerLayoutManager);

        requestsRecyclerAdapter = new BookListAdapter(this.getContext(), requestsOptions, R.id.requests);
        requestsRecyclerView.setAdapter(requestsRecyclerAdapter);

        Query borrow = rootRef.collection(getString(R.string.books_collection)).whereArrayContains(
                getString(R.string.requesters), UserCredentialAPI.getInstance().getUsername());

        FirestoreRecyclerOptions<Book> borrowOptions = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(borrow, Book.class)
                .build();

        borrowRecyclerView = (RecyclerView) view.findViewById(R.id.borrow_recycler_view);
        borrowRecyclerView.setHasFixedSize(true);

        borrowRecyclerLayoutManager = new LinearLayoutManager(this.getContext());
        borrowRecyclerView.setLayoutManager(borrowRecyclerLayoutManager);

        borrowRecyclerAdapter = new BookListAdapter(this.getContext(), borrowOptions, R.id.borrow);
        borrowRecyclerView.setAdapter(borrowRecyclerAdapter);

        borrowRecyclerView.setVisibility(View.INVISIBLE);

        /* Initialize the filterMenu. This will update the queries using the adapter */
        this.requestFilterMenu = new FilterMenu(requestsRecyclerAdapter, requests, R.id.requests);
        this.borrowFilterMenu = new FilterMenu(borrowRecyclerAdapter, borrow, R.id.borrow);

        Bundle arg = this.getArguments();
        if (arg != null && arg.getBoolean(getString(R.string.borrow)))
        {
            onClick(view);
        }
    }

    // For listening to firebase for updates to the books list
    @Override
    public void onStart()
    {
        super.onStart();
        requestsRecyclerAdapter.startListening();
        borrowRecyclerAdapter.startListening();
    }

    // Stops listening to the firebase on completion
    @Override
    public void onStop()
    {
        super.onStop();

        if (requestsRecyclerAdapter != null)
        {
            requestsRecyclerAdapter.stopListening();
        }
        if (borrowRecyclerAdapter != null)
        {
            borrowRecyclerAdapter.stopListening();
        }
    }

    /**
     * Launch the filter menu fragment when the filter button is clicked
     *
     * @param view
     */
    private void onFilterClick(View view)
    {
        FilterMenu filterMenu;
        if (this.borrow)
        {
            filterMenu = this.borrowFilterMenu;
        }
        else
        {
            filterMenu = this.requestFilterMenu;
        }

        View fragmentRootView = filterMenu.getView();
        if (fragmentRootView == null)
        {
            /* Fragment was hidden, show it */
            getFragmentManager().beginTransaction().add(R.id.frame_container, filterMenu).commit();
        }
        else
        {
            /* Fragment is shown, hide it */
            getFragmentManager().beginTransaction().remove(filterMenu).commit();
        }
    }

    // User wants to change tab, swap the lists and labels
    @Override
    public void onClick(View view)
    {
        ConstraintLayout layout = this.view.findViewById(R.id.requests);
        ConstraintSet cons = new ConstraintSet();
        cons.clone(layout);

        FilterMenu filterMenu;
        if (this.borrow)
        {
            filterMenu = this.borrowFilterMenu;
        }
        else
        {
            filterMenu = this.requestFilterMenu;
        }
        View fragmentRootView = filterMenu.getView();
        if (fragmentRootView != null)
        {
            /* Fragment is shown, hide it */
            getFragmentManager().beginTransaction().remove(filterMenu).commit();
        }

        if (borrow)
        { // currently in borrow
            cons.setVisibility(R.id.borrow_recycler_view, View.INVISIBLE);
            cons.setVisibility(R.id.requests_recycler_view, View.VISIBLE);

            current.setText(getResources().getString(R.string.my_requests));
            toggle.setText(getResources().getString(R.string.borrow));

            cons.connect(R.id.toggle_btn, ConstraintSet.END, R.id.requests, ConstraintSet.END);
            cons.connect(R.id.toggle_btn, ConstraintSet.START, R.id.current, ConstraintSet.END);
            cons.connect(R.id.current, ConstraintSet.END, R.id.toggle_btn, ConstraintSet.START);
            cons.connect(R.id.current, ConstraintSet.START, R.id.requests, ConstraintSet.START);
        }
        else
        { // currently in my requests
            cons.setVisibility(R.id.requests_recycler_view, View.INVISIBLE);
            cons.setVisibility(R.id.borrow_recycler_view, View.VISIBLE);

            current.setText(getResources().getString(R.string.borrow));
            toggle.setText(getResources().getString(R.string.my_requests));

            cons.connect(R.id.current, ConstraintSet.END, R.id.requests, ConstraintSet.END);
            cons.connect(R.id.current, ConstraintSet.START, R.id.toggle_btn, ConstraintSet.END);
            cons.connect(R.id.toggle_btn, ConstraintSet.END, R.id.current, ConstraintSet.START);
            cons.connect(R.id.toggle_btn, ConstraintSet.START, R.id.requests, ConstraintSet.START);
        }

        cons.applyTo(layout);
        borrow = !borrow;
    }
}
