package com.example.bookwormadventuresdeluxe2;

/**
 * Tests related to the Search Fragment. This includes tests related to searching and
 * viewing a book on the search page
 */

import android.content.Context;
import android.content.res.Resources;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.example.bookwormadventuresdeluxe2.TestUtils.SHORT_WAIT;
import static com.example.bookwormadventuresdeluxe2.TestUtils.createTestBook;
import static com.example.bookwormadventuresdeluxe2.TestUtils.deleteTestBook;
import static com.example.bookwormadventuresdeluxe2.TestUtils.signIn;
import static com.example.bookwormadventuresdeluxe2.TestUtils.signOut;

@RunWith(AndroidJUnit4.class)

public class SearchFragmentTest
{
    private Solo solo;

    private Context appContext;
    private Resources resources;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * Runs before each test and creates solo instance.
     *
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception
    {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

        /* Gets context for app resources */
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        /* Gets resource files */
        resources = appContext.getResources();

        /* Sign in with the second test account and add a book so it will appear
         * in the search screen for the main test account */
        signIn(solo, resources, resources.getString(R.string.test_account2_email), resources.getString(R.string.test_account2_password));
        createTestBook(solo, resources);
        signOut(solo, resources);

        /* Sign back in with the main test account */
        signIn(solo, resources);
    }

    /**
     * Ensure the added book from secondary test account shows in search fragment for
     * main test account
     */
    @Test
    public void bookAppearsInSearchTest()
    {
        /* Navigate to the search fragment */
        solo.clickOnView(solo.getView(R.id.search_menu_item));

        /* Wait until we enter the search fragment */
        solo.waitForFragmentById(R.layout.fragment_search, (int) SHORT_WAIT);

        /* Check that all the text for the newly added book is found */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_book_author)));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_book_isbn)));

        /* Go back to detail view */
        solo.clickOnView(solo.getView(R.id.app_header_back_button));
    }

    /**
     * Ensures that when a book is clicked from the search page, the detail view
     * displays the correct owner
     */
    @Test
    public void bookHasCorrectOwnerTest()
    {
        /* Navigate to the search fragment */
        solo.clickOnView(solo.getView(R.id.search_menu_item));

        /* Wait until we enter the search fragment */
        solo.waitForFragmentById(R.layout.fragment_search, (int) SHORT_WAIT);

        /* Click on newly added book */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Ensure the book displays the proper owner and owned by text */
        Assert.assertTrue(solo.searchText(resources.getString(R.string.owned_by)));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_account2_username)));
    }

    /**
     * Ensures that the detail view for the search book contains the button
     * to request the book
     */
    @Test
    public void requestsButtonVisibleTest()
    {
        /* Navigate to the search fragment */
        solo.clickOnView(solo.getView(R.id.search_menu_item));

        /* Wait until we enter the search fragment */
        solo.waitForFragmentById(R.layout.fragment_search, (int) SHORT_WAIT);

        /* Click on newly added book */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Ensure the requests button is visible */
        Assert.assertTrue(solo.searchButton(resources.getString(R.string.request_book)));
    }

    /**
     * Ensures that the correct profile is in view when the username of the
     * book owner is clicked
     */
    @Test
    public void viewOwnerProfileTest()
    {
        /* Navigate to the search fragment */
        solo.clickOnView(solo.getView(R.id.search_menu_item));

        /* Wait until we enter the search fragment */
        solo.waitForFragmentById(R.layout.fragment_search, (int) SHORT_WAIT);

        /* Click on newly added book */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Click on the username of the owner */
        solo.clickOnText(resources.getString(R.string.test_account2_username));

        /* Check that all the text is found for the accounts details */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account2_username), 1, SHORT_WAIT));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_account2_email)));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_account2_phone)));

        /* Return to the MyBooksActivity */
        solo.clickOnView(solo.getView(R.id.app_header_back_button));
    }

    /**
     * Closes the activity after each test
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        /* Sign out of the main test account */
        signOut(solo, resources);

        /* Sign back into the secondary test account and delete the added book */
        signIn(solo, resources, resources.getString(R.string.test_account2_email), resources.getString(R.string.test_account2_password));
        deleteTestBook(solo, resources, R.string.test_book_title);
        signOut(solo, resources);

        solo.finishOpenedActivities();
    }
}