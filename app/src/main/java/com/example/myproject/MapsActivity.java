package com.example.myproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myproject.CustomClasses.GetNearbyPlacesData;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    double latitude;
    double longitude;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    private GoogleMap mMap;
    ProgressBar mapProgressBar;
    boolean gotLocation = false;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mapProgressBar = findViewById(R.id.mapsProgressBar);
        mapProgressBar.setVisibility(View.INVISIBLE);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }
            else{
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        }

        Button btnHospitals = findViewById(R.id.btnHospitals);
        btnHospitals.setOnClickListener(new View.OnClickListener() {
            String search = "hospital";
            @Override
            public void onClick(View v) {
                mMap.clear();
                mapProgressBar.setVisibility(View.VISIBLE);
                String url = getUrl(latitude,longitude,search);
                Object[] DataTransfer = new Object[4];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                DataTransfer[2] = "hospital";
                DataTransfer[3] = mapProgressBar;
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(); // find this
                getNearbyPlacesData.execute(DataTransfer);

                //Toast.makeText(MapsActivity.this,"These are your nearby Hospitals!",Toast.LENGTH_LONG).show();
            }
        });

        Button btnTowing = findViewById(R.id.btnTowing);
        btnTowing.setOnClickListener(new View.OnClickListener() {
            String search = "car_repair";
            @Override
            public void onClick(View v) {
                mMap.clear();
                mapProgressBar.setVisibility(View.VISIBLE);
                String url = getUrl(latitude,longitude,search);
                Object[] DataTransfer = new Object[4];
                DataTransfer[0] = mMap;
                DataTransfer[1] = url;
                DataTransfer[2] = "tow";
                DataTransfer[3] = mapProgressBar;
                GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData(); // find this
                getNearbyPlacesData.execute(DataTransfer);
                //Toast.makeText(MapsActivity.this,"These are your nearby Towing Services!",Toast.LENGTH_LONG).show();
            }
        });

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gotLocation)
                {
                    Intent intent = new Intent(MapsActivity.this,LoginActivity.class);
                    Log.v("mapappdata","before sending latitude is: "+latitude);
                    Log.v("mapappdata","before sending longitude is: "+longitude);
                    Double newLat = latitude;
                    Double newLong = longitude;
                    intent.putExtra(LoginActivity.EXTRA_LAT, newLat.toString());
                    intent.putExtra(LoginActivity.EXTRA_LONG, newLong.toString());
                    startActivity(intent);
                }
                else{
                    Toast.makeText(MapsActivity.this,"Please wait to get location!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("mapappdata", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                markerOptions.title("Current Position");
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                gotLocation = true;
                Log.v("mapappdata","latitude is: "+latitude);
                Log.v("mapappdata","longitude is: "+longitude);

                if (mFusedLocationClient != null) {
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                }
            }
        }
    };

    private String getUrl(double latitude,double longitude,String nearbyPlace){
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location="+latitude+","+longitude);
        googlePlacesUrl.append("&type="+nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&rankby=distance");
        googlePlacesUrl.append("&key="+"AIzaSyDvEj4a2OGfQtkbFZTl35rx54ACwzVF1UQ"); // check what the correct key is here
        Log.d("mapapp",googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }

    public void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        new AlertDialog.Builder(this)
                                .setTitle("Location Permission Needed")
                                .setMessage("This app needs the Location permission, please accept to use location functionality")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //Prompt the user once explanation has been shown
                                        ActivityCompat.requestPermissions(MapsActivity.this,
                                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                MY_PERMISSIONS_REQUEST_LOCATION );
                                    }
                                })
                                .create()
                                .show();

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
