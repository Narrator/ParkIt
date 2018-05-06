package com.getparkit.parkit.Activities.AsyncDrawerActivities;


import android.os.Bundle;

import android.util.Log;

import android.view.View;
import android.widget.LinearLayout;

import com.getparkit.parkit.Activities.AsyncDrawerActivity;
import com.getparkit.parkit.Classes.UserAccess;
import com.getparkit.parkit.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class HomeScreenActivity extends AsyncDrawerActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout wrapper = findViewById(R.id.asyncDrawerWrapper);
        View  inflated = getLayoutInflater().inflate(R.layout.activity_home_screen, wrapper, false);

        wrapper.addView(inflated);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        initMap();

    }

    public void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (googleMap != null) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR", "GOOGLE MAPS NOT LOADED");
        }

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        UserAccess ua = this.getIntent().getParcelableExtra("user-access");
    }
    @Override
    public void callback(View view, String result) {

    }
}
