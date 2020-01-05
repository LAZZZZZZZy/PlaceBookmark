package edu.asu.msse.jwang512.placedatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity
        implements OnMapReadyCallback {
    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private double elevation;
    private String name;
    private boolean isPermisionAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent intent = getIntent();
        android.util.Log.w(this.getClass().getSimpleName(),"is" + intent.getBooleanExtra("Locate",true));
        getLocationPermission();
        if(intent.getBooleanExtra("Locate",false) != false)
        {
            getCurrentLocation();
        }else {
            longitude = Double.parseDouble(intent.getStringExtra("Longitude"));
            latitude = Double.parseDouble(intent.getStringExtra("Latitude"));
            name = intent.getStringExtra("Name");
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isPermisionAccess = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        android.util.Log.w(this.getClass().getSimpleName(),"code " + requestCode);
        if (requestCode == 1) {
            isPermisionAccess = true;
        }
    }

    @SuppressLint("MissingPermission")
    public void getCurrentLocation() {
        android.util.Log.w(this.getClass().getSimpleName(),"get current location");
        if (isPermisionAccess) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (locationManager != null) {
                Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location netLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (gpsLocation != null) {
                    latitude = gpsLocation.getLatitude();
                    longitude = gpsLocation.getLongitude();
                    elevation = gpsLocation.getAltitude();
                } else if (netLocation != null) {
                    latitude = netLocation.getLatitude();
                    longitude = netLocation.getLongitude();
                    elevation = netLocation.getAltitude();
                }
            }
        }
        else
        {
            finish();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(latitude,longitude );
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in " + name));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("Longitude",longitude);
        intent.putExtra("Latitude",latitude);
        intent.putExtra("Elevation",elevation);
        this.setResult(1, intent);
        this.finish();
    }
}

