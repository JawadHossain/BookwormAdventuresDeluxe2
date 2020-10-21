package com.example.bookwormadventuresdeluxe2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class LoginActivity extends AppCompatActivity
{
    EditText editTextUsername;
    EditText editTextPassword;
    Button loginButton;
    Button createAccountButton;
    ImageButton visibilityButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.login_username);
        editTextPassword = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.login_button);
        createAccountButton = (Button) findViewById(R.id.create_account_button);
        visibilityButton = (ImageButton) findViewById(R.id.visibility_button);

        /* Set the password to hidden by default */
        editTextPassword.setTransformationMethod(new PasswordTransformationMethod());

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                /* Override to open My Books Activity */
                // TODO: Validate login
                Intent myBooksIntent = new Intent(LoginActivity.this, MyBooksActivity.class);
                LoginActivity.this.startActivity(myBooksIntent);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                /* Override to open Create Account Activity */
                Intent myBooksIntent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                LoginActivity.this.startActivity(myBooksIntent);
            }
        });

        visibilityButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                /* Toggle back and forth between visible and hidden password */

                /* Get the cursor start and end index so we can restore the cursor position later */
                int cursorStart = editTextPassword.getSelectionStart();
                int cursorEnd = editTextPassword.getSelectionEnd();

                /* https://stackoverflow.com/questions/24106904/get-drawable-of-image-button */
                if (visibilityButton.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.ic_visibility_24px).getConstantState()))
                {
                    /* Image is currently set to password visible, so set it to hidden */
                    visibilityButton.setImageResource(R.drawable.ic_visibility_off_24px);
                    /* https://stackoverflow.com/questions/3685790/how-to-switch-between-hide-and-view-password */
                    /* Hide the text as well */
                    editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
                else
                {
                    /* Image is currently set to password hidden, so set it to visible */
                    visibilityButton.setImageResource(R.drawable.ic_visibility_24px);
                    /* https://stackoverflow.com/questions/3685790/how-to-switch-between-hide-and-view-password */
                    /* Show the text again */
                    editTextPassword.setTransformationMethod(null);
                }

                /* Restore cursor position after hiding/showing the password */
                editTextPassword.setSelection(cursorStart, cursorEnd);
            }
        });
    }
}