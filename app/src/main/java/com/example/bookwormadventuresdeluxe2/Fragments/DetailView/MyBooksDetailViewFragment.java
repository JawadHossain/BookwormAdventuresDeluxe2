package com.example.bookwormadventuresdeluxe2.Fragments.DetailView;

/**
 * This fragment holds the view for viewing the details of a book after it is clicked
 * on from the RecyclerView in MyBooks. From here, the user can view the details of their
 * book and edit it from a button in the header.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.Activities.AddOrEditBooksActivity;
import com.example.bookwormadventuresdeluxe2.Models.Book;
import com.example.bookwormadventuresdeluxe2.R;
import com.example.bookwormadventuresdeluxe2.Utilities.ActiveFragmentTracker;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.InvalidParameterException;

public class MyBooksDetailViewFragment extends DetailView
{
    private ImageButton editButton;

    public MyBooksDetailViewFragment()
    {
        // Required empty public constructor
    }

    public static MyBooksDetailViewFragment newInstance(String param1, String param2)
    {
        MyBooksDetailViewFragment fragment = new MyBooksDetailViewFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.bookDetailView = inflater.inflate(R.layout.fragment_my_books_detail_view, null, false);

        // Make the desired custom header buttons visible and set their click listeners
        this.editButton = bookDetailView.findViewById(R.id.app_header_edit_button);
        this.editButton.setVisibility(View.VISIBLE);
        this.editButton.setOnClickListener(this::onEditClick);

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
        status.setText(book.getAugmentStatus(UserCredentialAPI.getInstance().getUsername()).toString());

        TextView user = bookDetailView.findViewById(R.id.book_details_borrower);

        /* Set the status text to display the borrower if it is borrowed or accepted */
        switch (book.getStatus())
        {
            case Available:
                status.setText(getString(R.string.available));
                break;
            case Requested:
                status.setText(getString(R.string.requested));
                break;
            case Accepted:
                status.setText(getString(R.string.request_detail_requested));
                user.setText(this.selectedBook.getRequesters().get(0));
                clickUsername(user, book.getRequesters().get(0), this);
                break;
            case bPending:
            case Borrowed:
                status.setText(getString(R.string.request_detail_borrowed));
                user.setText(this.selectedBook.getRequesters().get(0));
                clickUsername(user, book.getRequesters().get(0), this);
                break;
            case rPending:
                status.setText(getString(R.string.request_detail_return));
                user.setText(this.selectedBook.getRequesters().get(0));
                clickUsername(user, book.getRequesters().get(0), this);
                break;
            default:
                throw new InvalidParameterException("Invalid book status in RequestDetailView updateView");
        }
    }

    /**
     * Takes the user back to the main MyBooks screen
     *
     * @param v The view that was clicked on
     */
    public void onBackClick(View v)
    {
        Fragment myBooksFragment = ActiveFragmentTracker.activeFragment;
        Bundle args = new Bundle();
        args.putSerializable("editedBook", this.selectedBook);
        myBooksFragment.setArguments(args);
        getFragmentManager().beginTransaction().remove(this).show(myBooksFragment).commit();
    }

    /**
     * Allows the user to edit the selected book
     *
     * @param v The view that was clicked on
     */
    public void onEditClick(View v)
    {
        Intent intent = new Intent(getActivity(), AddOrEditBooksActivity.class);
        intent.putExtra("requestCode", AddOrEditBooksActivity.EDIT_BOOK);
        intent.putExtra("bookToEdit", this.selectedBook);
        intent.putExtra("documentId", this.selectedBookId);
        startActivityForResult(intent, AddOrEditBooksActivity.EDIT_BOOK);
    }

    /**
     * Called by AddOrEditBooksActivity when user presses save or delete on edit screen
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        getActivity();
        if (requestCode == AddOrEditBooksActivity.EDIT_BOOK)
        {
            /* Save was pressed */
            if (resultCode == AddOrEditBooksActivity.EDIT_BOOK)
            {
                /* Get the book that was edited and its new values */
                this.selectedBook = (Book) intent.getSerializableExtra("EditedBook");
                updateView(this.selectedBook);

                /* Update the book in firebase */
                DocumentReference bookDocument = FirebaseFirestore
                        .getInstance()
                        .collection(getString(R.string.books_collection))
                        .document(this.selectedBookId);

                bookDocument.update(getResources().getString(R.string.firestore_title), this.selectedBook.getTitle());
                bookDocument.update(getResources().getString(R.string.firestore_author), this.selectedBook.getAuthor());
                bookDocument.update(getResources().getString(R.string.firestore_description), this.selectedBook.getDescription());
                bookDocument.update(getResources().getString(R.string.firestore_isbn), this.selectedBook.getIsbn());
                bookDocument.update(getResources().getString(R.string.firestore_imageUrl), this.selectedBook.getImageUrl());
            }
            else if (resultCode == AddOrEditBooksActivity.DELETE_BOOK) /* Delete was pressed */
            {
                /* Simulate back click to exit this fragment since the book no longer exists */
                this.onBackClick(getView());
            }
            /* Throw no exception here because if the back button is pressed we will have
               no return code.
             */
        }
    }
}