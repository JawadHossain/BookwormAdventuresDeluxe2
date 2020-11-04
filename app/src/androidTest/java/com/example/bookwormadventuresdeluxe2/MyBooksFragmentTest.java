package com.example.bookwormadventuresdeluxe2;

import android.content.Context;
import android.content.res.Resources;
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

/**
 * Tests for the MyBooksFragment
 */
@RunWith(AndroidJUnit4.class)

public class MyBooksFragmentTest
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
     * Tests successfully adding a new book with no picture
     */
    @Test
    public void createBookSuccessfulTest()
    {
        /* Create the book */
        createTestBook(solo, resources);

        /* Assert we are in the mybooks activity */
        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), MyBooksActivity.class);

        /* Check that all the text is found (this means the book is shown in the booklist) */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_book_author)));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_book_isbn)));

        /* Click on the book title text to enter the books details */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Wait until we enter the my books fragment */
        solo.waitForFragmentById(R.layout.fragment_my_books_detail_view, (int) SHORT_WAIT);

        /* Check that all the text is found */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_book_author)));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_book_isbn)));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_book_description)));

        /* Check that the created book is marked as available */
        Assert.assertTrue(solo.searchText(resources.getString(R.string.available)));

        /* Return to the MyBooksActivity */
        solo.clickOnView(solo.getView(R.id.app_header_back_button));

        /* Check that we returned to the MyBooks Activity */
        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), MyBooksActivity.class);

        /* Delete the book */
        deleteTestBook(solo, resources);
    }

    /**
     * Tests trying to add a book when all fields are empty
     */
    @Test
    public void createBookEmptyError()
    {
        /* Click the add books button */
        solo.clickOnView(solo.getView(R.id.my_books_add_button));

        /* Wait for the edit books activity */
        solo.waitForActivity(AddOrEditBooksActivity.class, (int) SHORT_WAIT);

        /* Get the 3 edit texts that are required */
        EditText titleEditText = (EditText) solo.getView(R.id.title_edit_text);
        EditText authorEditText = (EditText) solo.getView(R.id.author_edit_text);
        EditText isbnEditText = (EditText) solo.getView(R.id.isbn_edit_text);

        /* Click the save button */
        solo.clickOnView(solo.getView(R.id.my_books_save_button));

        /* Assert that the cannot be blank error shown is at least shown somewhere */
        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));

        /* Assert that all 3 of the required fields have an error */
        Assert.assertNotNull(titleEditText.getError());
        Assert.assertNotNull(authorEditText.getError());
        Assert.assertNotNull(isbnEditText.getError());

        /* Fill in the isbn text so we are able to close the keyboard and exit */
        solo.enterText((EditText) solo.getView(R.id.isbn_edit_text), resources.getString(R.string.test_book_isbn));

        /* Return to the MyBooksActivity */
        solo.clickOnView(solo.getView(R.id.app_header_back_button));

        /* Wait for the MyBooks Activity */
        solo.waitForActivity(AddOrEditBooksActivity.class, (int) SHORT_WAIT);

        /* Check that the title is NOT shown. This means the book was not added */
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_description), 1, SHORT_WAIT));

        /* Check that we returned to the MyBooks Activity */
        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), MyBooksActivity.class);
    }

    /**
     * Tests deleting a book
     */
    @Test
    public void deleteBookTest()
    {
        /* Create the test book */
        createTestBook(solo, resources);

        /* Check that all the text is found (this means the book is shown in the booklist) */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_book_author)));
        Assert.assertTrue(solo.searchText(resources.getString(R.string.test_book_isbn)));

        /* Delete the test book */
        deleteTestBook(solo, resources);

        /* Check that none of the text is found (this means the book is not in the booklist) */
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_title), 1, SHORT_WAIT));
        Assert.assertFalse(solo.searchText(resources.getString(R.string.test_book_author)));
        Assert.assertFalse(solo.searchText(resources.getString(R.string.test_book_isbn)));
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
