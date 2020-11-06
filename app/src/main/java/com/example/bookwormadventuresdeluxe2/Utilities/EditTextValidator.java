package com.example.bookwormadventuresdeluxe2.Utilities;

/**
 * Utility class for EditText error handling. Matches target
 * EditText and wanted output.
 */

import android.text.TextUtils;
import android.widget.EditText;

/**
 * Validates EditTexts and sets error notication on EditTexts
 */
public class EditTextValidator
{
    public static String EMPTY = "Cannot be left blank!";
    public static String PASSWORDSDONTMATCH = "Passwords do not match!";
    public static String EMAILTAKEN = "Email is already taken!";
    public static String USERNAMETAKEN = "Username is already taken!";
    public static String EMAILNOTFOUND = "Email not found!";
    public static String WRONGPASSWORD = "Incorrect password!";
    public static String WEAKPASS = "Password must be 6 characters or longer!";
    public static String INVALIDEMAIL = "Invalid e-mail address!";
    public static String INVALIDISBN = "Invalid ISBN!";

    /**
     * Set empty field error notification
     *
     * @param editText editText on which error is set
     */
    public static void isEmpty(EditText editText)
    {
        editText.setError(EMPTY);
        editText.setText("");
        editText.requestFocus();
    }

    /**
     * Set invalid ISBN error notification
     *
     * @param editText editText on which error is sey
     */
    public static void invalidIsbn(EditText editText)
    {
        editText.setError(INVALIDISBN);
        editText.requestFocus();
    }

    /**
     * Set email not found error notification
     *
     * @param editText editText on which error is set
     */
    public static void emailNotFound(EditText editText)
    {
        editText.setError(EMAILNOTFOUND);
        editText.requestFocus();
    }

    /**
     * Set username taken error notification
     *
     * @param editText editText on which error is set
     */
    public static void usernameTaken(EditText editText)
    {
        editText.setError(USERNAMETAKEN);
        editText.requestFocus();
    }

    /**
     * Set wrong password error notification
     *
     * @param editText editText on which error is set
     */
    public static void wrongPassword(EditText editText)
    {
        editText.setError(WRONGPASSWORD);
        editText.requestFocus();
    }

    /**
     * Set email taken error notification
     *
     * @param editText editText on which error is set
     */
    public static void emailTaken(EditText editText)
    {
        editText.setError(EMAILTAKEN);
        editText.requestFocus();
    }

    /**
     * Set invalid email error notification
     *
     * @param editText editText on which error is set
     */
    public static void invalidEmail(EditText editText)
    {
        editText.setError(INVALIDEMAIL);
        editText.requestFocus();
    }

    /**
     * Set passwords don't match error if password1 and password2 don't match
     *
     * @param password1 password on which error is set
     * @param password2 editText on which error is set
     * @return boolean stating if passwords match
     */
    public static boolean passwordsMatch(EditText password1, EditText password2)
    {
        if (password1.getText().toString().trim().equals(password2.getText().toString().trim()))
        {
            return true;
        }
        else
        {
            password1.setError(PASSWORDSDONTMATCH);
            password2.setError(PASSWORDSDONTMATCH);
            password2.requestFocus();
            password1.requestFocus();
            return false;
        }
    }

    /**
     * Set weak password error if password1 and password2 are have length < 6
     *
     * @param password1 password on which error is set
     * @param password2 editText on which error is set
     * @return boolean stating is password is long enough
     */
    public static boolean weakPass(EditText password1, EditText password2)
    {
        if (password1.getText().toString().trim().length() >= 6)
        {
            password1.setError(null);
            password2.setError(null);
            return false;
        }
        else
        {
            password1.setError(WEAKPASS);
            password2.setError(WEAKPASS);
            password2.requestFocus();
            password1.requestFocus();
            return true;
        }
    }

    /**
     * Checker for CreateAccountActivity empty fields
     *
     * @param username username EditText
     * @param email email EditText
     * @param phoneNumber phone number EditText
     * @param password1 password EditText
     * @param password2 confirm password EditText
     * @return boolean if empties exist or not
     */
    public static boolean createAccountEmpties(EditText username, EditText email,
                                               EditText phoneNumber, EditText password1,
                                               EditText password2)
    {
        Boolean empties = false;

        /* Set Empty EditText Error codes */
        if (TextUtils.isEmpty(password2.getText().toString().trim()))
        {
            EditTextValidator.isEmpty(password2);
            empties = true;
        }
        if (TextUtils.isEmpty(password1.getText().toString().trim()))
        {
            EditTextValidator.isEmpty(password1);
            empties = true;
        }
        if (TextUtils.isEmpty(phoneNumber.getText().toString().trim()))
        {
            EditTextValidator.isEmpty(phoneNumber);
            empties = true;
        }
        if (TextUtils.isEmpty(email.getText().toString().trim()))
        {
            EditTextValidator.isEmpty(email);
            empties = true;
        }
        if (TextUtils.isEmpty(username.getText().toString().trim()))
        {
            EditTextValidator.isEmpty(username);
            empties = true;
        }
        return empties;
    }
}