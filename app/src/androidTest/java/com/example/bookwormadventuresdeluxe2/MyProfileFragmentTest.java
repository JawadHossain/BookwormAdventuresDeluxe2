package com.example.bookwormadventuresdeluxe2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.bookwormadventuresdeluxe2.Activities.LoginActivity;
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
     *
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
