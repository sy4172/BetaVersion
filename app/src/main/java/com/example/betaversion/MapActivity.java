package com.example.betaversion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView addressTV, locationTV;
    String address;

    MapView mapView;
    GoogleMap gmap;
    Bundle mapViewBundle;

    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    FusedLocationProviderClient fusedLocationProviderClient;

    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).hide();
        actionBar.setHomeButtonEnabled(true);

        mapView = findViewById(R.id.mapView);
        addressTV = findViewById(R.id.addressTV);
        locationTV = findViewById(R.id.locationTV);

        Intent gi = getIntent();
        address = gi.getStringExtra("address");
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED); //Disable Screen Rotation

        mapViewBundle = null;

        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView.onCreate(mapViewBundle);
        mapView.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            turnGPSOn(this);
        }
        addressTV.setText("לחץ על הכפתור כדי לקבל את הכתובת"); // First Message to guide the user.
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }

    public static void turnGPSOn(Context context) {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        com.google.android.gms.tasks.Task<LocationSettingsResponse> result =
                LocationServices.getSettingsClient(context).checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {

            @Override
            public void onComplete(com.google.android.gms.tasks.Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        (Activity) context,
                                        101);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            }
        });
    }

    public void turnGPSOff(){
       try {
           String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_MODE);

           if(provider.contains("gps")){ //if gps is enabled
               final Intent poke = new Intent();
               poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
               poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
               poke.setData(Uri.parse("3"));
               sendBroadcast(poke);
           }
       } catch (Exception e){
           e.printStackTrace();
       }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
        turnGPSOn(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        turnGPSOff();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        turnGPSOn(this);

        gmap = googleMap;
        gmap.setMaxZoomPreference(21);
        gmap.setMinZoomPreference(0);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        }

        gmap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public boolean onMyLocationButtonClick() {
                if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    turnGPSOn(MapActivity.this);

                    try {
                        gmap.clear();
                        Geocoder geocoder = new Geocoder(MapActivity.this);

                        Location location = gmap.getMyLocation();
                        if(location != null){
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Locale.setDefault(Locale.forLanguageTag("he"));
                            List<Address> addresses = geocoder.getFromLocation(latitude,longitude,1);

                            LatLng cl = new LatLng(latitude, longitude);

                            MarkerOptions markerOptions = new MarkerOptions();
                            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(Color.BLUE));
                            markerOptions.position(cl);
                            gmap.addMarker(markerOptions);

                            float zoomLevel = 17.0f; //This goes up to 21
                            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(cl, zoomLevel));

                            locationTV.setText(addresses.get(0).getAddressLine(0));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MapActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                    }

                    turnGPSOff();
                } else {
                    ActivityCompat.requestPermissions(MapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
                return false;
            }
        });
        gmap.setIndoorEnabled(true);

        UiSettings uiSettings = gmap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        LatLng isr = new LatLng(31.014582403708065, 34.815507205709196);
        gmap.moveCamera(CameraUpdateFactory.newLatLng(isr));

        float zoomLevel = 7.0f; //This goes up to 21f
        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(isr, zoomLevel));

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @SuppressLint("MissingPermission")
    public void displayAddress(View view) {
        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    Geocoder geocoder = new Geocoder(MapActivity.this);
                    try {
                        Locale.setDefault(Locale.forLanguageTag("he"));
                        List<Address> addressList = geocoder.getFromLocationName(address, 6);
                        Address user_address = addressList.get(0);

                        LatLng latLng = new LatLng(user_address.getLatitude(), user_address.getLongitude());

                        gmap.clear();
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        gmap.addMarker(markerOptions);

                        float zoomLevel = 17.0f; //This goes up to 21
                        gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

                        addressTV.setText(user_address.getAddressLine(0));
                    } catch (Exception e) {
                        Toast.makeText(MapActivity.this, "Error Address!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void moveToPreviousAct(View view) {
        super.onBackPressed();
    }

    /**
     * Logout method works when the button in the up toolBar was clicked, this method disconnects the current user in the system of the FireBase authentication.
     *
     * @param item the item at the topBar
     */
    public void Logout(MenuItem item) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final TextView titleTV = new TextView(this);
        titleTV.setText("יציאה ממערכת");
        titleTV.setTextColor(Color.rgb(143, 90, 31));
        titleTV.setTextSize(25);
        titleTV.setPadding(0,15,30,15);
        titleTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_semibold));
        adb.setCustomTitle(titleTV);

        final TextView messageTV = new TextView(this);
        messageTV.setText("החשבון "+FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf("@"))+" ינותק.");
        messageTV.setTextSize(18);
        messageTV.setGravity(Gravity.RIGHT);
        messageTV.setPadding(0,15,30,0);
        messageTV.setTypeface(ResourcesCompat.getFont(titleTV.getContext(), R.font.rubik_medium));
        adb.setView(messageTV);

        adb.setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        adb.setPositiveButton("צא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Intent si = new Intent(MapActivity.this, LoginActivity.class);

                // Changing the preferences to default
                SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("stayConnect",false);
                editor.apply();

                startActivity(si);
                finish();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }
}