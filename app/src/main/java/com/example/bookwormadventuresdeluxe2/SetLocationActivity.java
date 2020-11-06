package com.example.bookwormadventuresdeluxe2;

/**
 * Allows user to pick an exchange location by placing a marker on a map.
 * Upon setting a location the activity returns the location to the parent activity
 * through an intent.
 * part of code taken from: https://developer.android.com/training/location/retrieve-current
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetLocationActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener, SearchView.OnQueryTextListener
{
    public static final String TAG = "SetLocationActivity";
    private static final int LOCATION_REQUEST_CODE = 1001; // identification code for onRequestPermissionsResult method
    public static final long UPDATE_INTERVAL = 5000; // 5 seconds
    public static final long FASTEST_INTERVAL = 5000; //  5 seconds
    public static final int CAMERA_ZOOM_LEVEL = 14; // can be between 1-20
    public static final double defaultLatitude = 53.5188629; // default is uAlberta
    public static final double defaultLongitude = -113.5041364;
    private SearchView addressSearchView;
    private Button setLocationButton;
    private TextView appHeaderTitle;
    private ImageButton backButton;

    private ArrayList<String> locationPermissions = new ArrayList<>();
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;

    private GoogleMap map;
    private Geocoder geocoder;
    private SupportMapFragment mapFragment;

    private String pickUpLocation;

    /* Called when there is a change in the user location */
    private LocationCallback locationCallback = new LocationCallback()
    {
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_location);
        /* Set Header */
        appHeaderTitle = findViewById(R.id.app_header_title);
        appHeaderTitle.setText(R.string.set_exchange_location);

        /* Set Back Button Listener */
        backButton = findViewById(R.id.app_header_back_button);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(this::onBackClick);

        geocoder = new Geocoder(this); // to search for location using name ro LatLng
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this); // for location updates
        /* Add required location permissions to a list*/
        locationPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        locationPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        addressSearchView = findViewById(R.id.address_search_view);
        setLocationButton = findViewById(R.id.set_location_button);
        setLocationButton.setOnClickListener(this::onSetLocationButtonClick);

        /*  Obtain the SupportMapFragment */
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        /* If permissions were granted, set On Map ready Callback.
         * Else we set this after asking permissions required for enabling user location
         * */
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mapFragment.getMapAsync(this);
        }

        /* Search Address */
        addressSearchView.setOnQueryTextListener(this);
        /*
         * Show address search view hint at all times and hide keyboard
         * source: https://stackoverflow.com/questions/33566780/searchview-query-hint-before-clicking-it
         */
        addressSearchView.setIconified(false);
        addressSearchView.clearFocus();
    }

    /**
     * Return pick up location to parent through an intent.
     *
     * @param view
     */
    private void onSetLocationButtonClick(View view)
    {
        if (pickUpLocation != null)
        {
            /* Return intent with result*/
            Intent returnIntent = new Intent();
            returnIntent.putExtra("pickUpLocation", pickUpLocation);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
        else
        {
            /* Ask to place marker if not placed */
            Toast.makeText(SetLocationActivity.this, "Place a marker on the map to set pickup location", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Take user to parent activity on back button click.
     *
     * @param view
     */
    private void onBackClick(View view)
    {
        super.onBackPressed();
    }

    /**
     * Start location updates or
     * ask for location permissions
     */
    @Override
    protected void onStart()
    {
        super.onStart();
        /* Check settings and start location updates*/
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            checkSettingsAndStartLocationUpdates();
        }
        else
        {
            /* Ask only for the requests that were not granted */
            permissionsToRequest = permissionsToRequest(locationPermissions);
            ActivityCompat.requestPermissions(SetLocationActivity.this,
                    permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                    LOCATION_REQUEST_CODE);
        }

    }

    /**
     * Start location updates if user has GPS turned on.
     * Otherwise, request to turn on GPS
     */
    private void checkSettingsAndStartLocationUpdates()
    {
        try
        {
            // Configure location request
            locationRequest = new LocationRequest();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(UPDATE_INTERVAL);
            locationRequest.setFastestInterval(FASTEST_INTERVAL);

            // Set up client to check GPS availability
            LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest).build();
            SettingsClient client = LocationServices.getSettingsClient(SetLocationActivity.this);

            Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);

            /* On success start location updates*/
            locationSettingsResponseTask.addOnSuccessListener(SetLocationActivity.this, new OnSuccessListener<LocationSettingsResponse>()
            {
                @Override
                public void onSuccess(LocationSettingsResponse locationSettingsResponse)
                {
                    startLocationUpdates();
                }
            });

            /* On failure attempt to resolve exception eg: user has turned of GPS  */
            locationSettingsResponseTask.addOnFailureListener(SetLocationActivity.this, new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    if (e instanceof ResolvableApiException)
                    {
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try
                        {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            ResolvableApiException apiException = (ResolvableApiException) e;
                            apiException.startResolutionForResult(SetLocationActivity.this, LOCATION_REQUEST_CODE);
                        } catch (IntentSender.SendIntentException ex)
                        {
                            Log.d(TAG, "onFailure: " + ex.getMessage());
                        }
                    }
                }
            });

        } catch (Exception e)
        {
            Log.d("Check GPS Settings ", e.getMessage());
        }
    }

    /**
     * Request location updates if permissions were granted
     */
    private void startLocationUpdates()
    {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.myLooper());
    }

    /**
     * Detach location update listener
     */
    private void stopLocationUpdates()
    {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    /**
     * Stop requesting location updates
     */
    @Override
    protected void onStop()
    {
        super.onStop();
        stopLocationUpdates();
    }

    /**
     * If all permissions were granted start location updates.
     * Otherwise show dialog with option to confirm location permission requests denial or
     * provide permissions.
     * Set on map ready callback.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_REQUEST_CODE)
        {
            /* Find the permissions not granted*/
            for (String perm : permissionsToRequest)
            {
                if (!hasPermission(perm))
                {
                    permissionsRejected.add(perm);
                }
            }

            /* Ask for permissions not granted*/
            if (permissionsRejected.size() > 0)
            {
                /* Show Warning Dialog if permission denied and provide option to allow permission again or cancel*/
                if (ActivityCompat.shouldShowRequestPermissionRationale(SetLocationActivity.this, permissionsRejected.get(0)))
                {
                    new AlertDialog.Builder(SetLocationActivity.this)
                            .setMessage("Location permissions are required to view current location.")
                            .setPositiveButton("Allow", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i)
                                {
                                    /* request permissions again */
                                    ActivityCompat.requestPermissions(SetLocationActivity.this, permissionsRejected.toArray(
                                            new String[permissionsRejected.size()]), LOCATION_REQUEST_CODE);
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .create()
                            .show();
                }


            }
            else
            {
                checkSettingsAndStartLocationUpdates();
            }
            mapFragment.getMapAsync(this);        /* Permissions were requested, set On Map ready Callback */
        }
    }

    /**
     * When map is ready set listener to add or move marker.
     * Moves map camera to the default location
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        map = googleMap;
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        map.setOnMapClickListener(this); // Listener to set marker
        map.setOnMarkerDragListener(this);// Listener to drag and move marker

        /* Enable user location if permission granted*/
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            enableUserLocation();
        }

        /* Move Camera to default location */
        LatLng latLng = new LatLng(defaultLatitude, defaultLongitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM_LEVEL));
    }

    /**
     * Todo: Find top padding amount for user location button based on device view height
     * Enables  User Location on Map
     * Ok to SuppressLint as permission checked before call to method
     */
    @SuppressLint("MissingPermission")
    private void enableUserLocation()
    {
        if (map != null)
        {
            map.setMyLocationEnabled(true);
            map.setPadding(0, 500, 0, 0); // Move Get My location button under TextBox
        }
    }

    /**
     * Returns a list of permissions not granted from a list of permissions passed in.
     *
     * @param wantedPermissions the list of permissions required
     * @return list of permissions not yet approved
     */
    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions)
    {
        /* List of permissions not granted */
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions)
        {
            if (!hasPermission(perm))
            {
                result.add(perm);
            }
        }

        return result;
    }

    /**
     * Can check if permission was previously accepted on SDK > M
     * On sdk < M user must have accepted before downloading application.
     *
     * @param perm The permission to check if we have
     */
    private boolean hasPermission(String perm)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            return checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    /**
     * Add new marker on Map click
     *
     * @param latLng map click location
     */
    @Override
    public void onMapClick(LatLng latLng)
    {
        /* Hide Search View keyboard*/
        addressSearchView.clearFocus();

        try
        {
            /* Clear previous markers */
            map.clear();
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Find address
            if (addresses.size() > 0)
            {
                /* Find first address match */
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                addressSearchView.setQuery(streetAddress, false); // update address search_view text

                /* Add new marker*/
                map.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                /* Set Marker as current pickupLocation */
                pickUpLocation = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
                /* Move view to new marker */
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM_LEVEL));
                /* Update Search View Text with Marker address */

            }
        } catch (IOException e)
        {
            Log.d(TAG, "Error Setting new Marker in onMapLongClick " + e.getMessage());
        }
    }

    /**
     * Required to implement GoogleMap.OnMarkerDragListener.
     *
     * @param marker
     */
    @Override
    public void onMarkerDragStart(Marker marker)
    {
    }

    /**
     * Required to implement GoogleMap.OnMarkerDragListener.
     *
     * @param marker
     */
    @Override
    public void onMarkerDrag(Marker marker)
    {
    }

    /**
     * Update marker when it is dropped after position change.
     *
     * @param marker
     */
    @Override
    public void onMarkerDragEnd(Marker marker)
    {
        LatLng latLng = marker.getPosition();
        try
        {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // find address
            if (addresses.size() > 0)
            {
                Address address = addresses.get(0); // get first match
                String streetAddress = address.getAddressLine(0); // find address name
                marker.setTitle(streetAddress); // update marker title
                addressSearchView.setQuery(streetAddress, false); // update address search_view text
                pickUpLocation = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude); // Update pickup location
            }
        } catch (IOException e)
        {
            Log.d(TAG, "Error updating marker title in onMarkerDragEnd");
        }
    }

    /**
     * Add marker on location entered through the search view
     *
     * @param s Required but unused string parameter
     */
    @Override
    public boolean onQueryTextSubmit(String s)
    {
        /* Clear previous markers */
        map.clear();
        /* Extract location String */
        String location = addressSearchView.getQuery().toString();
        /* Find address if Search_View text is not null*/
        if (location != null || !location.equals(""))
        {
            try
            {
                List<Address> addresses = geocoder.getFromLocationName(location, 1); // find address
                if (addresses.size() > 0)
                {
                    Address address = addresses.get(0); // choose first match
                    String streetAddress = address.getAddressLine(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    /* Add marker */
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(streetAddress)
                            .draggable(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, CAMERA_ZOOM_LEVEL)); // move to marker
                }
            } catch (IOException e)
            {
                Log.d(TAG, "Error finding address in onQueryTextSubmit");
            }
        }
        return false;
    }

    /**
     * Required to implement SearchView.OnQueryTextListener
     * Could implement an autocomplete dropdown as address is typed
     */
    @Override
    public boolean onQueryTextChange(String s)
    {
        return false;
    }
}
