package com.example.bookwormadventuresdeluxe2;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookwormadventuresdeluxe2.Utilities.DetailView;

import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.Utilities.Status;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This fragment holds the view for viewing the details of a book after it is clicked
 * on from the RecyclerView in MyBooks. From here, the user can view the details of their
 * book and edit it from a button in the header.
 */
public class MyBooksDetailViewFragment extends DetailView
{
    ImageButton editButton;

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
        if (book.getStatus() == Status.Borrowed)
        {
            status.setText(book.getStatus().toString() + " " + getString(R.string.detail_join));
            TextView user = bookDetailView.findViewById(R.id.book_details_borrower);
            book.setBorrower("TODO"); // TODO: remove when borrower is set for real
            user.setText(book.getBorrower());
        }
        else
        {
            status.setText(book.getStatus().toString());
        }

        ImageView statusCircle = bookDetailView.findViewById(R.id.book_details_status_circle);
        book.setStatusCircleColor(book.getStatus(), statusCircle);
    }

    /**
     * Takes the user back to the main MyBooks screen
     *
     * @param v The view that was clicked on
     */
    public void onBackClick(View v)
    {
        MyBooksFragment myBooksFragment = new MyBooksFragment();
        Bundle args = new Bundle();
        args.putSerializable("editedBook", this.selectedBook);
        myBooksFragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.frame_container, myBooksFragment).commit();
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

    /* Called by AddOrEditBooksActivity when user presses save or delete on edit screen */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (requestCode == AddOrEditBooksActivity.EDIT_BOOK)
        {
            /* Save was pressed */
            if (resultCode == AddOrEditBooksActivity.EDIT_BOOK)
            {
                /* Get the book that was edited and its new values */
                this.selectedBook = (Book) data.getSerializableExtra("EditedBook");
                updateView(this.selectedBook);

                /* Update the book in firebase */
                DocumentReference bookDocument = FirebaseFirestore
                        .getInstance()
                        .collection(getString(R.string.books_collection))
                        .document(this.selectedBookId);

                bookDocument.update("title", this.selectedBook.getTitle());
                bookDocument.update("author", this.selectedBook.getAuthor());
                bookDocument.update("description", this.selectedBook.getDescription());
                bookDocument.update("isbn", this.selectedBook.getIsbn());
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