package com.example.bookwormadventuresdeluxe2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This fragment holds the view for viewing the details of a book after it is clicked
 * on from the RecyclerView in MyBooks. From here, the user can view the details of their
 * book and edit it from a button in the header.
 */
public class MyBooksDetailViewFragment extends Fragment
{
    ImageButton backButton;
    ImageButton editButton;

    Book selectedBook;

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
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View bookDetailView = inflater.inflate(R.layout.fragment_my_books_detail_view, null, false);

        // Make the desired custom header buttons visible and set their click listeners
        this.editButton = bookDetailView.findViewById(R.id.app_header_edit_button);
        this.editButton.setVisibility(View.VISIBLE);
        this.editButton.setOnClickListener(this::onEditClick);

        this.backButton = bookDetailView.findViewById(R.id.app_header_back_button);
        this.backButton.setVisibility(View.VISIBLE);
        this.backButton.setOnClickListener(this::onBackClick);

        // Set the content based on the book that was selected
        TextView title = bookDetailView.findViewById(R.id.book_details_title);
        title.setText(this.selectedBook.getTitle());

        TextView authorName = bookDetailView.findViewById(R.id.book_details_author);
        authorName.setText(this.selectedBook.getAuthor());

        TextView description = bookDetailView.findViewById(R.id.book_details_description);
        description.setText(this.selectedBook.getDescription());

        TextView status = bookDetailView.findViewById(R.id.book_details_status);
        status.setText(this.selectedBook.getStatus().toString());

        TextView isbn = bookDetailView.findViewById(R.id.book_details_isbn);
        isbn.setText(this.selectedBook.getIsbn());

        ImageView statusCircle = bookDetailView.findViewById(R.id.book_details_status_circle);
        this.selectedBook.setStatusCircleColor(this.selectedBook.getStatus(), statusCircle);

        return bookDetailView;
    }

    /**
     * Takes the user back to the main MyBooks screen
     *
     * @param v The view that was clicked on
     */
    public void onBackClick(View v)
    {
        MyBooksFragment myBooksFragment = new MyBooksFragment();
        getFragmentManager().beginTransaction().replace(R.id.frame_container, myBooksFragment).commit();
    }

    /**
     * Allows the user to edit the selected book
     *
     * @param v The view that was clicked on
     */
    public void onEditClick(View v)
    {
        // TODO: Once Richmond is done add book this will be much easier
    }

    /**
     * Receives and sets the selected book from the calling fragment, MyBooksFragment
     *
     * @param selectedBook The book that was selected from MyBooksFragment
     */
    public void onFragmentInteraction(Book selectedBook)
    {
        this.selectedBook = selectedBook;
    }
}