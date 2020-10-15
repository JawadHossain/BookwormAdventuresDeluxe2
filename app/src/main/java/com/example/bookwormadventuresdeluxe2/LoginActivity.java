package com.example.bookwormadventuresdeluxe2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends AppCompatActivity
{
    Button loginButton;
    Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTitle("Bookworm Adventure Deluxe 2");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = (Button) findViewById(R.id.loginButton);
        createAccountButton = (Button) findViewById(R.id.createAccountButton);

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                /* Override login to open MyBooks Activity */
                // TODO: Validate login
                Intent myBooksIntent = new Intent(LoginActivity.this, MyBooksActivity.class);
                LoginActivity.this.startActivity(myBooksIntent);
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                /* Override login to open Create Account Activity */
                Intent myBooksIntent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                LoginActivity.this.startActivity(myBooksIntent);
            }
        });
    }
}