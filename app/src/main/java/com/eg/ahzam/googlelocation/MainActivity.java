package com.eg.ahzam.googlelocation;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;


import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {

    private TextView tvPostcode, tvLongLat;
    private Button btnUpdateLocation;

    GoogleApiClient googleApiClient;
    Location lastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // if there is not GoogleApiClient
        if(googleApiClient == null) {
            // Then use the builder to add the LocationServices API
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        tvPostcode = findViewById(R.id.tvPostCode);
        tvLongLat = findViewById(R.id.tvLongLat);

        btnUpdateLocation = findViewById(R.id.btnUpdateLocation);
        btnUpdateLocation.setOnClickListener(new View.OnClickListener() {
            @Override   // When user clicks on the button
            public void onClick(View view) {
                displayLocation();  // Display devices current location
            }
        });

    }


    private void displayLocation() {
        // create a new Geocoder to get device location
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        // if the location permissions in the android manifest is not granted
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);


            // If the device is able to get the long & lat coordinates
            if(lastLocation != null) {
                Toast.makeText(MainActivity.this, R.string.location_updated, Toast.LENGTH_SHORT).show();
                double lat = lastLocation.getLatitude();
                double longitude = lastLocation.getLongitude();
                tvLongLat.setText(lat + ", " + longitude);

                // Address --> Postcode
                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocation(lat, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String postalCode = "";
                Iterator<Address> addressIterator = addresses.iterator();
                // While the address iterator is going through the addressList
                while(addressIterator.hasNext()) {
                    Address add = addressIterator.next();   // New Address object to get next address
                    postalCode = add.getPostalCode();       // the postcode is retrieved from the address

                }

                tvPostcode.setText(postalCode); // sets the postcode on the textview

                // if location is unavailable
            } else {
                Toast.makeText(MainActivity.this, R.string.turn_location_on, Toast.LENGTH_SHORT).show();
                tvPostcode.setText(R.string.location_unavailable);
                tvLongLat.setVisibility(View.INVISIBLE);
                //LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
            }
        }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
    }

    /*
    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, (LocationListener) this);
    }*/

    @Override
    public void onConnectionSuspended(int i) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();      // Connect the googleApiClient
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();   // Disconnect the googleApiClient
        super.onStop();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
