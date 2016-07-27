package com.purview.activities.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.purview.R;
import com.purview.activities.AddEvent;
import com.purview.entities.Event;
import com.purview.temp.Constants;
import com.purview.utils.GPSTracker;

public class Map extends SupportMapFragment implements OnMapReadyCallback {

    private GoogleMap map;
    private int LOCATION_PERMISSION = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.map = googleMap;
        UiSettings mapSettings = map.getUiSettings();
        mapSettings.setMapToolbarEnabled(false);

        if(!checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) || !checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            accessPermissions();
        }

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Intent intent = new Intent(getContext(), AddEvent.class);
                intent.putExtra("lat", latLng.latitude);
                intent.putExtra("lng", latLng.longitude);

                startActivity(intent);
            }
        });
        initialize();
    }

    private void initialize() {
        if(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) || checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            map.setMyLocationEnabled(true);
            loadEvents();
            goToMyLocation();
        }
    }

    private void loadEvents() {
        for(Event e : Constants.events) {
            addMarker(new LatLng(e.getLocation().getLatitude(), e.getLocation().getLongitude()), BitmapDescriptorFactory.HUE_BLUE);
            Log.d("EVENT LOADED: ", e.getName());
        }
    }

    private void goToMyLocation() {
        Location location = getMyLocation(map);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f);

        map.animateCamera(cameraUpdate);
        GoogleMap.OnMapClickListener listener = new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarker(latLng, BitmapDescriptorFactory.HUE_RED);
            }
        };

        map.setOnMapClickListener(listener);
    }

    private void addMarker(LatLng location, float color) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location).icon(BitmapDescriptorFactory.defaultMarker(color)).alpha(0.6f);
        map.addMarker(markerOptions);
    }

    private Location getMyLocation(GoogleMap map) {
        GPSTracker tracker = new GPSTracker(getContext());
        return tracker.getLocation();
    }

    private void accessPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION);
    }

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

}

