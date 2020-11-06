package com.example.bookwormadventuresdeluxe2;

/**
 * Tests related to the Requests/Borrow fragments. This includes tests related to viewing
 * and navigating requests on owned books and requests on books of others
 */

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
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

public class RequestsFragmentTest
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

        /* Sign in with the test account */
        signIn(solo, resources);
    }

    /**
     * Ensures that the proper list is visible depending on which tab is clicked
     */
    @Test
    public void requestsTabFragmentSwitchTest()
    {
        /* Navigate to the search fragment */
        solo.clickOnView(solo.getView(R.id.requests_menu_item));

        /* We should enter the requests fragment */
        solo.waitForFragmentById(R.layout.fragment_requests, (int) SHORT_WAIT);

        /* Requests list should be visible */
        Assert.assertTrue(solo.getView(R.id.requests_recycler_view).getVisibility() == View.VISIBLE);

        /* Click on the borrowed tab */
        solo.clickOnText(resources.getString(R.string.borrow));

        /* Borrowed list should be visible */
        Assert.assertTrue(solo.getView(R.id.borrow_recycler_view).getVisibility() == View.VISIBLE);
    }

    /**
     * Closes the activity after each test
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        signOut(solo, resources);
        solo.finishOpenedActivities();
    }
}
