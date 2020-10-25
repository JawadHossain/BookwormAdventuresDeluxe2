package com.example.bookwormadventuresdeluxe2;

import android.widget.EditText;

/**
 * Returns error message and sets error notification for input textChecked
 */
public class EditTextErrors
{
    private static String EMPTY = "Cannot be left blank!";
    private static String PASSWORDSDONTMATCH = "Passwords do not match!";
    private static String EMAILTAKEN = "Email is already taken!";
    private static String EMAILNOTFOUND = "Email not found!";
    private static String WRONGPASSWORD = "Incorrect password!";
    private static String WEAKPASS = "Password must be 6 characters or longer!";
    private static String INVALIDEMAIL = "Invalid e-mail address!";

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

    public static void emailTaken(EditText textChecked)
    {
        textChecked.setError(EMAILTAKEN);
        textChecked.requestFocus();
    }

    public static void invalidEmail(EditText textChecked)
    {
        textChecked.setError(INVALIDEMAIL);
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
        if (password1.getText().toString().length() >=6 )
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