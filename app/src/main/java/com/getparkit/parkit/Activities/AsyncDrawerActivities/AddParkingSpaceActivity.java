package com.getparkit.parkit.Activities.AsyncDrawerActivities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getparkit.parkit.Activities.AsyncDrawerActivity;
import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.Async.DownloadImageTask;
import com.getparkit.parkit.Classes.UserAccess;
import com.getparkit.parkit.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import io.swagger.client.model.NewVehicle;
import io.swagger.client.model.Vehicle;
import retrofit2.Call;
import retrofit2.Callback;

public class AddParkingSpaceActivity extends AsyncDrawerActivity {

    private LinearLayout form;
    private GoogleMap mMap;
    private EditText address;
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
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("PLACE", "Place: " + place.getName());
                address.setText(place.getAddress());
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
        if (!valid) {
            Toast toast = Toast.makeText(getApplicationContext(),"Please fill out all the fields!", Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        NewVehicle newVehicle = new NewVehicle();

        newVehicle.setMake(((EditText) findViewById(R.id.make)).getText().toString());
        newVehicle.setModel(((EditText) findViewById(R.id.model)).getText().toString());
        newVehicle.setYear(((EditText) findViewById(R.id.year)).getText().toString());
        newVehicle.setLicensePlate(((EditText) findViewById(R.id.license)).getText().toString());
        newVehicle.setState(((EditText) findViewById(R.id.state)).getText().toString());
        newVehicle.setColor(((EditText) findViewById(R.id.color)).getText().toString());

        Call<Vehicle> call = userApi.userPrototypeCreateVehicles("me", newVehicle);

        call.enqueue(new Callback<Vehicle>() {
            @Override
            public void onResponse(Call<Vehicle> call, retrofit2.Response<Vehicle> response) {
                if (client.handleError(response, AddParkingSpaceActivity.this, HomeScreenActivity.class, ua)) {
                    return;
                }

                Intent i = new Intent(AddParkingSpaceActivity.this,HomeScreenActivity.class);
                i.putExtra("user-access", ua);
                i.putExtra("messagae","Your vehicle has been added successfully!");
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
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
