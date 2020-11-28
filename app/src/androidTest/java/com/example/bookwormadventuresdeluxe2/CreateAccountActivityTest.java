package com.example.bookwormadventuresdeluxe2;

/**
 * Android tests for CreateAccountActivity, tests multiple
 * input combinations and create account button. Also tests successfully
 * creating an account.
 */

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.bookwormadventuresdeluxe2.Activities.CreateAccountActivity;
import com.example.bookwormadventuresdeluxe2.Activities.MyBooksActivity;
import com.example.bookwormadventuresdeluxe2.Utilities.EditTextValidator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static com.example.bookwormadventuresdeluxe2.TestUtils.NO_WAIT;
import static com.example.bookwormadventuresdeluxe2.TestUtils.SHORT_WAIT;

/**
 * Tests for create account screen
 */
@RunWith(AndroidJUnit4.class)
public class CreateAccountActivityTest
{
    private Solo solo;

    private Context appContext;
    private Resources resources;

    private EditText usernameText;
    private EditText emailText;
    private EditText phoneNumberText;
    private EditText password1Text;
    private EditText password2Text;

    private FirebaseAuth fbAuth;
    private FirebaseFirestore fb;
    private CollectionReference colRef;

    @Rule
    public ActivityTestRule<CreateAccountActivity> rule =
            new ActivityTestRule<>(CreateAccountActivity.class, true, true);

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
        resources = appContext.getResources();

        usernameText = (EditText) solo.getView(R.id.create_username);
        emailText = (EditText) solo.getView(R.id.create_email);
        phoneNumberText = (EditText) solo.getView(R.id.create_phone_number);
        password1Text = (EditText) solo.getView(R.id.create_password);
        password2Text = (EditText) solo.getView(R.id.confirm_password);

