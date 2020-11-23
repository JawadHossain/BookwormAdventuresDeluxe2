package com.example.bookwormadventuresdeluxe2;

import android.content.res.Resources;

import com.robotium.solo.Solo;

import org.junit.Assert;

import static com.example.bookwormadventuresdeluxe2.TestUtils.NO_WAIT;
import static com.example.bookwormadventuresdeluxe2.TestUtils.SHORT_WAIT;

/**
 * Common testing functionality related to the filter menu. Assumes that the user is currently in a
 * fragment that has access to the show filter menu button.
 */
public class FilterMenuUtils
{
    private static int FILTER_MENU_BUTTON_INDEX = 0;

    /**
     * Opens the filter menu and filters by available books, then verifies that the available
     * test book is shown and none of the other test books are shown.
     */
    public static void verifyAvailableFilter(Solo solo, Resources resources)
    {
        /* Open the filter menu */
        solo.clickOnImageButton(FILTER_MENU_BUTTON_INDEX);
        solo.waitForFragmentById(R.layout.fragment_filter_menu, (int) SHORT_WAIT);

        /* Press the available button */
        solo.clickOnView(solo.getView(R.id.available_button));

        /* Check that the available title is shown but none of the others are */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_available_title), 1, SHORT_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_accepted_title), 1, NO_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_requested_title), 1, NO_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_borrowed_title), 1, NO_WAIT));

        /* Click on the book title */
        solo.clickOnText(resources.getString(R.string.test_book_available_title));

        /* Check that all the correct information is shown */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_available_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_available_author), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_available_description), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_isbn), 1, NO_WAIT));
    }

    /**
     * Opens the filter menu and filters by accepted books, then verifies that the accepted
     * test book is shown and none of the other test books are shown.
     */
    public static void verifyAcceptedFilter(Solo solo, Resources resources)
    {
        /* Open the filter menu */
        solo.clickOnImageButton(FILTER_MENU_BUTTON_INDEX);
        solo.waitForFragmentById(R.layout.fragment_filter_menu, (int) SHORT_WAIT);

        /* Press the accepted button */
        solo.clickOnView(solo.getView(R.id.accepted_button));

        /* Check that the accepted title is shown but none of the others are */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_accepted_title), 1, SHORT_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_available_title), 1, NO_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_requested_title), 1, NO_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_borrowed_title), 1, NO_WAIT));

        /* Click on the book title */
        solo.clickOnText(resources.getString(R.string.test_book_accepted_title));

        /* Check that all the correct information is shown */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_accepted_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_accepted_author), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_accepted_description), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_isbn), 1, NO_WAIT));
    }

    /**
     * Opens the filter menu and filters by requested books, then verifies that the requested
     * test book is shown and none of the other test books are shown.
     */
    public static void verifyRequestedFilter(Solo solo, Resources resources)
    {
        /* Open the filter menu */
        solo.clickOnImageButton(FILTER_MENU_BUTTON_INDEX);
        solo.waitForFragmentById(R.layout.fragment_filter_menu, (int) SHORT_WAIT);

        /* Press the requested button */
        solo.clickOnView(solo.getView(R.id.requested_button));

        /* Check that the requested title is shown but none of the others are */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_requested_title), 1, SHORT_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_available_title), 1, NO_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_accepted_title), 1, NO_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_borrowed_title), 1, NO_WAIT));

        /* Click on the book title */
        solo.clickOnText(resources.getString(R.string.test_book_requested_title));

        /* Check that all the correct information is shown */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_requested_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_requested_author), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_requested_description), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_isbn), 1, NO_WAIT));
    }

    /**
     * Opens the filter menu and filters by borrowed books, then verifies that the borrowed
     * test book is shown and none of the other test books are shown.
     */
    public static void verifyBorrowedFilter(Solo solo, Resources resources)
    {
        /* Open the filter menu */
        solo.clickOnImageButton(FILTER_MENU_BUTTON_INDEX);
        solo.waitForFragmentById(R.layout.fragment_filter_menu, (int) SHORT_WAIT);

        /* Press the borrowed button */
        solo.clickOnView(solo.getView(R.id.borrowed_button));

        /* Check that the borrowed title is shown but none of the others are */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_borrowed_title), 1, SHORT_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_available_title), 1, NO_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_accepted_title), 1, NO_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_requested_title), 1, NO_WAIT));

        /* Click on the book title */
        solo.clickOnText(resources.getString(R.string.test_book_borrowed_title));

        /* Check that all the correct information is shown */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_borrowed_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_borrowed_author), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_borrowed_description), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_isbn), 1, NO_WAIT));
    }

    /**
     * Opens the filter menu and filters by available books, then filters by all books to check that
     * all books are correctly shown when the all button is pressed.
     */
    public static void verifyAllFilter(Solo solo, Resources resources)
    {
        /* Open the filter menu */
        solo.clickOnImageButton(FILTER_MENU_BUTTON_INDEX);
        solo.waitForFragmentById(R.layout.fragment_filter_menu, (int) SHORT_WAIT);

        /* Press the available button */
        solo.clickOnView(solo.getView(R.id.available_button));

        /* Check that only the available title is shown */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_available_title), 1, SHORT_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_accepted_title), 1, NO_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_requested_title), 1, NO_WAIT));
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_borrowed_title), 1, NO_WAIT));

        filterByAll(solo);

        /* Check that all the books are shown */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_available_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_accepted_title), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_requested_title), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_borrowed_title), 1, NO_WAIT));
    }

    /**
     * Filters by all
     */
    public static void filterByAll(Solo solo)
    {
        /* Open the filter menu */
        solo.clickOnImageButton(FILTER_MENU_BUTTON_INDEX);
        solo.waitForFragmentById(R.layout.fragment_filter_menu, (int) SHORT_WAIT);

        /* Press the all button */
        solo.clickOnView(solo.getView(R.id.all_button));
    }
}
