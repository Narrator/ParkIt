package com.getparkit.parkit.Activities.AsyncDrawerActivities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getparkit.parkit.Activities.AsyncDrawerActivity;
import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.Async.DownloadImageTask;
import com.getparkit.parkit.Classes.AuthenticatedApiClient;
import com.getparkit.parkit.Classes.UserAccess;
import com.getparkit.parkit.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.client.api.UserApi;
import io.swagger.client.model.ParkingSpace;
import retrofit2.Call;
import retrofit2.Callback;

public class OwnerHomeScreenActivity extends AsyncDrawerActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout wrapper = findViewById(R.id.asyncDrawerWrapper);
        View inflated = getLayoutInflater().inflate(R.layout.activity_owner_home_screen, wrapper, false);

        wrapper.addView(inflated);

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

        client = new AuthenticatedApiClient(ua.getAccessToken());

        userApi = client.createService(UserApi.class);

        Call<List<ParkingSpace>> call = userApi.userPrototypeGetParkingSpaces("me","");

        call.enqueue(new Callback<List<ParkingSpace>>() {
            @Override
            public void onResponse(Call<List<ParkingSpace>> call, retrofit2.Response<List<ParkingSpace>> response) {
                if (client.handleError(response, OwnerHomeScreenActivity.this, OwnerHomeScreenActivity.class, ua)) {
                    return;
                }

                List<ParkingSpace> parkingSpaces = response.body();
                ParkingSpace defaultPs = null;

                for (ParkingSpace parkingSpace: parkingSpaces) {
                    if (parkingSpace.isDefault()) {
                        defaultPs = parkingSpace;
                        break;
                    }
                }

                // Add a marker in Sydney and move the camera
                LatLng psLatLng = new LatLng(defaultPs.getLocation().getLat().doubleValue(),
                        defaultPs.getLocation().getLng().doubleValue());
                mMap.addMarker(new MarkerOptions().position(psLatLng).title(defaultPs.getAddress()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(psLatLng, 15));

                TextView parkingSpaceName = findViewById(R.id.parkingSpaceName);

                String addressText = "Your parking garage at: " + defaultPs.getAddress();
                parkingSpaceName.setText(addressText);
            }

            @Override
            public void onFailure(Call<List<ParkingSpace>> call, Throwable t) {
                Log.d("SERVER_ERROR","Error dealing with the response: " + t.getMessage());
                startActivityForResult(new Intent(OwnerHomeScreenActivity.this, ErrorActivity.class),1);
                finish();
            }
        });
    }

    // method is invoked when all async tasks for drawer are done
    // Implement all logic dependent on drawer tasks and variables here..
    @Override
    public void callback(View view, String result) {
    }


}
