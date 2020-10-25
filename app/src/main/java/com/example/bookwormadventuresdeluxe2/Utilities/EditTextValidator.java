package com.example.bookwormadventuresdeluxe2.Utilities;

import android.widget.EditText;

/**
 * Validates EditTexts and sets error notication on EditTexts
 */
public class EditTextValidator
{
    private static String EMPTY = "Cannot be left blank!";
    private static String PASSWORDSDONTMATCH = "Passwords do not match!";
    private static String EMAILTAKEN = "Email is already taken!";
    private static String USERNAMETAKEN = "Username is already taken!";
    private static String EMAILNOTFOUND = "Email not found!";
    private static String WRONGPASSWORD = "Incorrect password!";
    private static String WEAKPASS = "Password must be 6 characters or longer!";
    private static String INVALIDEMAIL = "Invalid e-mail address!";

    /**
     * Set empty field error notification
     *
     * @param editText editText on which error is set
     */
    public static void isEmpty(EditText editText)
    {
        editText.requestFocus();
        editText.setError(EMPTY);

        if (allSpaces(editText))
        {
            editText.setText("");
        }
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
     * Checks if input text was all space characters
     *
     * @param editText editText to be checked
     * @return boolean stating if all spaces
     */
    private static boolean allSpaces(EditText editText)
    {
        int spaceCount = 0;

        for (int i = 0; i < editText.length(); i++)
        {
            if (Character.isSpaceChar(editText.getText().toString().charAt(i)))
            {
                spaceCount++;
            }
        }
        if (spaceCount == editText.length())
        {
            editText.clearComposingText();
            return true;
        }
        else
        {
            return false;
        }
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
        if (password1.getText().toString().compareTo(password2.getText().toString()) == 0)
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
     */
    public static void weakPass(EditText password1, EditText password2)
    {
        if (password1.getText().toString().length() >= 6)
        {
            password1.setError(null);
            password2.setError(null);
        }
        else
        {
            password1.setError(WEAKPASS);
            password2.setError(WEAKPASS);
            password2.requestFocus();
            password1.requestFocus();
        }
    }

}