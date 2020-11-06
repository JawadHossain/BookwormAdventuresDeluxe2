package com.example.bookwormadventuresdeluxe2;

/**
 * Allows borrower to view a pickup location
 * The pickup location is provided through an intent when this activity is started.
 */

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class ViewLocationActivity extends AppCompatActivity implements OnMapReadyCallback
{
    private static final String TAG = "ViewLocationActivity";
    private TextView addressTextView;
    private TextView appHeaderTitle;
    private ImageButton backButton;
    private String locationString;

    private GoogleMap map;
    private Geocoder geocoder;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);
        /* Set Header */
        appHeaderTitle = findViewById(R.id.app_header_title);
        appHeaderTitle.setText(R.string.view_location_pending_request);

        /* Set Back Button Listener */
        backButton = findViewById(R.id.app_header_back_button);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(this::onBackClick);

        /* Used to find addresses using name or LatLng*/
        geocoder = new Geocoder(this);

        /* Get pickUpLocation from intent*/
        Intent intent = getIntent();
        locationString = intent.getStringExtra("location");

        /* Set a default location in case the location is null*/
        if (TextUtils.isEmpty(locationString))
        {
            locationString = "53.510787,-113.5140128";
        }

        addressTextView = findViewById(R.id.view_address_text);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(ViewLocationActivity.this);
    }

    /**
     * Take user to parent activity
     *
     * @param view
     */
    private void onBackClick(View view)
    {
        super.onBackPressed();
    }

    /**
     * Add a marker at the pick up location when map is ready
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;

        /* Create LatLng object from locationString*/
        String[] latLogArray = locationString.split(",", 2); // split into array at ","
        Double latitude = Double.parseDouble(latLogArray[0].trim());
        Double longitude = Double.parseDouble(latLogArray[1].trim());
        LatLng location = new LatLng(latitude, longitude);

        /* Find street address using geocoder*/
        try
        {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0)
            {
                /* Find first address match */
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                addressTextView.setText(streetAddress); // Update Address TextView

                /* Add new marker*/
                map.addMarker(new MarkerOptions()
                        .position(location)
                        .title(streetAddress)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                /* Move view to new marker */
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14));

            }
        } catch (IOException e)
        {
            Log.d(TAG, "Error Setting new Marker onMapReady " + e.getMessage());
        }
    }
}
