package com.example.bookwormadventuresdeluxe2;

/**
 * Android tests for LoginActivity, tests multiple input
 * combinations and buttons. Also tests a successful login
 * and signout.
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
import static com.example.bookwormadventuresdeluxe2.TestUtils.signIn;
import static com.example.bookwormadventuresdeluxe2.TestUtils.signOut;

/**
 * Tests for login screen, cannot be run more than 6 times in 5 minutes
 * or device will be blocked for too many login attempts
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest
{
    private Solo solo;

    private Context appContext;
    private Resources resources;

    private EditText emailText;
    private EditText passwordText;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
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

        emailText = (EditText) solo.getView(R.id.login_email);
        passwordText = (EditText) solo.getView(R.id.login_password);
    }

    /**
     * Tests going to CreateAccountActivity
     */
    @Test
    public void createAccountButtonTest()
    {
        solo.clickOnButton(resources.getString(R.string.create_account));
        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), CreateAccountActivity.class);
    }

    /**
     * Tests login button functionality with no input
     */
    @Test
    public void emptyLoginTest()
    {
        solo.clickOnButton(resources.getString(R.string.login));
        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), LoginActivity.class);

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));

        Assert.assertNotNull(emailText.getError());
        Assert.assertNotNull(passwordText.getError());
    }

    /**
     * Test for spaces input
     */
    @Test
    public void spacesLoginTest()
    {
        solo.enterText(emailText, resources.getString(R.string.space));
        solo.enterText(passwordText, resources.getString(R.string.space));
        solo.clickOnButton(resources.getString(R.string.login));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));

        Assert.assertNotNull(emailText.getError());
        Assert.assertNotNull(passwordText.getError());
    }

    /**
     * Tests for incorrect password error
     */
    @Test
    public void wrongPasswordTest()
    {
        solo.enterText(emailText, resources.getString(R.string.test_account1_email));
        solo.enterText(passwordText, resources.getString(R.string.wrong_pass));
        solo.clickOnButton(resources.getString(R.string.login));

        Assert.assertTrue(solo.waitForText(EditTextValidator.WRONGPASSWORD, 1, SHORT_WAIT));

        Assert.assertNotNull(passwordText.getError());
    }

    /**
     * Test for invalid or non-existing email input
     */
    @Test
    public void invalidEmailTest()
    {
        solo.enterText(emailText, resources.getString(R.string.wrong_email));
        solo.enterText(passwordText, resources.getString(R.string.wrong_pass));
        solo.clickOnButton(resources.getString(R.string.login));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMAILNOTFOUND, 1, SHORT_WAIT));

        Assert.assertNotNull(emailText.getError());
    }

    /**
     * Tests successfully logging into an account
     */
    @Test
    public void successfulLogin()
    {
        signIn(solo, resources);

        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), MyBooksActivity.class);

        signOut(solo, resources);
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
