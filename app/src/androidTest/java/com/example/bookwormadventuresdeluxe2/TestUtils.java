package com.example.bookwormadventuresdeluxe2;

/**
 * A collection of utilities to be used across all tests
 */

import android.content.res.Resources;
import android.widget.EditText;

import com.example.bookwormadventuresdeluxe2.Utilities.Status;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

public class TestUtils
{
    /* Default Robotium delay is wayyyyy too long. Use this one to speed up tests */
    public static long SHORT_WAIT = 5000;
    public static long NO_WAIT = 0;
    private static int EDIT_BUTTON_INDEX = 1;

    /**
     * Signs out of test account after login. Assumes we are in the MyBooksActivity.
     */
    public static void signOut(Solo solo, Resources resources)
    {
        /* Assert we are in the MyBooks activity */
        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), MyBooksActivity.class);

        solo.clickOnText(resources.getString(R.string.navbar_text_label_4));

        solo.waitForText(resources.getString(R.string.sign_out), 1, SHORT_WAIT);

        solo.clickOnButton(resources.getString(R.string.sign_out));

        /* Wait for the login activity after signing out */
        solo.waitForActivity(LoginActivity.class, (int) SHORT_WAIT);
    }

    /**
     * Signs in with the test account. Assumes we are in the LoginActivity.
     */
    public static void signIn(Solo solo, Resources resources)
    {
        /* Assert we are in the LoginIn activity */
        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), LoginActivity.class);

        /* Get the email and password EditTexts */
        EditText emailText = (EditText) solo.getView(R.id.login_email);
        EditText passwordText = (EditText) solo.getView(R.id.login_password);

        solo.enterText(emailText, resources.getString(R.string.test_account1_email));
        solo.enterText(passwordText, resources.getString(R.string.test_account1_password));
        solo.clickOnButton(resources.getString(R.string.login));

        /* Wait for the MyBooks activity after signing in */
        solo.waitForActivity(MyBooksActivity.class, (int) SHORT_WAIT);
    }

    /**
     * Signs in with the given test account. Assumes we are in the LoginActivity.
     */
    public static void signIn(Solo solo, Resources resources, String email, String password)
    {
        /* Assert we are in the LoginIn activity */
        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), LoginActivity.class);

        /* Get the email and password EditTexts */
        EditText emailText = (EditText) solo.getView(R.id.login_email);
        EditText passwordText = (EditText) solo.getView(R.id.login_password);

        solo.enterText(emailText, email);
        solo.enterText(passwordText, password);
        solo.clickOnButton(resources.getString(R.string.login));

        /* Wait for the MyBooks activity after signing in */
        solo.waitForActivity(MyBooksActivity.class, (int) SHORT_WAIT);
    }

    /**
     * Creates a new book using the test account. Assumes we are in the MyBooksActivity.
     */
    public static void createTestBook(Solo solo, Resources resources)
    {
        /* Assert we are in the MyBooks activity */
        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), MyBooksActivity.class);

        /* Click the add books button */
        solo.clickOnView(solo.getView(R.id.my_books_add_button));

        /* Wait for the edit books activity */
        solo.waitForActivity(AddOrEditBooksActivity.class, (int) SHORT_WAIT);

        /* Enter all the fields */
        solo.enterText((EditText) solo.getView(R.id.title_edit_text), resources.getString(R.string.test_book_title));
        solo.enterText((EditText) solo.getView(R.id.author_edit_text), resources.getString(R.string.test_book_author));
        solo.enterText((EditText) solo.getView(R.id.description_edit_text), resources.getString(R.string.test_book_description));
        solo.enterText((EditText) solo.getView(R.id.isbn_edit_text), resources.getString(R.string.test_book_isbn));

        /* Click the save button */
        solo.clickOnView(solo.getView(R.id.my_books_save_button));

        /* Wait for the MyBooks activity after adding a book */
        solo.waitForActivity(MyBooksActivity.class, (int) SHORT_WAIT);
    }

    /**
     * Deletes the test book with the given title. Assumes we are in the MyBooksActivity
     */
    public static void deleteTestBook(Solo solo, Resources resources, int title)
    {
        /* Assert we are in the MyBooks activity */
        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), MyBooksActivity.class);

        /* Click on the book title text to enter the books details */
        solo.clickOnText(resources.getString(title));

        /* Wait until we enter the my books fragment */
        solo.waitForFragmentById(R.layout.fragment_my_books_detail_view, (int) SHORT_WAIT);

        /* Click the edit button */
        solo.clickOnImageButton(EDIT_BUTTON_INDEX);

        /* Wait until we enter the edit books activity */
        solo.waitForActivity(AddOrEditBooksActivity.class, (int) SHORT_WAIT);

        /* Click on the delete button */
        solo.clickOnButton(resources.getString(R.string.delete_book));

        /* Wait for the MyBooks activity after deleting the book */
        solo.waitForActivity(MyBooksActivity.class, (int) SHORT_WAIT);
    }

    public static class BookManager
    {
        private Book availableBook;
        private Book acceptedBook;
        private Book requestedBook;
        private Book borrowedBook;
        private Resources resources;
        private Solo solo;

        public BookManager(Solo solo, Resources resources)
        {
            this.solo = solo;
            this.resources = resources;

            /* Private book objects so we can easily add and remove from firebase */
            availableBook = new Book(resources.getString(R.string.test_account1_username),
                    resources.getString(R.string.test_book_available_title),
                    resources.getString(R.string.test_book_available_author),
                    resources.getString(R.string.test_book_available_description),
                    resources.getString(R.string.test_book_isbn),
                    Status.Available,
                    "");
            acceptedBook = new Book(resources.getString(R.string.test_account1_username),
                    resources.getString(R.string.test_book_accepted_title),
                    resources.getString(R.string.test_book_accepted_author),
                    resources.getString(R.string.test_book_accepted_description),
                    resources.getString(R.string.test_book_isbn),
                    Status.Accepted,
                    "");
            /* Set the second test account as the borrower */
            acceptedBook.addRequester(resources.getString(R.string.test_account2_username));
            requestedBook = new Book(resources.getString(R.string.test_account1_username),
                    resources.getString(R.string.test_book_requested_title),
                    resources.getString(R.string.test_book_requested_author),
                    resources.getString(R.string.test_book_requested_description),
                    resources.getString(R.string.test_book_isbn),
                    Status.Requested,
                    "");
            /* Set the second test account as the requester */
            requestedBook.addRequester(resources.getString(R.string.test_account2_username));

            borrowedBook = new Book(resources.getString(R.string.test_account1_username),
                    resources.getString(R.string.test_book_borrowed_title),
                    resources.getString(R.string.test_book_borrowed_author),
                    resources.getString(R.string.test_book_borrowed_description),
                    resources.getString(R.string.test_book_isbn),
                    Status.Borrowed,
                    "");
            /* Set the second test account as the borrower */
            borrowedBook.addRequester(resources.getString(R.string.test_account2_username));
        }

        /**
         * Insert one book of each status into firebase.
         */
        public void addTestBooks()
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference collectionReference = db.collection(resources.getString(R.string.books_collection));
            collectionReference.add(this.availableBook);
            collectionReference.add(this.acceptedBook);
            collectionReference.add(this.requestedBook);
            collectionReference.add(this.borrowedBook);

            /* Sleep a small amount of time to let the UI update */
            solo.sleep((int) SHORT_WAIT);
        }

        /**
         * Remove one book of each status from firebase
         */
        public void deleteTestBooks()
        {
            /* We need to go through the UI because we can't actually get the documentID directly */
            deleteTestBook(this.solo, this.resources, R.string.test_book_available_title);
            deleteTestBook(this.solo, this.resources, R.string.test_book_accepted_title);
            deleteTestBook(this.solo, this.resources, R.string.test_book_requested_title);
            deleteTestBook(this.solo, this.resources, R.string.test_book_borrowed_title);
        }

        /**
         * Insert the requested book into firebase
         */
        public void addRequestedBook()
        {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final CollectionReference collectionReference = db.collection(resources.getString(R.string.books_collection));
            collectionReference.add(this.requestedBook);
        }

        /**
         * Delete the requested book from firebase
         */
        public void deleteRequestedBook()
        {
            /* We need to go through the UI because we can't actually get the documentID directly */
            deleteTestBook(this.solo, this.resources, R.string.test_book_requested_title);
        }
    }

}
