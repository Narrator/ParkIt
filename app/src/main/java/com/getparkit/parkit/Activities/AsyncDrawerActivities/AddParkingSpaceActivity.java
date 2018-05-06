package com.getparkit.parkit.Activities.AsyncDrawerActivities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getparkit.parkit.Activities.AsyncDrawerActivity;
import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.Activities.HelperActivities.LoadingActivity;
import com.getparkit.parkit.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import io.swagger.client.model.GeoPoint;
import io.swagger.client.model.NewParkingSpace;
import io.swagger.client.model.ParkingSpace;
import io.swagger.client.model.Vehicle;
import retrofit2.Call;
import retrofit2.Callback;

public class AddParkingSpaceActivity extends AsyncDrawerActivity {

    private LinearLayout form;
    private EditText address;
    private Place place;
    private int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout wrapper = findViewById(R.id.asyncDrawerWrapper);
        View inflated = getLayoutInflater().inflate(R.layout.activity_add_parking_space, wrapper, false);

        wrapper.addView(inflated);

        form = findViewById(R.id.form);
        address = form.findViewById(R.id.address);

        address.setClickable(false);
        address.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                // first time only....
                if (hasFocus && !address.isClickable()) {
                    address.setText("");
                    address.setTextColor(getResources().getColor(R.color.colorPrimary));
                    address.setClickable(true);
                }
            }
        });

    }

    public void addressSearch(View v) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place1 = PlaceAutocomplete.getPlace(this, data);
                Log.i("PLACE", "Place: " + place1.getName());
                address.setText(place1.getAddress());
                place = place1;
                // EditText stuff...
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("STATUS", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void addParkingSpace(View v) {

        Boolean valid = true;
        form = findViewById(R.id.form);
        int childCount = form.getChildCount();

        for(int i =0; i < childCount; i++) {
            try {
                final EditText editText = (EditText) form.getChildAt(i);
                if (editText.getText().toString().isEmpty()) {

                    editText.setTextColor(getResources().getColor(R.color.colorRed));
                    editText.setActivated(true);
                    editText.setHighlightColor(getResources().getColor(R.color.colorRed));

                    valid = false;
                }
            } catch (Exception e) {
                // ignore...
                e.printStackTrace();
            }
        }

        // ToDo: handle custom addresses... (non google-searched ones)
        if (place == null) {
            valid = false;
        }
        if (!valid) {
            Toast toast = Toast.makeText(getApplicationContext(),"Please fill out all the fields!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        Intent i = new Intent(AddParkingSpaceActivity.this, LoadingActivity.class);
        startActivity(i);

        NewParkingSpace newParkingSpace = new NewParkingSpace();

        GeoPoint location = new GeoPoint();
        location.setLat(BigDecimal.valueOf(place.getLatLng().latitude));
        location.setLng(BigDecimal.valueOf(place.getLatLng().longitude));

        List<Address> addressList = null;

        Geocoder geocoder = new Geocoder(getApplicationContext());

        try {
            addressList = geocoder.getFromLocation(place.getLatLng().latitude, place.getLatLng().longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = addressList.get(0);

        newParkingSpace.setAddress(place.getName().toString());
        newParkingSpace.setCity(address.getLocality());
        newParkingSpace.setCountry(address.getCountryCode());
        newParkingSpace.setState(address.getAdminArea());
        newParkingSpace.setStreetNumber(address.getFeatureName());
        newParkingSpace.setRoute(address.getThoroughfare());
        newParkingSpace.setZipCode(address.getPostalCode());
        newParkingSpace.setLocation(location);
        newParkingSpace.setDefault(true);

        Call<ParkingSpace> call = userApi.userPrototypeCreateParkingSpaces("me", newParkingSpace);

        call.enqueue(new Callback<ParkingSpace>() {
            @Override
            public void onResponse(Call<ParkingSpace> call, retrofit2.Response<ParkingSpace> response) {
                if (client.handleError(response, AddParkingSpaceActivity.this, HomeScreenActivity.class, ua)) {
                    return;
                }

                Intent i = new Intent(AddParkingSpaceActivity.this, OwnerHomeScreenActivity.class);
                i.putExtra("user-access", ua);
                i.putExtra("messagae","Your parking space has been added successfully!");
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(Call<ParkingSpace> call, Throwable t) {
                Log.d("SERVER_ERROR","Error dealing with the response: " + t.getMessage());
                startActivityForResult(new Intent(AddParkingSpaceActivity.this, ErrorActivity.class),1);
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
