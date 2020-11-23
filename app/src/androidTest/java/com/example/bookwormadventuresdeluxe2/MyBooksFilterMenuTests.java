package com.example.bookwormadventuresdeluxe2;

import android.content.Context;
import android.content.res.Resources;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.example.bookwormadventuresdeluxe2.FilterMenuUtils.filterByAll;
import static com.example.bookwormadventuresdeluxe2.FilterMenuUtils.verifyAcceptedFilter;
import static com.example.bookwormadventuresdeluxe2.FilterMenuUtils.verifyAllFilter;
import static com.example.bookwormadventuresdeluxe2.FilterMenuUtils.verifyAvailableFilter;
import static com.example.bookwormadventuresdeluxe2.FilterMenuUtils.verifyBorrowedFilter;
import static com.example.bookwormadventuresdeluxe2.FilterMenuUtils.verifyRequestedFilter;
import static com.example.bookwormadventuresdeluxe2.TestUtils.SHORT_WAIT;
import static com.example.bookwormadventuresdeluxe2.TestUtils.signIn;
import static com.example.bookwormadventuresdeluxe2.TestUtils.signOut;

/**
 * Tests for the filter menu in the MyBooks fragment
 */
public class MyBooksFilterMenuTests
{
    private Solo solo;

    private Context appContext;
    private Resources resources;

    private TestUtils.BookManager bookManager;

    private int BACK_BUTTON_INDEX = 0;

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

        /* Setup the book manager */
        bookManager = new TestUtils.BookManager(solo, resources);

        /* Create the test books */
        bookManager.addTestBooks();

        /* Sign in with the test account */
        signIn(solo, resources);

        /* In addition to waiting for the MyBooksActivity, we need to wait for the fragment */
        solo.waitForFragmentById(R.layout.fragment_my_books, (int) SHORT_WAIT);
    }

    /**
     * Tests filtering for books with the available status
     */
    @Test
    public void filterByAvailableTest()
    {
        /* Verify the behavior of the accepted filter button */
        verifyAvailableFilter(this.solo, this.resources);

        /* Click back button to return to my books recycler view fragment */
        solo.clickOnImageButton(BACK_BUTTON_INDEX);
        solo.waitForFragmentById(R.layout.fragment_my_books, (int) SHORT_WAIT);

        filterByAll(solo);
    }

    /**
     * Tests filtering for books with the accepted status
     */
    @Test
    public void filterByAcceptedTest()
    {
        /* Verify the behavior of the accepted filter button */
        verifyAcceptedFilter(this.solo, this.resources);

        /* Click back button to return to my books recycler view fragment */
        solo.clickOnImageButton(BACK_BUTTON_INDEX);
        solo.waitForFragmentById(R.layout.fragment_my_books, (int) SHORT_WAIT);

        filterByAll(solo);
    }

    /**
     * Tests filtering for books with the requested status
     */
    @Test
    public void filterByRequestedTest()
    {
        /* Verify the behavior of the requested filter button */
        verifyRequestedFilter(this.solo, this.resources);

        /* Click back button to return to my books recycler view fragment */
        solo.clickOnImageButton(BACK_BUTTON_INDEX);
        solo.waitForFragmentById(R.layout.fragment_my_books, (int) SHORT_WAIT);

        filterByAll(solo);
    }

    /**
     * Tests filtering for books with the borrowed status
     */
    @Test
    public void filterByBorrowedTest()
    {
        /* Verify the behavior of the borrowed filter button */
        verifyBorrowedFilter(this.solo, this.resources);

        /* Click back button to return to my books recycler view fragment */
        solo.clickOnImageButton(BACK_BUTTON_INDEX);
        solo.waitForFragmentById(R.layout.fragment_my_books, (int) SHORT_WAIT);

        filterByAll(solo);
    }

    /**
     * Tests filtering by all
     */
    @Test
    public void filterByAllTest()
    {
        /* Verify the behavior of the all filter button */
        verifyAllFilter(this.solo, this.resources);
    }

    /**
     * Closes the activity after each test
     *
     * @throws Exception
     */
    @After

    public void tearDown() throws Exception
    {
        bookManager.deleteTestBooks();
        signOut(solo, resources);
        solo.finishOpenedActivities();
    }
}
