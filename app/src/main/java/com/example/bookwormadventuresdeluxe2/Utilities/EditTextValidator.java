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
     * Error notification method for empty field
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

    public static void emailNotFound(EditText editText)
    {
        editText.setError(EMAILNOTFOUND);
        editText.requestFocus();
    }

    public static void usernameTaken(EditText editText)
    {
        editText.setError(USERNAMETAKEN);
        editText.requestFocus();
    }

    public static void wrongPassword(EditText editText)
    {
        editText.setError(WRONGPASSWORD);
        editText.requestFocus();
    }

    public static void emailTaken(EditText editText)
    {
        editText.setError(EMAILTAKEN);
        editText.requestFocus();
    }

    public static void invalidEmail(EditText editText)
    {
        editText.setError(INVALIDEMAIL);
        editText.requestFocus();
    }

    /**
     * Checks if input text was all space characters
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
     * Returns error for CreateAccountActivity if passwords don't match
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