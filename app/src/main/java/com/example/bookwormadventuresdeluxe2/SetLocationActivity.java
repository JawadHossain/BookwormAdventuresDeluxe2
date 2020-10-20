package com.example.bookwormadventuresdeluxe2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class SetLocationActivity extends AppCompatActivity
{
    EditText editTextAddress;
    Button setLocationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTitle("Set Location");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        editTextAddress = (EditText) findViewById(R.id.set_address_text);
        setLocationButton = (Button) findViewById(R.id.set_location_button);
    }
}
