package com.kurata.hotelmanagement;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.kurata.hotelmanagement.databinding.ActivityAddLocationBinding;
import com.kurata.hotelmanagement.utils.Constants;
import com.kurata.hotelmanagement.utils.Preference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Add_location extends FragmentActivity implements OnMapReadyCallback {
    FrameLayout map;
    GoogleMap gMap;
    Location currentLocation;
    Marker marker;
    FusedLocationProviderClient fusedClient;
    private static final int REQUEST_CODE = 101;
    private ActivityAddLocationBinding binding;
    private Double Latitude, Longitude;
    private Preference preferenceManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddLocationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //init
        preferenceManager = new Preference(getApplicationContext());

        binding.search.clearFocus();
        Location x = new Location(LocationManager.GPS_PROVIDER);
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        getLocation();

        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String loc = binding.search.getQuery().toString();
                if (loc == null){
                    Toast.makeText(Add_location.this, "Location Not Found", Toast.LENGTH_SHORT).show();
                } else {
                    Geocoder geocoder = new Geocoder(Add_location.this, Locale.getDefault());
                    try {
                        List<Address> addressList = geocoder.getFromLocationName(loc, 1);
                        if (addressList.size() > 0){
                            LatLng latLng = new LatLng(addressList.get(0).getLatitude(),addressList.get(0).getLongitude());
                            if (marker != null){
                                marker.remove();
                            }
                            Latitude = addressList.get(0).getLatitude();
                            Longitude = addressList.get(0).getLongitude();
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(loc);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,20);
                            gMap.animateCamera(cameraUpdate);
                            marker = gMap.addMarker(markerOptions);
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        binding.submit.setOnClickListener(v -> onBackPressed());

    }


    @Override
    public void onBackPressed() {

        Intent i = new Intent();

        i.putExtra("address", binding.search.getQuery().toString());
//        i.putExtra("latitude", Latitude);
//        i.putExtra("longitude", Longitude);
        if(Latitude != null){
            preferenceManager.putString(Constants.LATITUDE, String.valueOf(Latitude));
            preferenceManager.putString(Constants.LONGITUDE, String.valueOf(Longitude));
        }

        setResult(RESULT_OK, i);
        finish();
    }

//    public void search() {
//        binding.search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                String loc = binding.search.getQuery().toString();
//                if (loc == null) {
//                    Toast.makeText(Add_location.this, "Location Not Found", Toast.LENGTH_SHORT).show();
//                } else {
//                    Geocoder geocoder = new Geocoder(Add_location.this, Locale.getDefault());
//                    try {
//                        List<Address> addressList = geocoder.getFromLocationName(loc, 1);
//                        if (addressList.size() > 0) {
//                            LatLng latLng = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
//                            if (marker != null) {
//                                marker.remove();
//                            }
//                            Latitude = addressList.get(0).getLatitude();
//                            Longitude = addressList.get(0).getLongitude();
//                            Log.d("Latiititi", Latitude.toString());
//                            Log.d("LOCATION", latLng.toString());
//                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(loc);
//                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//                            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 20);
//                            gMap.animateCamera(cameraUpdate);
//                            marker = gMap.addMarker(markerOptions);
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                return false;
//            }
//        });
//    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        if(getIntent().getStringExtra("address") != null){
            binding.search.setQuery(getIntent().getStringExtra("address"),true);
            currentLocation = getIntent().getParcelableExtra("location");

            Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
            SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            assert supportMapFragment != null;
            supportMapFragment.getMapAsync(Add_location.this);
        }else{
            Task<Location> task = fusedClient.getLastLocation();
            task.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currentLocation = location;
                        //Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                        assert supportMapFragment != null;
                        supportMapFragment.getMapAsync(Add_location.this);
                    }
                }
            });
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title(binding.search.getQuery().toString());
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20));
        googleMap.addMarker(markerOptions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }


}