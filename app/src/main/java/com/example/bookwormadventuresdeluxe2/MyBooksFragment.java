package com.example.bookwormadventuresdeluxe2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A {@link Fragment} subclass for navbar menu item 1.
 */
public class MyBooksFragment extends Fragment
{
    private RecyclerView myBooksRecyclerView;
    private BookListAdapter myBooksRecyclerAdapter;
    private RecyclerView.LayoutManager myBooksRecyclerLayoutManager;
    private FilterMenu filterMenu;

    ImageButton notificationButton;
    ImageButton filterButton;

    public MyBooksFragment()
    {
        // Required empty constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View myBooksView = inflater.inflate(R.layout.fragment_my_books, container, false);

        // Set visibility of desired custom header buttons
        myBooksView.findViewById(R.id.app_header_filter_button).setVisibility(View.VISIBLE);
        myBooksView.findViewById(R.id.app_header_scan_button).setVisibility(View.VISIBLE);

        /* Setup Filter button */
        this.filterButton = myBooksView.findViewById(R.id.app_header_filter_button);
        this.filterButton.setVisibility(View.VISIBLE);
        this.filterButton.setOnClickListener(this::onFilterClick);

        // TODO: Setup scan button

        /* Setup notification button */
        this.notificationButton = myBooksView.findViewById(R.id.app_header_notification_button);
        this.notificationButton.setVisibility(View.VISIBLE);
        this.notificationButton.setOnClickListener(this::onNotificationClick);

        return myBooksView;
    }

    // https://stackoverflow.com/questions/6495898/findviewbyid-in-fragment#:~:text=Use%20getView%20%28%29%20or%20the%20View%20parameter%20from,method%29.%20With%20this%20you%20can%20call%20findViewById%20%28%29.
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
        Query query = rootRef.collection(getString(R.string.books_collection)).orderBy("title");

        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();

        myBooksRecyclerView = (RecyclerView) view.findViewById(R.id.search_recycler_view);
        myBooksRecyclerView.setHasFixedSize(true);

        myBooksRecyclerLayoutManager = new LinearLayoutManager(this.getContext());
        myBooksRecyclerView.setLayoutManager(myBooksRecyclerLayoutManager);

        myBooksRecyclerAdapter = new BookListAdapter(this.getContext(), options, R.id.my_books);
        myBooksRecyclerView.setAdapter(myBooksRecyclerAdapter);

        /* Initialize the filterMenu. This will update the queries using the adapter */
        this.filterMenu = new FilterMenu(myBooksRecyclerAdapter, query);

        FloatingActionButton btn = (FloatingActionButton) getView().findViewById(R.id.my_books_add_button);
        btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // this function is called from the MyBooksFragment
                Intent intent = new Intent(getActivity(), AddOrEditBooksActivity.class);
                startActivityForResult(intent, AddOrEditBooksActivity.ADD_BOOK);
            }
        });
    }

    // For listening to firebase for updates to the books list
    @Override
    public void onStart()
    {
        super.onStart();
        myBooksRecyclerAdapter.startListening();
    }

    // Stops listening to the firebase on completion
    @Override
    public void onStop()
    {
        super.onStop();

        if (myBooksRecyclerAdapter != null)
        {
            myBooksRecyclerAdapter.stopListening();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intentData)
    {
        super.onActivityResult(requestCode, resultCode, intentData);
        getActivity();
        if (requestCode == AddOrEditBooksActivity.ADD_BOOK && resultCode == Activity.RESULT_OK)
        {
            Book newBook = (Book) intentData.getSerializableExtra("NewBook");

            // Get the data from the new book and add it to the database
            Map<String, Object> data = new HashMap<>();
            data.put("owner", newBook.getOwner());
            data.put("title", newBook.getTitle());
            data.put("author", newBook.getAuthor());
            data.put("description", newBook.getDescription());
            data.put("isbn", newBook.getIsbn());
            data.put("status", newBook.getStatus());
            data.put("pickUpAddress", "");
            data.put("requesters", new ArrayList<String>());

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            rootRef.collection(getString(R.string.books_collection)).add(data);
            myBooksRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private void onNotificationClick(View view)
    {
        NotificationFragment notificationFragment = new NotificationFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame_container, notificationFragment).commit();
    }

    /**
     * Launch the filter menu fragment when the filter button is clicked
     *
     * @param view
     */
    private void onFilterClick(View view)
    {
        /* https://stackoverflow.com/questions/43207043/check-if-fragment-is-currently-visible-or-no/45059794 */
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
}