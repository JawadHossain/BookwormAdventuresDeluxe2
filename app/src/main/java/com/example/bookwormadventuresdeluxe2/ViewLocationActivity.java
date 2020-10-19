package com.example.bookwormadventuresdeluxe2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewLocationActivity extends AppCompatActivity
{
    TextView textViewAddress;
    Button scanISBNButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTitle("View Location");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        textViewAddress = (TextView) findViewById(R.id.view_address_text);
        scanISBNButton = (Button) findViewById(R.id.scan_isbn_button);
    }
}
