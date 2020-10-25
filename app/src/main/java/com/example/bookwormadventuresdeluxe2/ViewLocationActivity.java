package com.example.bookwormadventuresdeluxe2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class ViewLocationActivity extends AppCompatActivity implements OnMapReadyCallback
{
    TextView textViewAddress;
    Button scanISBNButton;

    GoogleMap map;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        setTitle("View Location");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        textViewAddress = findViewById(R.id.view_address_text);
        scanISBNButton = findViewById(R.id.scan_isbn_button);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(ViewLocationActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
    }

    // TODO: Needs way more for blue dot and initializing start location
}
