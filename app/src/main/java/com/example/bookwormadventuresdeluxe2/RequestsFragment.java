package com.example.bookwormadventuresdeluxe2;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
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

    View view;
    Button toggle;
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
        view.findViewById(R.id.app_header_filter_button).setVisibility(View.VISIBLE);

        GradientDrawable shape = new GradientDrawable();
        toggle.setBackground(shape);
        toggle.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        //TODO: actually query the books
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection(getString(R.string.books_collection)).orderBy("title");

        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();

        requestsRecyclerView = (RecyclerView) view.findViewById(R.id.requests_recycler_view);
        requestsRecyclerView.setHasFixedSize(true);

        requestsRecyclerLayoutManager = new LinearLayoutManager(this.getContext());
        requestsRecyclerView.setLayoutManager(requestsRecyclerLayoutManager);

        requestsRecyclerAdapter = new BookListAdapter(this.getContext(), options, R.id.requests);
        requestsRecyclerView.setAdapter(requestsRecyclerAdapter);

        Query query2 = rootRef.collection(getString(R.string.books_collection)).orderBy("title");

        FirestoreRecyclerOptions<Book> options2 = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query2, Book.class)
                .build();

        borrowRecyclerView = (RecyclerView) view.findViewById(R.id.borrow_recycler_view);
        borrowRecyclerView.setHasFixedSize(true);

        borrowRecyclerLayoutManager = new LinearLayoutManager(this.getContext());
        borrowRecyclerView.setLayoutManager(borrowRecyclerLayoutManager);

        borrowRecyclerAdapter = new BookListAdapter(this.getContext(), options2, R.id.borrow);
        borrowRecyclerView.setAdapter(borrowRecyclerAdapter);
        borrowRecyclerView.setVisibility(View.INVISIBLE);
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

    // User wants to change tab, swap the lists and labels
    @Override
    public void onClick(View view)
    {
        ConstraintLayout layout = this.view.findViewById(R.id.requests);
        ConstraintSet cons = new ConstraintSet();
        cons.clone(layout);

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
