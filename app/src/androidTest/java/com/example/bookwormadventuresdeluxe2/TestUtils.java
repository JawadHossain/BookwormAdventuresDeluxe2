package com.example.bookwormadventuresdeluxe2;

/**
 * A collection of utilities to be used across all tests
 */

import android.content.res.Resources;
import android.widget.EditText;

import com.robotium.solo.Solo;

public class TestUtils
{
    /* Default Robotium delay is wayyyyy too long. Use this one to speed up tests */
    public static long SHORT_WAIT = 5000;

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
     * Deletes the test book created by createTestBook. Assumes we are in the MyBooksActtivity
     */
    public static void deleteTestBook(Solo solo, Resources resources)
    {
        /* Assert we are in the MyBooks activity */
        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), MyBooksActivity.class);

        /* Click on the book title text to enter the books details */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Wait until we enter the my books fragment */
        solo.waitForFragmentById(R.layout.fragment_my_books_detail_view, (int) SHORT_WAIT);

        /* Click the edit button */
        solo.clickOnView(solo.getView(R.id.app_header_edit_button));

        /* Wait until we enter the edit books activity */
        solo.waitForActivity(AddOrEditBooksActivity.class, (int) SHORT_WAIT);

        /* Click on the delete button */
        solo.clickOnButton(resources.getString(R.string.delete_book));

        /* Wait for the MyBooks activity after deleting the book */
        solo.waitForActivity(MyBooksActivity.class, (int) SHORT_WAIT);
    }
}
