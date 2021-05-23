package com.example.restaurantmapapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.restaurantmapapp.data.DatabaseHelper;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class AddNewPlaceActivity extends AppCompatActivity {
    public static final String TAG = "Running ";
    DatabaseHelper db;
    LocationManager locationManager;
    LocationListener locationListener;
    Place placeToSave;
    AutocompleteSupportFragment autocompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_place);
        EditText placeNameEditText = findViewById(R.id.placeNameEditText);
        Button getCurrentLocationButton = findViewById(R.id.getCurrentLocationButton);
        Button showOnMapButton = findViewById(R.id.showOnMapButton);
        Button saveButton = findViewById(R.id.saveButton);
        db = new DatabaseHelper(this);
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getString(R.string.API_KEY));

        // Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
//                Toast.makeText(AddNewPlaceActivity.this, "Place: " + place.getName() + ", " + place.getLatLng(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(AddNewPlaceActivity.this, place.getLatLng().latitude + "," + place.getLatLng().longitude, Toast.LENGTH_SHORT).show();
//                Toast.makeText(AddNewPlaceActivity.this, place.getAddress(), Toast.LENGTH_SHORT).show();

                placeToSave = place;
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
            }
        });

        getCurrentLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placeToSave.getLatLng().latitude != 0 && placeToSave.getLatLng().longitude != 0) {
                    autocompleteFragment.setText(placeToSave.getAddress());
                } else {
                    Toast.makeText(AddNewPlaceActivity.this, "Please select a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        showOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placeToSave.getLatLng().latitude != 0 && placeToSave.getLatLng().longitude != 0) {
                    Intent intent = new Intent(AddNewPlaceActivity.this, MapsActivity.class);
                    intent.putExtra("Latitude", placeToSave.getLatLng().latitude);
                    intent.putExtra("Longitude", placeToSave.getLatLng().longitude);
                    intent.putExtra("Name", placeNameEditText.getText().toString());
                    startActivity(intent);
                } else {
                    Toast.makeText(AddNewPlaceActivity.this, "Please select a location", Toast.LENGTH_LONG).show();
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (placeToSave.getLatLng().latitude != 0 && placeToSave.getLatLng().longitude != 0) {
                    if (!placeNameEditText.getText().toString().isEmpty()) {
                        com.example.restaurantmapapp.model.Location location = new com.example.restaurantmapapp.model.Location();
                        location.setName(placeNameEditText.getText().toString());
                        location.setLatitude("" + placeToSave.getLatLng().latitude);
                        location.setLongitude("" + placeToSave.getLatLng().longitude);
                        long result = db.insertLocation(location);
                        if (result > -1) {
                            Toast.makeText(AddNewPlaceActivity.this, "Add location successfully", Toast.LENGTH_LONG).show();
                            Intent backToMainIntent = new Intent(AddNewPlaceActivity.this, MainActivity.class);
                            startActivity(backToMainIntent);
                        } else {
                            Toast.makeText(AddNewPlaceActivity.this, "Fail to add a new location", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(AddNewPlaceActivity.this, "Please enter a location name", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AddNewPlaceActivity.this, "Please select a location", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}