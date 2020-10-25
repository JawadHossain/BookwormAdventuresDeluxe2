package com.example.bookwormadventuresdeluxe2;

import android.widget.EditText;

/**
 * Returns error message and sets error notification for input textChecked
 */
public class EditTextErrors
{
    private static String EMPTY = "Cannot be left blank!";
    private static String PASSWORDSDONTMATCH = "Passwords do not match!";
    private static String USEREXISTS = "Username taken!";
    private static String EMAILNOTFOUND = "Email not found!";
    private static String WRONGPASSWORD = "Incorrect password";

    /** Error notification method for empty field*/
    public static void isEmpty(EditText textChecked)
    {
        textChecked.requestFocus();
        textChecked.setError(EMPTY);

        if(allSpaces(textChecked))
        {
            textChecked.setText("");
        }
    }

    public static void emailNotFound(EditText textChecked)
    {
        textChecked.setError(EMAILNOTFOUND);
        textChecked.requestFocus();
    }

    public static void wrongPassword(EditText textChecked)
    {
        textChecked.setError(WRONGPASSWORD);
        textChecked.requestFocus();
    }

    /** Checks if input text was all space characters */
    private static boolean allSpaces(EditText textChecked)
    {
        int spaceCount = 0;

        for (int i = 0; i < textChecked.length(); i++)
        {
            if (Character.isSpaceChar(textChecked.getText().toString().charAt(i)))
            {
                spaceCount++;
            }
        }
        if (spaceCount == textChecked.length())
        {
            textChecked.clearComposingText();
            return true;
        }
        else
        {
            return false;
        }
    }

    /** Returns error for CreateAccountActivity if passwords don't match*/
    public static void passwordsMatch(EditText password1, EditText password2)
    {
        if (password1.getText().toString() == password2.getText().toString())
        {
            password1.setError(PASSWORDSDONTMATCH);
            password2.setError(PASSWORDSDONTMATCH);
            password1.requestFocus();
            password2.requestFocus();
        }
    }

}