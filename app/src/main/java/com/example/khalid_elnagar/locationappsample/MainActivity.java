package com.example.khalid_elnagar.locationappsample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends AppCompatActivity

{
    private final String LOG_TAG = "MainActivity";

    private final int LOCATION_PERMISSION_REQUEST = 0;
    private FusedLocationProviderClient mfusedLocationClient;

    private TextView locationTxt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationTxt = findViewById(R.id.location_txt);

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
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST);

            } else {//if granted get the last Location
                getLastLocation();
            }
        } else {
            //if the android device is less than the marshmallow
            // get the location without requesting the permission
            getLastLocation();
        }


    }

    /**
     * get the last Location
     */
    private Task<Location> getLastLocation() {
        Task<Location> locationTask = mfusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            locationTxt.setText(location.toString());

                        } else {
                            Log.v(LOG_TAG, "the Location object is null");
                        }
                    }
                });
        return locationTask;
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
}
