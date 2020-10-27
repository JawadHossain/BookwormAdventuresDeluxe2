package com.example.bookwormadventuresdeluxe2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class SetLocationActivity extends AppCompatActivity implements OnMapReadyCallback
{
    EditText editTextAddress;
    Button setLocationButton;

    GoogleMap map;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTitle("Set Location");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);

        editTextAddress = findViewById(R.id.set_address_text);
        setLocationButton = findViewById(R.id.set_location_button);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(SetLocationActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
    }

    // TODO: Needs way more for blue dot and initializing start location
}
