package com.example.bookwormadventuresdeluxe2;

import android.content.res.Resources;
import android.widget.EditText;

import com.robotium.solo.Solo;

/**
 * A collection of utilities to be used across all tests
 */
public class TestUtils
{
    /**
     * Signs out of test account after login. Assumes we are in the MyBooksActivity.
     */
    public static void signOut(Solo solo, Resources resources)
    {
        solo.clickOnText(resources.getString(R.string.navbar_text_label_4));

        solo.waitForText(resources.getString(R.string.sign_out));

        solo.clickOnButton(resources.getString(R.string.sign_out));

        /* Wait for the login activity after signing out */
        solo.waitForActivity(LoginActivity.class);
    }

    /**
     * Signs in with the test account. Assumes we are in the LoginActivity.
     */
    public static void signIn(Solo solo, Resources resources)
    {
        /* Get the email and password EditTexts */
        EditText emailText = (EditText) solo.getView(R.id.login_email);
        EditText passwordText = (EditText) solo.getView(R.id.login_password);

        solo.enterText(emailText, resources.getString(R.string.test_account1_email));
        solo.enterText(passwordText, resources.getString(R.string.test_account1_password));
        solo.clickOnButton(resources.getString(R.string.login));

        /* Wait for the MyBooks activity after signing in */
        solo.waitForActivity(MyBooksActivity.class);
    }
}
