package com.example.bookwormadventuresdeluxe2.Utilities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.Book;
import com.example.bookwormadventuresdeluxe2.R;

/**
 * abstract class representing all the DetailView fragments
 * Implements parts of methods common to all DetailViews
 */
public abstract class DetailView extends Fragment
{
    protected Book selectedBook;
    protected String selectedBookId;
    protected ImageButton backButton;
    protected View bookDetailView;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Make the desired custom header buttons visible and set their click listeners
        this.backButton = bookDetailView.findViewById(R.id.app_header_back_button);
        this.backButton.setVisibility(View.VISIBLE);
        this.backButton.setOnClickListener(this::onBackClick);

        if (this.selectedBook != null)
        {
            updateView(this.selectedBook);
        }

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
        TextView title = bookDetailView.findViewById(R.id.book_details_title);
        title.setText(book.getTitle());

        TextView authorName = bookDetailView.findViewById(R.id.book_details_author);
        authorName.setText(book.getAuthor());

        TextView description = bookDetailView.findViewById(R.id.book_details_description);
        description.setText(book.getDescription());

        TextView isbn = bookDetailView.findViewById(R.id.book_details_isbn);
        isbn.setText(book.getIsbn());
    }

    /**
     * Receives and sets the selected book from the calling fragment
     *
     * @param selectedBook The book that was selected from calling fragment
     */
    public void onFragmentInteraction(Book selectedBook, String documentId)
    {
        this.selectedBook = selectedBook;
        this.selectedBookId = documentId;
    }

    /**
     * Takes the user back to the main Requests screen
     *
     * @param v The view that was clicked on
     */
    abstract public void onBackClick(View v);
}
