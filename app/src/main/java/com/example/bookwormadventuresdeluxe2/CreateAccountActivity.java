package com.example.bookwormadventuresdeluxe2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreateAccountActivity extends AppCompatActivity
{
    Button createAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTitle("Bookworm Adventure Deluxe 2");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        createAccountButton = (Button) findViewById(R.id.createAccountButton2);

        createAccountButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                /* Override to open My Books Activity */
                Intent myBooksIntent = new Intent(CreateAccountActivity.this, MyBooksActivity.class);
                CreateAccountActivity.this.startActivity(myBooksIntent);
            }
        });
    }
}