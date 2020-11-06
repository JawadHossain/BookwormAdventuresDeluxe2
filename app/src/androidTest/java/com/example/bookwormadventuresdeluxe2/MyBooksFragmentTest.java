package com.example.bookwormadventuresdeluxe2;

/**
 * Tests related to the MyBooksFragment. This includes tests related to adding, editing, deleting,
 * or filtering for books in the MyBooksFragment.
 */

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
        deleteTestBook(solo, resources,  R.string.test_book_title);
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

        /* Assert that there cannot be blank error shown is at least shown somewhere */
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
        deleteTestBook(solo, resources, R.string.test_book_title);

        /* Check that none of the text is found (this means the book is not in the booklist) */
        Assert.assertFalse(solo.waitForText(resources.getString(R.string.test_book_title), 1, SHORT_WAIT));
        Assert.assertFalse(solo.searchText(resources.getString(R.string.test_book_author)));
        Assert.assertFalse(solo.searchText(resources.getString(R.string.test_book_isbn)));
    }

    /**
     * Tests successfully editing a book
     */
    @Test
    public void editBookSuccessfulTest()
    {
        /* Create the test book */
        createTestBook(solo, resources);

        /* Open test book detail view */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Wait until we enter the my books fragment */
        solo.waitForFragmentById(R.layout.fragment_my_books_detail_view, (int) SHORT_WAIT);

        /* Click on edit book icon to launch Edit book activity*/
        solo.clickOnView(solo.getView(R.id.app_header_edit_button));
        solo.waitForActivity(AddOrEditBooksActivity.class, (int) SHORT_WAIT);

        /* Get the 4 edit texts */
        EditText titleEditText = (EditText) solo.getView(R.id.title_edit_text);
        EditText authorEditText = (EditText) solo.getView(R.id.author_edit_text);
        EditText descriptionEditText = (EditText) solo.getView(R.id.description_edit_text);
        EditText isbnEditText = (EditText) solo.getView(R.id.isbn_edit_text);

        /* Clear the edit texts*/
        titleEditText.setText("");
        authorEditText.setText("");
        descriptionEditText.setText("");
        isbnEditText.setText("");

        /* Update all the fields */
        solo.enterText((EditText) solo.getView(R.id.title_edit_text), resources.getString(R.string.test_book_edit_title));
        solo.enterText((EditText) solo.getView(R.id.author_edit_text), resources.getString(R.string.test_book_edit_author));
        solo.enterText((EditText) solo.getView(R.id.description_edit_text), resources.getString(R.string.test_book_edit_description));
        solo.enterText((EditText) solo.getView(R.id.isbn_edit_text), resources.getString(R.string.test_book_edit_isbn));

        /* Click save button to return to book detail view */
        solo.clickOnView(solo.getView(R.id.my_books_save_button));
        solo.waitForFragmentById(R.layout.fragment_my_books_detail_view, (int) SHORT_WAIT);

        /* Check book details have been updated */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_edit_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_edit_author), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_edit_description), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_edit_isbn), 1, SHORT_WAIT));

        /* Click back button to return to my books recycler view fragment */
        solo.clickOnView(solo.getView(R.id.app_header_back_button));
        solo.waitForFragmentById(R.layout.fragment_my_books, (int) SHORT_WAIT);

        /* Check book details have been updated in the my books recycler view */
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_edit_title), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_edit_author), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_book_edit_isbn), 1, SHORT_WAIT));

        /* Delete the test book */
        deleteTestBook(solo, resources, R.string.test_book_edit_title);
    }

    /**
     * Test Empty field error handling on book edit
     */
    @Test
    public void editBookEmptyFieldTest()
    {
        /* Create the test book */
        createTestBook(solo, resources);

        /* Open test book detail view */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Wait until we enter the my books fragment */
        solo.waitForFragmentById(R.layout.fragment_my_books_detail_view, (int) SHORT_WAIT);

        /* Click on edit book icon to launch Edit book activity*/
        solo.clickOnView(solo.getView(R.id.app_header_edit_button));
        solo.waitForActivity(AddOrEditBooksActivity.class, (int) SHORT_WAIT);

        /* Get the 4 edit texts */
        EditText titleEditText = (EditText) solo.getView(R.id.title_edit_text);
        EditText authorEditText = (EditText) solo.getView(R.id.author_edit_text);
        EditText descriptionEditText = (EditText) solo.getView(R.id.description_edit_text);
        EditText isbnEditText = (EditText) solo.getView(R.id.isbn_edit_text);

        /* Clear the edit texts*/
        titleEditText.setText("");
        authorEditText.setText("");
        descriptionEditText.setText("");
        isbnEditText.setText("");

        /* Click save button  */
        solo.clickOnView(solo.getView(R.id.my_books_save_button));

        /* Assert cannot be blank error is shown somewhere */
        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));

        /* Assert that all 3 of the required fields have an error */
        Assert.assertNotNull(titleEditText.getError());
        Assert.assertNotNull(authorEditText.getError());
        Assert.assertNotNull(isbnEditText.getError());

        /* Click on the delete button */
        solo.clickOnButton(resources.getString(R.string.delete_book));

        /* Wait for the MyBooks activity after deleting the book */
        solo.waitForActivity(MyBooksActivity.class, (int) SHORT_WAIT);
    }

    /**
     * Test Invalid ISBN format on book edit
     */
    @Test
    public void editBookISBNFormatTest()
    {
        /* Create the test book */
        createTestBook(solo, resources);

        /* Open test book detail view */
        solo.clickOnText(resources.getString(R.string.test_book_title));

        /* Wait until we enter the my books fragment */
        solo.waitForFragmentById(R.layout.fragment_my_books_detail_view, (int) SHORT_WAIT);

        /* Click on edit book icon to launch Edit book activity*/
        solo.clickOnView(solo.getView(R.id.app_header_edit_button));
        solo.waitForActivity(AddOrEditBooksActivity.class, (int) SHORT_WAIT);

        /* Get ISBN text */
        EditText isbnEditText = (EditText) solo.getView(R.id.isbn_edit_text);

        /* Enter additional ISBN number to create invalid length */
        solo.enterText((EditText) solo.getView(R.id.isbn_edit_text), "1");

        /* Click save button  */
        solo.clickOnView(solo.getView(R.id.my_books_save_button));

        /* Assert invalid isbn error is shown somewhere */
        Assert.assertTrue(solo.waitForText(EditTextValidator.INVALIDISBN, 1, SHORT_WAIT));

        /* Assert ISBN field has an error */
        Assert.assertNotNull(isbnEditText.getError());

        /* Click on the delete button */
        solo.clickOnButton(resources.getString(R.string.delete_book));

        /* Wait for the MyBooks activity after deleting the book */
        solo.waitForActivity(MyBooksActivity.class, (int) SHORT_WAIT);
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
