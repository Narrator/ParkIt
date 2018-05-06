package com.getparkit.parkit.Activities.AsyncDrawerActivities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getparkit.parkit.Activities.AsyncDrawerActivity;
import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.Activities.HelperActivities.LoadingActivity;
import com.getparkit.parkit.Activities.LoginActivity;
import com.getparkit.parkit.R;

import java.util.ArrayList;
import java.util.List;

import io.swagger.client.api.ParkingSpaceApi;
import io.swagger.client.model.NewSpot;
import io.swagger.client.model.NewVehicle;
import io.swagger.client.model.ParkingSpace;
import io.swagger.client.model.Spot;
import io.swagger.client.model.Vehicle;
import retrofit2.Call;
import retrofit2.Callback;

public class AddSpotActivity extends AsyncDrawerActivity {

    private LinearLayout form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout wrapper = findViewById(R.id.asyncDrawerWrapper);
        View inflated = getLayoutInflater().inflate(R.layout.activity_add_spot, wrapper, false);

        wrapper.addView(inflated);

        form = findViewById(R.id.form);
        int childCount = form.getChildCount();

        for(int i =0; i < childCount; i++) {
            try {
                final EditText editText = (EditText) form.getChildAt(i);
                editText.setClickable(false);
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean hasFocus) {
                        // first time only....
                        if (hasFocus && !editText.isClickable()) {
                            editText.setText("");
                            editText.setTextColor(getResources().getColor(R.color.colorPrimary));
                            editText.setClickable(true);
                        }
                    }
                });
            } catch (Exception e) {
                // ignore...
                e.printStackTrace();
            }
        }
    }

    public void addSpot(View v) {

        Boolean valid = true;
        form = findViewById(R.id.form);
        int childCount = form.getChildCount();

        for(int i =0; i < childCount; i++) {
            try {
                final EditText editText = (EditText) form.getChildAt(i);
                if (editText.getText().toString().isEmpty()) {
                    editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
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

        Intent i = new Intent(AddSpotActivity.this, LoadingActivity.class);
        startActivity(i);

        Intent gI = getIntent();
        String parkingSpaceId = gI.getStringExtra("parkingSpaceId");
        NewSpot newSpot = new NewSpot();

        newSpot.setDescription(((EditText) findViewById(R.id.description)).getText().toString());
        newSpot.setZone(Double.parseDouble(((EditText) findViewById(R.id.spotNo)).getText().toString()));

        // Get the parking spots of the parking space, other show text asking user to add.
        ParkingSpaceApi parkingSpaceApi = client.createService(ParkingSpaceApi.class);

        Call<Spot> spotCall = parkingSpaceApi.parkingSpacePrototypeCreateSpots(parkingSpaceId,newSpot);

        spotCall.enqueue(new Callback<Spot>() {
            @Override
            public void onResponse(Call<Spot> call, retrofit2.Response<Spot> response) {
                if (client.handleError(response, AddSpotActivity.this, OwnerHomeScreenActivity.class, ua)) {
                    return;
                }

                Intent i = new Intent(AddSpotActivity.this,OwnerHomeScreenActivity.class);
                i.putExtra("user-access", ua);
                i.putExtra("messagae","Your spot has been added successfully!");
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(Call<Spot> call, Throwable t) {
                Log.d("SERVER_ERROR","Error dealing with the response: " + t.getMessage());
                startActivityForResult(new Intent(AddSpotActivity.this, ErrorActivity.class),1);
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