        fb = FirebaseFirestore.getInstance();
        colRef = fb.collection(resources.getString(R.string.users_collection));
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
     * Tests create account button functionality with no input
     */
    @Test
    public void emptyCreateAccountTest()
    {
        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));

        Assert.assertNotNull(usernameText.getError());
        Assert.assertNotNull(emailText.getError());
        Assert.assertNotNull(phoneNumberText.getError());
        Assert.assertNotNull(password1Text.getError());
        Assert.assertNotNull(password2Text.getError());
    }

    /**
     * Tests create account button functionality with spaces input
     */
    @Test
    public void spacesCreateAccountTest()
    {
        solo.enterText(usernameText, resources.getString(R.string.space));
        solo.enterText(emailText, resources.getString(R.string.space));
        solo.enterText(phoneNumberText, resources.getString(R.string.space));
        solo.enterText(password1Text, resources.getString(R.string.space));
        solo.enterText(password2Text, resources.getString(R.string.space));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));

        Assert.assertNotNull(usernameText.getError());
        Assert.assertNotNull(emailText.getError());
        Assert.assertNotNull(phoneNumberText.getError());
        Assert.assertNotNull(password1Text.getError());
        Assert.assertNotNull(password2Text.getError());
    }

    /**
     * Tests for empty username and username with only space input
     */
    @Test
    public void emptyAndSpaceUsernameTest()
    {
        solo.enterText(emailText, resources.getString(R.string.test_create_account_email));
        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));
        solo.enterText(password1Text, resources.getString(R.string.test_account1_password));
        solo.enterText(password2Text, resources.getString(R.string.test_account1_password));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));
        Assert.assertNotNull(usernameText.getError());

        Assert.assertNull(emailText.getError());
        Assert.assertNull(phoneNumberText.getError());
        Assert.assertNull(password1Text.getError());
        Assert.assertNull(password2Text.getError());

        solo.enterText(usernameText, resources.getString(R.string.space));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));
        Assert.assertNotNull(usernameText.getError());

        Assert.assertNull(emailText.getError());
        Assert.assertNull(phoneNumberText.getError());
        Assert.assertNull(password1Text.getError());
        Assert.assertNull(password2Text.getError());
    }

    /**
     * Tests for empty email and email with only space input
     */
    @Test
    public void emptyAndSpaceEmailTest()
    {
        solo.enterText(usernameText, resources.getString(R.string.test_create_account_username));
        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));
        solo.enterText(password1Text, resources.getString(R.string.test_account1_password));
        solo.enterText(password2Text, resources.getString(R.string.test_account1_password));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));
        Assert.assertNotNull(emailText.getError());

        Assert.assertNull(usernameText.getError());
        Assert.assertNull(phoneNumberText.getError());
        Assert.assertNull(password1Text.getError());
        Assert.assertNull(password2Text.getError());

        solo.enterText(emailText, resources.getString(R.string.space));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));
        Assert.assertNotNull(emailText.getError());

        Assert.assertNull(usernameText.getError());
        Assert.assertNull(phoneNumberText.getError());
        Assert.assertNull(password1Text.getError());
        Assert.assertNull(password2Text.getError());
    }

    /**
     * Tests for empty phone number
     */
    @Test
    public void emptyPhoneNumberTest()
    {
        solo.enterText(usernameText, resources.getString(R.string.test_create_account_username));
        solo.enterText(emailText, resources.getString(R.string.test_create_account_email));
        solo.enterText(password1Text, resources.getString(R.string.test_account1_password));
        solo.enterText(password2Text, resources.getString(R.string.test_account1_password));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));
        Assert.assertNotNull(phoneNumberText.getError());

        Assert.assertNull(usernameText.getError());
        Assert.assertNull(emailText.getError());
        Assert.assertNull(password1Text.getError());
        Assert.assertNull(password2Text.getError());
    }

    /**
     * Tests for empty password and password with space input
     */
    @Test
    public void emptyAndSpacePasswordTest()
    {
        solo.enterText(usernameText, resources.getString(R.string.test_create_account_username));
        solo.enterText(emailText, resources.getString(R.string.test_create_account_email));
        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));
        Assert.assertNotNull(password1Text.getError());
        Assert.assertNotNull(password2Text.getError());

        Assert.assertNull(usernameText.getError());
        Assert.assertNull(emailText.getError());
        Assert.assertNull(phoneNumberText.getError());

        solo.enterText(password1Text, resources.getString(R.string.space));
        solo.enterText(password2Text, resources.getString(R.string.space));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMPTY, 1, SHORT_WAIT));
        Assert.assertNotNull(password1Text.getError());
        Assert.assertNotNull(password2Text.getError());

        Assert.assertNull(usernameText.getError());
        Assert.assertNull(emailText.getError());
        Assert.assertNull(phoneNumberText.getError());
    }

    /**
     * Tests for username input string < 3 characters
     */
    @Test
    public void shortUsernameTest()
    {
        solo.enterText(usernameText, resources.getString(R.string.short_username));

        solo.enterText(emailText, resources.getString(R.string.test_create_account_email));
        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));
        solo.enterText(password1Text, resources.getString(R.string.test_account1_password));
        solo.enterText(password2Text, resources.getString(R.string.test_account1_password));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.USERNAMETOOSHORT, 1, SHORT_WAIT));
        Assert.assertNotNull(usernameText.getError());

        Assert.assertNull(emailText.getError());
        Assert.assertNull(phoneNumberText.getError());
        Assert.assertNull(password1Text.getError());
        Assert.assertNull(password2Text.getError());
    }

    /**
     * Tests for a non-email string
     */
    @Test
    public void invalidEmailTest()
    {
        solo.enterText(emailText, resources.getString(R.string.wrong_email));

        solo.enterText(usernameText, resources.getString(R.string.test_create_account_username));
        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));
        solo.enterText(password1Text, resources.getString(R.string.test_account1_password));
        solo.enterText(password2Text, resources.getString(R.string.test_account1_password));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.INVALIDEMAIL, 1, SHORT_WAIT));

        Assert.assertNotNull(emailText.getError());

        Assert.assertNull(usernameText.getError());
        Assert.assertNull(phoneNumberText.getError());
        Assert.assertNull(password1Text.getError());
        Assert.assertNull(password2Text.getError());
    }

    /**
     * Tests create account button for multiple invalid phone number strings
     */
    @Test
    public void invalidPhoneNumbersTest()
    {
        createAccountInvalidPhoneNumberUtil(resources.getString(R.string.invalid_phone1));
        createAccountInvalidPhoneNumberUtil(resources.getString(R.string.invalid_phone2));
        createAccountInvalidPhoneNumberUtil(resources.getString(R.string.invalid_phone3));
        createAccountInvalidPhoneNumberUtil(resources.getString(R.string.invalid_phone4));
    }

    /**
     * Handles entering invalid phone numbers
     */
    public void createAccountInvalidPhoneNumberUtil(String phoneNumber)
    {
        solo.clearEditText(usernameText);
        solo.clearEditText(emailText);
        solo.clearEditText(phoneNumberText);
        solo.clearEditText(password1Text);
        solo.clearEditText(password2Text);

        solo.enterText(phoneNumberText, phoneNumber);

        solo.enterText(emailText, resources.getString(R.string.test_create_account_email));
        solo.enterText(usernameText, resources.getString(R.string.test_create_account_username));
        solo.enterText(password1Text, resources.getString(R.string.test_account1_password));
        solo.enterText(password2Text, resources.getString(R.string.test_account1_password));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.INVALIDPHONE, 1, SHORT_WAIT));

        Assert.assertNotNull(phoneNumberText.getError());

        Assert.assertNull(emailText.getError());
        Assert.assertNull(usernameText.getError());
        Assert.assertNull(password1Text.getError());
        Assert.assertNull(password2Text.getError());
    }

    /**
     * Tests create account button with a taken username
     */
    @Test
    public void takenUsernameTest()
    {
        solo.enterText(usernameText, resources.getString(R.string.test_account1_username));

        solo.enterText(emailText, resources.getString(R.string.test_create_account_email));
        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));
        solo.enterText(password1Text, resources.getString(R.string.test_account1_password));
        solo.enterText(password2Text, resources.getString(R.string.test_account1_password));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.USERNAMETAKEN, 1, SHORT_WAIT));

        Assert.assertNotNull(usernameText.getError());

        Assert.assertNull(emailText.getError());
        Assert.assertNull(phoneNumberText.getError());
        Assert.assertNull(password1Text.getError());
        Assert.assertNull(password2Text.getError());
    }

    /**
     * Tests create account button with a taken email
     */
    @Test
    public void takenEmailTest()
    {
        solo.enterText(emailText, resources.getString(R.string.test_account1_email));

        solo.enterText(usernameText, resources.getString(R.string.test_create_account_username));
        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));
        solo.enterText(password1Text, resources.getString(R.string.test_account1_password));
        solo.enterText(password2Text, resources.getString(R.string.test_account1_password));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.EMAILTAKEN, 1, SHORT_WAIT));

        Assert.assertNotNull(emailText.getError());

        Assert.assertNull(usernameText.getError());
        Assert.assertNull(phoneNumberText.getError());
        Assert.assertNull(password1Text.getError());
        Assert.assertNull(password2Text.getError());

    }

    /**
     * Tests for taken username and taken email input at the same time
     */
    @Test
    public void takenUsernameAndTakenEmailTest()
    {
        solo.enterText(usernameText, resources.getString(R.string.test_account1_username));
        solo.enterText(emailText, resources.getString(R.string.test_account1_email));

        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));
        solo.enterText(password1Text, resources.getString(R.string.test_account1_password));
        solo.enterText(password2Text, resources.getString(R.string.test_account1_password));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.USERNAMETAKEN, 1, SHORT_WAIT));
        Assert.assertTrue(solo.waitForText(EditTextValidator.EMAILTAKEN, 1, NO_WAIT));

        Assert.assertNotNull(usernameText.getError());
        Assert.assertNotNull(emailText.getError());

        Assert.assertNull(phoneNumberText.getError());
        Assert.assertNull(password1Text.getError());
        Assert.assertNull(password2Text.getError());
    }

    /**
     * Tests create account button with non-matching passwords
     */
    @Test
    public void nonMatchingPasswordsTest()
    {
        solo.enterText(password1Text, resources.getString(R.string.test_account1_password));
        solo.enterText(password2Text, resources.getString(R.string.wrong_pass));

        solo.enterText(emailText, resources.getString(R.string.test_create_account_email));
        solo.enterText(usernameText, resources.getString(R.string.test_create_account_username));
        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.PASSWORDSDONTMATCH, 1, SHORT_WAIT));

        Assert.assertNotNull(password1Text.getError());
        Assert.assertNotNull(password2Text.getError());

        Assert.assertNull(usernameText.getError());
        Assert.assertNull(emailText.getError());
        Assert.assertNull(phoneNumberText.getError());
    }

    /**
     * Tests create account button with password less than 6 characters
     */
    @Test public void shortPasswordTest()
    {
        solo.enterText(password1Text, resources.getString(R.string.short_pass));
        solo.enterText(password2Text, resources.getString(R.string.short_pass));

        solo.enterText(emailText, resources.getString(R.string.test_create_account_email));
        solo.enterText(usernameText, resources.getString(R.string.test_create_account_username));
        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.WEAKPASS, 1, SHORT_WAIT));

        Assert.assertNotNull(password1Text.getError());
        Assert.assertNotNull(password2Text.getError());

        Assert.assertNull(usernameText.getError());
        Assert.assertNull(emailText.getError());
        Assert.assertNull(phoneNumberText.getError());
    }

    /**
     * Tests create account button with password 5 characters and a space
     */
    @Test public void shortPasswordAndSpaceTest()
    {
        solo.enterText(password1Text, resources.getString(R.string.short_pass) + resources.getString(R.string.space));
        solo.enterText(password2Text, resources.getString(R.string.short_pass) + resources.getString(R.string.space));

        solo.enterText(emailText, resources.getString(R.string.test_create_account_email));
        solo.enterText(usernameText, resources.getString(R.string.test_create_account_username));
        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));

        solo.clickOnButton(resources.getString(R.string.create_account));

        Assert.assertTrue(solo.waitForText(EditTextValidator.WEAKPASS, 1, SHORT_WAIT));

        Assert.assertNotNull(password1Text.getError());
        Assert.assertNotNull(password2Text.getError());

        Assert.assertNull(usernameText.getError());
        Assert.assertNull(emailText.getError());
        Assert.assertNull(phoneNumberText.getError());
    }

    /**
     * Tests successful account creation and signs out
     */
    @Test
    public void createAccountTest()
    {
        solo.enterText(emailText, resources.getString(R.string.test_create_account_email));
        solo.enterText(usernameText, resources.getString(R.string.test_create_account_username));

        solo.enterText(phoneNumberText, resources.getString(R.string.test_account1_phone));
        solo.enterText(password1Text, resources.getString(R.string.test_account1_password));
        solo.enterText(password2Text, resources.getString(R.string.test_account1_password));

        solo.clickOnButton(resources.getString(R.string.create_account));

        solo.assertCurrentActivity(resources.getString(R.string.wrong_activity), MyBooksActivity.class);

        createAccountSignOut();
    }

    /**
     * Signs out of created test account after deleting account
     */
    public void createAccountSignOut()
    {
        solo.clickOnText(resources.getString(R.string.navbar_text_label_4));

        solo.waitForText(resources.getString(R.string.sign_out), 1, SHORT_WAIT);

        deleteCreateTestAccount();

        solo.clickOnButton(resources.getString(R.string.sign_out));
    }

    /**
     * Deletes created test account in FirebaseAuth and Firebase database
     */
    public void deleteCreateTestAccount()
    {
        colRef.whereEqualTo("username", resources.getString(R.string.test_create_account_username))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>()
                {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            for (QueryDocumentSnapshot document : task.getResult())
                            {
                                colRef.document(document.getId()).delete();
                                fbAuth = FirebaseAuth.getInstance();
                                fbAuth.getInstance().getCurrentUser().delete();
                            }
                        }
                    }
                });
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
