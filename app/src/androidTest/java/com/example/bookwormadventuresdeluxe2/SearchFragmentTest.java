package com.example.bookwormadventuresdeluxe2;

/**
 * Tests related to the Search Fragment. This includes tests related to searching and
 * viewing a book on the search page
 */

import android.content.Context;
import android.content.res.Resources;
import android.widget.SearchView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.bookwormadventuresdeluxe2.Activities.LoginActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.example.bookwormadventuresdeluxe2.TestUtils.NO_WAIT;
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
    private SearchView searchView;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    private static int BACK_BUTTON_INDEX = 0;

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
     * Tests searching for a book using multiple inputs
     */
    @Test
    public void searchTest()
    {
        /* Search for title */
        enterSearchTextUtil(resources.getString(R.string.test_book_title));

        /* Search for author */
        enterSearchTextUtil(resources.getString(R.string.test_book_author));

        /* Search for description */
        enterSearchTextUtil(resources.getString(R.string.test_book_description));

        /* Search for isbn */
        enterSearchTextUtil(resources.getString(R.string.test_book_isbn));
    }

    /**
     * Goes to SearchFragment
     */
    public void goToSearch()
    {
        /* Navigate to the search fragment */
        solo.clickOnView(solo.getView(R.id.search_menu_item));

        /* Wait until we enter the search fragment */
        solo.waitForFragmentById(R.layout.fragment_search, (int) SHORT_WAIT);
    }

    /**
     * Ensure the added book from secondary test account shows in search fragment for
     * main test account
     */
    public void bookAppearsInSearchUtil()
    {
        goToSearch();

        /* Check that all the text for the newly added book is found */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_author), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_isbn), 1, NO_WAIT));
    }

    /**
     * Enters a search term into the search bar and clicks the result
     *
     * @param searchText Text to be searched
     */
    public void enterSearchTextUtil(String searchText)
    {
        bookAppearsInSearchUtil();

        searchView = (SearchView) solo.getView(R.id.search_bar);
        solo.clickOnView(searchView);
        searchView.setQuery(searchText, false);
        solo.sendKey(Solo.ENTER);
        solo.clickOnText(resources.getString(R.string.test_book_title));
        solo.waitForFragmentById(R.layout.fragment_borrow_detail_view, (int) SHORT_WAIT);

        assertSearchBookClick();
    }

    /**
     * Asserts that the searched book was clicked and properly displays
     */
    public void assertSearchBookClick()
    {
        /* Check that all the text for the newly added book is found */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account1_username), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_title), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_author), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_isbn), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_isbn), 1, NO_WAIT));
    }

    /**
     * Ensures that when a book is clicked from the search page, the detail view
     * displays the correct owner
     */
    @Test
    public void bookHasCorrectOwnerTest()
    {
        goToSearch();

        /* Click on newly added book */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Ensure the book displays the proper owner and owned by text */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.owned_by), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account2_username), 1, NO_WAIT));
    }

    /**
     * Ensures that the request and cancel request buttons works
     */
    @Test
    public void requestAndCancelRequestButtonTest()
    {
        goToSearch();

        /* Click on newly added book */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Ensure the requests button is visible */
        Assert.assertTrue(solo.searchButton(resources.getString(R.string.request_book)));

        /* Request book */
        solo.clickOnText(resources.getString(R.string.request_book));

        /* Click on book */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Ensure that the cancel request button is visible */
        Assert.assertTrue(solo.searchButton(resources.getString(R.string.cancel_request)));

        /* Cancel request*/
        solo.clickOnText(resources.getString(R.string.cancel_request));

        /* Click on book */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Ensure that the request button is visible */
        Assert.assertTrue(solo.searchButton(resources.getString(R.string.request_book)));
    }

    /**
     * Ensures that the correct profile is in view when the username of the
     * book owner is clicked
     */
    @Test
    public void viewOwnerProfileTest()
    {
        goToSearch();

        /* Click on newly added book */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Click on the username of the owner */
        solo.clickOnText(resources.getString(R.string.test_account2_username));

        /* Check that all the text is found for the accounts details */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account2_username), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account2_email), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account2_phone), 1, NO_WAIT));

        /* Return to the MyBooksActivity */
        solo.clickOnImageButton(BACK_BUTTON_INDEX);
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