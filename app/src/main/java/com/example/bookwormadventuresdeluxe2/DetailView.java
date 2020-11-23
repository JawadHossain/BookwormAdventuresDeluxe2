package com.example.bookwormadventuresdeluxe2;

/**
 * DetailView is the parent view for the array of different screens which may appear when you
 * click on a book depending on the context of where it was clicked from. It houses functionality
 * for updating the views and passing data between the caller and the detail view.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.bookwormadventuresdeluxe2.Book;
import com.example.bookwormadventuresdeluxe2.FirebaseUserGetSet;
import com.example.bookwormadventuresdeluxe2.ProfileFragment;
import com.example.bookwormadventuresdeluxe2.R;
import com.example.bookwormadventuresdeluxe2.UserProfileObject;
import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;

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

        ImageView statusCircle = bookDetailView.findViewById(R.id.book_details_status_circle);
        book.setStatusCircleColor(statusCircle, UserCredentialAPI.getInstance().getUsername());

        ImageView bookPhoto = bookDetailView.findViewById(R.id.book_details_image);
        book.setPhoto(book, bookPhoto);
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

    /**
     * Opens user profile on TextView click
     *
     * @param textView TextView in view
     * @param username Requester's username
     */
    public void clickUsername(TextView textView, String username)
    {
        textView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /* Pulling UserProfileObject from database */
                FirebaseUserGetSet.getUser(username, new FirebaseUserGetSet.UserCallback()
                {
                    @Override
                    public void onCallback(UserProfileObject userObject)
                    {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(getString(R.string.profile_object), userObject);
                        ProfileFragment profileFragment = new ProfileFragment();
                        profileFragment.setArguments(bundle);
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                                .replace(R.id.frame_container, profileFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                });
            }
        });
    }
}
