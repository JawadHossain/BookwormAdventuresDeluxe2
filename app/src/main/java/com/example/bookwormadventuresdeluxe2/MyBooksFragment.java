package com.example.bookwormadventuresdeluxe2;

/**
 * MyBooksFragment holds the list of books belonging to the user who is signed in. From here,
 * the user may click on a book to view its details in MyBooksDetailViewFragment or perform
 * other actions such as filtering the list, scanning a book to open its details or view
 * their notifications.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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

    private ImageButton notificationButton;
    private ImageButton filterButton;
    private ImageButton scanButton;

    private Query booksOfCurrentUser;

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

        /* Setup Filter button */
        this.scanButton = myBooksView.findViewById(R.id.app_header_scan_button);
        this.scanButton.setVisibility(View.VISIBLE);
        this.scanButton.setOnClickListener(this::onScanClick);

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
        UserCredentialAPI userCredentialApi = UserCredentialAPI.getInstance();
        booksOfCurrentUser = rootRef.collection(getString(R.string.books_collection)).whereEqualTo("owner", userCredentialApi.getUsername());

        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(booksOfCurrentUser, Book.class)
                .build();

        myBooksRecyclerView = (RecyclerView) view.findViewById(R.id.search_recycler_view);
        myBooksRecyclerView.setHasFixedSize(true);

        myBooksRecyclerLayoutManager = new LinearLayoutManager(this.getContext());
        myBooksRecyclerView.setLayoutManager(myBooksRecyclerLayoutManager);

        myBooksRecyclerAdapter = new BookListAdapter(this.getContext(), options, R.id.my_books);
        myBooksRecyclerView.setAdapter(myBooksRecyclerAdapter);

        /* Initialize the filterMenu. This will update the queries using the adapter */
        this.filterMenu = new FilterMenu(myBooksRecyclerAdapter, booksOfCurrentUser, R.id.my_books);

        FloatingActionButton addBookButton = (FloatingActionButton) getView().findViewById(R.id.my_books_add_button);
        addBookButton.setOnClickListener(new View.OnClickListener()
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
            data.put(getResources().getString(R.string.firestore_owner), newBook.getOwner());
            data.put(getResources().getString(R.string.firestore_title), newBook.getTitle());
            data.put(getResources().getString(R.string.firestore_author), newBook.getAuthor());
            data.put(getResources().getString(R.string.firestore_description), newBook.getDescription());
            data.put(getResources().getString(R.string.firestore_isbn), newBook.getIsbn());
            data.put(getResources().getString(R.string.firestore_status), newBook.getStatus());
            data.put(getResources().getString(R.string.firestore_pick_up_address), "");
            data.put(getResources().getString(R.string.firestore_requesters), new ArrayList<String>());
            data.put(getResources().getString(R.string.firestore_imageUrl), newBook.getImageUrl());

            FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
            rootRef.collection(getString(R.string.books_collection)).add(data);
            myBooksRecyclerAdapter.notifyDataSetChanged();
        }
        else if (requestCode == IntentIntegrator.REQUEST_CODE)
        {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intentData);
            if (result != null)
            {
                if (result.getContents() != null)
                {
                    String barcode = result.getContents();
                    processIsbnScan(barcode);
                }
            }
        }
    }

    /**
     * Queries Firebase for the user's books with the same isbn as the one scanned
     * https://stackoverflow.com/questions/50650224/wait-until-firestore-data-is-retrieved-to-launch-an-activity/50680352
     *
     * @param barcode
     */
    private void processIsbnScan(String barcode)
    {
        Query query = booksOfCurrentUser.whereEqualTo("isbn", barcode);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
        {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task)
            {
                // The key is the documentID, value is the Book object
                HashMap<String, Book> results = new HashMap<String, Book>();
                if (task.isSuccessful())
                {
                    // get every book that matches the isbn
                    for (QueryDocumentSnapshot document : task.getResult())
                    {
                        results.put(document.getId(), document.toObject(Book.class));
                    }

                    switch (results.size())
                    {
                        case 0:
                            Toast.makeText(getActivity(), "No books match the scanned ISBN.", Toast.LENGTH_LONG).show();
                            break;
                        case 1:
                            String documentId = results.keySet().iterator().next();
                            Book bookToView = results.get(documentId);
                            MyBooksDetailViewFragment bookDetailFragment = new MyBooksDetailViewFragment();

                            // Open the detailed view if the book exists
                            bookDetailFragment.onFragmentInteraction(bookToView, documentId);
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.frame_container, bookDetailFragment).commit();
                            break;
                        default:
                            /*
                             * https://eclass.srv.ualberta.ca/mod/forum/discuss.php?d=1504014
                             * The link states that the user will only have one copy per book
                             * So this option does not have to be addressed yet.
                             * Potential TODO: make a popup that allows the user to pick the book they want to open
                             */
                            Toast.makeText(getActivity(), "Multiple books found.", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Scan failed. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Listener for when the notification button is clicked
     *
     * @param view
     */
    private void onNotificationClick(View view)
    {
        NotificationFragment notificationFragment = new NotificationFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame_container, notificationFragment).commit();
    }

    /**
     * Method is called when the user clicks on the scan icon in the MyBooks fragment
     *
     * @param view
     */
    private void onScanClick(View view)
    {
        IntentIntegrator integrator = new IntentIntegrator(getActivity());
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
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