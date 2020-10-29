package com.example.bookwormadventuresdeluxe2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.widget.EditText;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

/**
 * Tests for login screen, cannot be
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest
{
    private Solo solo;
    private String TAG = "LoginActivityTest";

    private CollectionReference colRef;
    private FirebaseFirestore db;
    private Context appContext;
    private Resources r;

    private int shortWait = 1000;
    private int longWait = 4000;

    private EditText emailText;
    private EditText passwordText;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(getInstrumentation(),rule.getActivity());

        /* Gets context for app resources */
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        /* Gets resource files */
        r = appContext.getResources();
        r.getString(R.string.users_collection);

        db = FirebaseFirestore.getInstance();
        colRef = db.collection(r.getString(R.string.users_collection));

        emailText = (EditText) solo.getView(R.id.login_email);
        passwordText = (EditText) solo.getView(R.id.login_password);
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
     * Tests going to CreateAccountActivity
     */
    @Test
    public void createAccountButtonTest()
    {
        solo.clickOnButton(r.getString(R.string.create_account));
        solo.assertCurrentActivity("Wrong Activity", CreateAccountActivity.class);

        solo.sleep(shortWait);
    }

    /**
     * Tests login button functionality with no input
     */
    @Test
    public void emptyLoginTest()
    {
        solo.clickOnButton(r.getString(R.string.login));
        solo.assertCurrentActivity("Wrong Activity", LoginActivity.class);

        solo.sleep(shortWait);
    }

    /**
     * Test for spaces input
     */
    @Test
    public void spacesLoginTest()
    {
        solo.enterText(emailText, " ");
        solo.enterText(passwordText, " ");
        solo.clickOnButton(r.getString(R.string.login));

        solo.sleep(shortWait);

        Assert.assertNotNull(emailText.getError());
        Assert.assertNotNull(passwordText.getError());
    }

    /**
     * Tests for incorrect password error
     */
    @Test
    public void wrongPasswordTest()
    {
        solo.enterText(emailText, r.getString(R.string.test_account1_email));
        solo.enterText(passwordText, "wrongpassword");
        solo.clickOnButton(r.getString(R.string.login));

        solo.sleep(longWait);

        Assert.assertNotNull(passwordText.getError());
    }

    /**
     * Test for invalid or non-existing email input
     */
    @Test
    public void invalidEmailTest()
    {
        solo.enterText(emailText, "wrongemail");
        solo.enterText(passwordText, "wrongpassword");
        solo.clickOnButton(r.getString(R.string.login));

        solo.sleep(longWait);

        Assert.assertNotNull(emailText.getError());
    }

    /**
     * Signs in to test account
     */
    @Test
    public void successfulLogin()
    {
        solo.enterText(emailText, r.getString(R.string.test_account1_email));
        solo.enterText(passwordText, r.getString(R.string.test_account1_password));
        solo.clickOnButton(r.getString(R.string.login));

        solo.sleep(shortWait);

        solo.assertCurrentActivity("Wrong Activity", MyBooksActivity.class);

        signOut();
    }

    /**
     * Signs out of test account after login
     */
    public void signOut()
    {
        solo.clickOnText(r.getString(R.string.navbar_text_label_4));

        solo.sleep(shortWait);

        solo.clickOnButton(r.getString(R.string.sign_out));
    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
