package com.example.bookwormadventuresdeluxe2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.bookwormadventuresdeluxe2.Activities.LoginActivity;
import com.example.bookwormadventuresdeluxe2.Activities.MyBooksActivity;
import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static com.example.bookwormadventuresdeluxe2.TestUtils.NO_WAIT;
import static com.example.bookwormadventuresdeluxe2.TestUtils.SHORT_WAIT;
import static com.example.bookwormadventuresdeluxe2.TestUtils.signIn;

public class MyProfileFragmentTest
{
    private Solo solo;

    private Context appContext;
    private Resources resources;

    private EditText editEmailText;
    private EditText editPhoneText;

    private EditText loginEmailText;
    private EditText loginPasswordText;

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
    }

    /**
     * Gets the activity
     *
     * @throws Exception
     */
    @Test
    public void start() throws Exception {
        Activity activity = rule.getActivity();
    }

    /**
     * Tests viewing 'my profile' after login
     */
    @Test
    public void checkMyProfile()
    {
        /* Sign in with the test account */
        signIn(solo, resources);

        solo.clickOnText(resources.getString(R.string.navbar_text_label_4));

        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account1_username), 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account1_email), 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account1_phone), 1, NO_WAIT));

        solo.clickOnButton(resources.getString(R.string.sign_out));

        solo.waitForActivity(LoginActivity.class, (int) SHORT_WAIT);
    }

    /**
     * Tests editing email information
     */
    @Test
    public void editEmailTest()
    {
        signIn(solo, resources);

        /* Edits email */
        editEmailTestUtil(resources.getString(R.string.test_edit_email));

        /* Log in with new email */
        loginEmailText = (EditText) solo.getView(R.id.login_email);
        loginPasswordText = (EditText) solo.getView(R.id.login_password);

        solo.enterText(loginEmailText, resources.getString(R.string.test_edit_email));
        solo.enterText(loginPasswordText, resources.getString(R.string.test_account1_password));

        solo.clickOnButton(resources.getString(R.string.login));
        solo.waitForActivity(MyBooksActivity.class, (int) SHORT_WAIT);

        /* Reverts to original email*/
        editEmailTestUtil(resources.getString(R.string.test_account1_email));
    }

    /**
     * Handles entering email for editing contact information
     *
     * @param email Email to be set
     */
    public void editEmailTestUtil(String email)
    {
        goToEditProfile();

        editEmailText = (EditText) solo.getView(R.id.edit_email);

        solo.clearEditText(editEmailText);
        solo.enterText(editEmailText, email);
        solo.clickOnText(resources.getString(R.string.confirm));

        solo.waitForFragmentById(R.layout.fragment_profile, (int) SHORT_WAIT);

        Assert.assertTrue(solo.waitForText(email, 1, NO_WAIT));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account1_phone), 1, NO_WAIT));

        solo.clickOnText(resources.getString(R.string.sign_out));
        solo.waitForActivity(LoginActivity.class, (int) SHORT_WAIT);
    }

    /**
     * Tests editing phone number contact information
     */
    @Test
    public void editPhoneNumber()
    {
        /* Tests editing phone number */
        editPhoneTestUtil(resources.getString(R.string.test_edit_phone));

        /* Reverts to original phone number*/
        editPhoneTestUtil(resources.getString(R.string.test_account1_phone));
    }

    /**
     * Handles entering phone number for editing contact information
     *
     * @param phone Phone number to be set
     */
    public void editPhoneTestUtil(String phone)
    {
        signIn(solo, resources);
        goToEditProfile();

        editPhoneText = (EditText) solo.getView(R.id.edit_phone);

        solo.clearEditText(editPhoneText);
        solo.enterText(editPhoneText, phone);
        solo.clickOnText(resources.getString(R.string.confirm));

        solo.waitForFragmentById(R.layout.fragment_profile, (int) SHORT_WAIT);

        Assert.assertTrue(solo.waitForText(phone, 1, NO_WAIT));

        solo.clickOnButton(resources.getString(R.string.sign_out));
        solo.waitForActivity(LoginActivity.class, (int) SHORT_WAIT);
    }

    /**
     * Tests editing email information with a taken email
     */
    @Test
    public void takenEmailTest()
    {
        signIn(solo, resources);
        goToEditProfile();

        editEmailText = (EditText) solo.getView(R.id.edit_email);
        editPhoneText = (EditText) solo.getView(R.id.edit_phone);

        solo.clearEditText(editEmailText);
        solo.enterText(editEmailText, resources.getString(R.string.test_account2_email));
        solo.clickOnText(resources.getString(R.string.confirm));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMAILTAKEN, 1, NO_WAIT));
        Assert.assertNull(editPhoneText.getError());

        solo.clickOnText(resources.getString(R.string.cancel));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account1_email), 1, SHORT_WAIT));

        solo.clickOnButton(resources.getString(R.string.sign_out));
        solo.waitForActivity(LoginActivity.class, (int) SHORT_WAIT);
    }

    /**
     * Tests editing email information with an invalid email string
     */
    @Test
    public void invalidEmailTest()
    {
        signIn(solo, resources);
        goToEditProfile();

        editEmailText = (EditText) solo.getView(R.id.edit_email);
        editPhoneText = (EditText) solo.getView(R.id.edit_phone);

        solo.clearEditText(editEmailText);
        solo.enterText(editEmailText, resources.getString(R.string.wrong_email));
        solo.clickOnText(resources.getString(R.string.confirm));

        Assert.assertTrue(solo.waitForText(EditTextValidator.INVALIDEMAIL, 1, NO_WAIT));
        Assert.assertNull(editPhoneText.getError());

        solo.clickOnText(resources.getString(R.string.cancel));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account1_email), 1, SHORT_WAIT));

        solo.clickOnButton(resources.getString(R.string.sign_out));
        solo.waitForActivity(LoginActivity.class, (int) SHORT_WAIT);
    }

    /**
     * Tests editing phone number with invalid strings
     */
    @Test
    public void invalidPhoneTest()
    {
        signIn(solo, resources);
        goToEditProfile();

        editInvalidPhoneNumberUtil(resources.getString(R.string.invalid_phone1));
        editInvalidPhoneNumberUtil(resources.getString(R.string.invalid_phone2));
        editInvalidPhoneNumberUtil(resources.getString(R.string.invalid_phone3));
        editInvalidPhoneNumberUtil(resources.getString(R.string.invalid_phone4));

        solo.clickOnText(resources.getString(R.string.cancel));
        Assert.assertTrue(solo.waitForText(resources.getString(R.string.test_account1_phone), 1, SHORT_WAIT));

        solo.clickOnButton(resources.getString(R.string.sign_out));
        solo.waitForActivity(LoginActivity.class, (int) SHORT_WAIT);
    }

    /**
     * Handles entering invalid phone numbers
     */
    public void editInvalidPhoneNumberUtil(String phoneNumber)
    {
        editEmailText = (EditText) solo.getView(R.id.edit_email);
        editPhoneText = (EditText) solo.getView(R.id.edit_phone);

        solo.clearEditText(editPhoneText);
        solo.enterText(editPhoneText, phoneNumber);
        solo.clickOnButton(resources.getString(R.string.confirm));

        Assert.assertTrue(solo.waitForText(EditTextValidator.INVALIDPHONE, 1, SHORT_WAIT));
        Assert.assertNotNull(editEmailText);
    }

    /**
     * Navigates to EditProfileFragment
     */
    public void goToEditProfile()
    {
        solo.clickOnText(resources.getString(R.string.navbar_text_label_4));
        solo.waitForFragmentById(R.layout.fragment_profile, (int) SHORT_WAIT);
        solo.clickOnButton(resources.getString(R.string.edit_contact));
        solo.waitForFragmentById(R.layout.edit_profile, (int) SHORT_WAIT);
    }

    /**
     * Closes the activity after each test
     *
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception
    {
        solo.finishOpenedActivities();
    }
}
