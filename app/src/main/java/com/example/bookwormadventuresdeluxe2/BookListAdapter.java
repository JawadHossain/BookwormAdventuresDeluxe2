package com.example.bookwormadventuresdeluxe2;

/**
 * BookListAdapter is a FirestoreRecycler data which acts as middleware between the books
 * on Firestore and the UI that displays them by providing view updaters and onClickListeners
 * for items in the RecyclerView.
 */

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookwormadventuresdeluxe2.Utilities.UserCredentialAPI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

// https://stackoverflow.com/questions/49277797/how-to-display-data-from-firestore-in-a-recyclerview-with-android
public class BookListAdapter extends FirestoreRecyclerAdapter<Book, BookListAdapter.BookListViewHolder>
{
    private Context context;
    private int caller;

    private static String search = "";

    // Reference to the views for each item
    public static class BookListViewHolder extends RecyclerView.ViewHolder
    {
        public TextView title;
        public TextView author;
        public TextView isbn;
        public ImageView statusCircle;
        public ImageView bookPhoto;
        public ConstraintLayout bookItemLayout;

        /**
         * A viewHolder for the books
         *
         * @param bookItemLayout The layout to reference
         */
        public BookListViewHolder(ConstraintLayout bookItemLayout)
        {
            super(bookItemLayout);
            this.title = (TextView) bookItemLayout.getViewById(R.id.book_item_title);
            this.author = (TextView) bookItemLayout.getViewById(R.id.book_item_author);
            this.isbn = (TextView) bookItemLayout.getViewById(R.id.book_item_isbn);
            this.statusCircle = (ImageView) bookItemLayout.getViewById(R.id.book_item_status);
            this.bookItemLayout = (ConstraintLayout) bookItemLayout.getViewById(R.id.book_item);
            this.bookPhoto = (ImageView) bookItemLayout.getViewById(R.id.book_item_image);
        }
    }

    public BookListAdapter(Context context, FirestoreRecyclerOptions options, int caller)
    {
        super(options);
        this.context = context;
        this.caller = caller;
    }

    /* Inflates the layout for individual items */
    public BookListAdapter.BookListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        ConstraintLayout bookItem = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        BookListViewHolder bookListViewHolder = new BookListViewHolder(bookItem);
        return bookListViewHolder;
    }

    /**
     * Launches the detail view for the item that was selected from the list. The layout of the detail
     * view depends on the context from which screen it was clicked so the function takes the view
     * as a parameter as well as the details of the book which was clicked
     *
     * @param bookDetailFragment
     * @param book
     * @param documentId
     * @return
     */
    private View.OnClickListener launchDetailView(DetailView bookDetailFragment, Book book, String documentId)
    {
        View.OnClickListener listener = new View.OnClickListener()
        {
            /* Handles a click on an item in the recycler view */
            @Override
            public void onClick(View v)
            {
                /* Opens the book in detail view */
                bookDetailFragment.onFragmentInteraction(book, documentId);

                ((MyBooksActivity) context).getSupportFragmentManager().beginTransaction()
                        .add(R.id.frame_container, bookDetailFragment, context.getResources().getString(R.string.book_detail_fragment))
                        .hide(ActiveFragmentTracker.activeFragment)
                        .commit();
            }
        };
        return listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull BookListViewHolder holder, int position, @NonNull Book book)
    {
        // Set the text, status and photo on the item view for each book
        String documentId = getSnapshots().getSnapshot(position).getId();
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        holder.isbn.setText(book.getIsbn());
        DetailView detailView;
        String user = UserCredentialAPI.getInstance().getUsername();
        book.setStatusCircleColor(holder.statusCircle, user);
        book.setPhoto(book, holder.bookPhoto);
        Bundle source = new Bundle();

        /* Set the onClickListener for the item depending on the context of the list */
        switch (this.caller)
        {
            case R.id.my_books:
                detailView = new MyBooksDetailViewFragment();
                break;
            case R.id.requests:
                detailView = new RequestDetailViewFragment();
                break;
            case R.id.borrow:
                source.putString(context.getString(R.string.book_click_source_fragment), context.getString(R.string.borrow));
                detailView = new BorrowDetailViewFragment();
                detailView.setArguments(source);
                break;
            case R.id.search_books:

                /* If book belongs to owner, OR not a search match while searching, hide it */
                if (book.getOwner().equals(UserCredentialAPI.getInstance().getUsername())
                    || (!this.search.equals("") && !searchMatch(book)))
                {
                    /* Hiding and setting the item margins to 0 */
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

                /* Else if not searching, OR searching and book is a search match, show book*/
                else if (this.search.equals("") || (!search.equals("") && searchMatch(book)))
                {
                    /*
                     * Resetting the item layout to original parameters
                     *
                     * Source: https://stackoverflow.com/questions/12728255/in-android-how-do-i-set-margins-in-dp-programmatically
                     * */
                    RecyclerView.LayoutParams searchLayoutParams =
                            new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    /* Original margins */
                    int margin = 32;
                    searchLayoutParams.setMargins(margin, margin, margin, margin);
                    holder.itemView.setVisibility(View.VISIBLE);
                    holder.itemView.setLayoutParams(searchLayoutParams);
                    source.putString(context.getString(R.string.book_click_source_fragment), context.getString(R.string.search_title));
                }
                detailView = new BorrowDetailViewFragment();
                detailView.setArguments(source);
                break;
            default:
                throw new IllegalArgumentException();
        }

        holder.itemView.setOnClickListener(launchDetailView(detailView, book, documentId));
    }

    /**
     * Setter for search term of the adapter
     *
     * @param searchText Current search term
     */
    public void setSearch(String searchText)
    {
        this.search = searchText;
    }

    /**
     * Getter for current search term of the adapter
     *
     * @return String result of this adapter's current search term
     */
    public String getSearch()
    {
        return this.search;
    }

    /**
     * Checker for searching through books
     *
     * @param book Book to be searched for search string
     * @return boolean result if book is a match
     */
    public boolean searchMatch(Book book)
    {
        if (book.getTitle().toLowerCase().contains(this.search.toLowerCase())
            || book.getAuthor().toLowerCase().contains(this.search.toLowerCase())
            || book.getDescription().toLowerCase().contains(this.search.toLowerCase())
            || book.getIsbn().contains(this.search))
        {
            return true;
        }
        return false;
    }
}
