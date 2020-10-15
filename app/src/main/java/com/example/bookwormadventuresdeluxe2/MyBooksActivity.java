package com.example.bookwormadventuresdeluxe2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MyBooksActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTitle("My Books");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books);
    }
}