package com.example.khalid_elnagar.locationappsample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity

{
    private final String LOG_TAG = "MainActivity";

    private final int LOCATION_PERMISSION_REQUEST = 0;
    private final String REQUESTING_LOCATION_UPDATE_KEY = "requestUpdate";

    private FusedLocationProviderClient mfusedLocationClient;
    private Location mCurrentLocation;
    private boolean mRequestingLocationUpdates;
    private LocationRequest mLocationRequest;
    private LocationCallback mlocationcallback;
    //views
    private TextView mlatitudeTxt;
    private TextView mlongitudeTxt;

    private Switch mLocationSwitch;

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            mRequestingLocationUpdates = false;

        } else//update the requesting value from the bundle
            if (savedInstanceState.containsKey(REQUESTING_LOCATION_UPDATE_KEY))
                mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATE_KEY);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateValuesFromBundle(savedInstanceState);
        mlatitudeTxt = findViewById(R.id.latitude_txt);
        mlongitudeTxt = findViewById(R.id.longitude_txt);
        createLocationRequest();
        createLocationCallback();


        mfusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        /**
         * {@link SDK_INT} is the android api integer value of the current device
         * {@link M} =  23 , is the Android 6.0 Marshmallow api
         * so if the current device is marshmallow or more
         * it will Requires a runtime permission for using the {@link LocationServices}
         * */
        if (SDK_INT >= M) {//ask for permission to use get the Location
            //if not granted request it
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);

            } else {//if granted get the last Location
                getLastLocation();
            }
        } else {
            //if the android device is less than the marshmallow
            // get the location without requesting the permission
            getLastLocation();
        }
        initializeSwitch();

    }

    private void initializeSwitch() {
        mLocationSwitch = findViewById(R.id.location_switch);
        mLocationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mRequestingLocationUpdates = isChecked;
                if (isChecked) {
                    Log.v(LOG_TAG, "switch is true ");
                    startLocationUpdate();
                } else {
                    stopLocationUpdates();
                    Log.v(LOG_TAG, "switch is false");
                }
            }
        });
    }

    private void createLocationCallback() {
        mlocationcallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Log.v(LOG_TAG, " Location Result" + locationResult);
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    mCurrentLocation = location;

                    mlatitudeTxt.setText(String.valueOf(location.getLatitude()));
                    mlongitudeTxt.setText(String.valueOf(location.getLongitude()));
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates) {
            startLocationUpdate();
        }
    }

    private void startLocationUpdate() {
        Log.v(LOG_TAG, "start Location request");

        mfusedLocationClient.requestLocationUpdates(mLocationRequest, mlocationcallback, null)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.v(LOG_TAG, "the request updates is success");
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.v(LOG_TAG, e.getMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        Log.v(LOG_TAG, "stop Location request");
        mfusedLocationClient.removeLocationUpdates(mlocationcallback);


    }

    /**
     * get the last Location
     */
    private void getLastLocation() {

        mfusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.v(LOG_TAG, "last location is here");
                            mCurrentLocation = location;
                            mlatitudeTxt.setText(String.valueOf(location.getLatitude()));
                            mlongitudeTxt.setText(String.valueOf(location.getLongitude()));

                        } else {
                            Log.v(LOG_TAG, "the Location object is null");
                        }
                    }
                });

    }

    protected void createLocationRequest() {
        //create  the location request
        mLocationRequest = new LocationRequest();
        // 1000 ms == 1 second
        mLocationRequest.setInterval(1000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getLastLocation();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }


        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATE_KEY, mRequestingLocationUpdates);

        super.onSaveInstanceState(outState);
    }
}
