package ca.ualberta.cmput301f20t04.bookatmenow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GeoLocation extends AppCompatActivity implements OnMapReadyCallback {

    private Button setGeoLocPickup;
    private Button cancelPickupLocSet;

    private GoogleMap map;
    private LatLng selectedLocation;
    private boolean viewingMap;

    private Intent mapType;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private CameraPosition cameraPosition;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);//for testing
    private static final int DEFAULT_ZOOM = 15;
    private static final String TAG = GeoLocation.class.getSimpleName();

    /*
    We should have a formula, I was thinking <activity_type_name> so as an example: <setNewUser_button_saveAndExit>
    */

    public void setPickupLocation(View view) {//return selectedLocation via intent listener
        if (selectedLocation == null){//no location was selected
            Toast toast = Toast.makeText(this, "Select a location first", Toast.LENGTH_SHORT);
            toast.show();//tell user improper format
        } else {//location was selected. Return values
            Log.i("AppInfo", "Before intent send: " + String.valueOf(selectedLocation.latitude) + ", " + String.valueOf(selectedLocation.longitude));
            Intent returnData = new Intent();
            returnData.putExtra("lat", selectedLocation.latitude);
            returnData.putExtra("lng", selectedLocation.longitude);
            setResult(Activity.RESULT_OK, returnData);
            this.finish();//close activity
        }
    }

    public void cancel(View view) {//cancel setting pickup location, or pressing back on location view screen
        this.finish();//close activity
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }*/

        setContentView(R.layout.activity_geo_location);

        //mapView = findViewById(R.id.pickupLocationMapView);
        setGeoLocPickup = findViewById(R.id.GeoLocation_button_setPickupLoc);
        cancelPickupLocSet = findViewById(R.id.GeoLocation_button_cancel);

        mapType = getIntent();
        viewingMap = false;

        if (mapType.getStringExtra("purpose").equals("view") ){//we are viewing the map, not setting a location
            setGeoLocPickup.setVisibility(View.GONE);
            cancelPickupLocSet.setText("BACK");
            viewingMap = true;
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.GeoLocation_fragment_map);
        mapFragment.getMapAsync(this);


        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();


    }//onCreate end
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Handles the result of the request for location permissions.
     */
    // [START maps_current_place_on_request_permissions_result]
    @Override
    public void onRequestPermissionsResult(int requestCode,//needs permission to get users location
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {//after map loads
        this.map = googleMap;

        if (viewingMap == false){//we are wanting to select a location
            this.map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {//user clicks on map
                @Override
                public void onMapClick(LatLng latLng) {
                    map.clear();//clears map of markers
                    selectedLocation = latLng;
                    map.addMarker(new MarkerOptions()//set marker
                            .position(latLng)
                            .title("Pickup Location"));
                }
            });
        }


    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

}