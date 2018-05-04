package com.getparkit.parkit.Activities.AsyncDrawerActivities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.getparkit.parkit.Activities.AsyncDrawerActivity;
import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.R;

import java.util.ArrayList;
import java.util.List;

import io.swagger.client.model.NewVehicle;
import io.swagger.client.model.Vehicle;
import retrofit2.Call;
import retrofit2.Callback;

public class AddVehicleActivity extends AsyncDrawerActivity {

    private LinearLayout form;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout wrapper = findViewById(R.id.asyncDrawerWrapper);
        View inflated = getLayoutInflater().inflate(R.layout.activity_add_vehicle, wrapper, false);

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

    public void addVehicle(View v) {

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
                if (client.handleError(response, AddVehicleActivity.this, HomeScreenActivity.class, ua)) {
                    return;
                }

                Intent i = new Intent(AddVehicleActivity.this,HomeScreenActivity.class);
                i.putExtra("user-access", ua);
                i.putExtra("messagae","Your vehicle has been added successfully!");
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(Call<Vehicle> call, Throwable t) {
                Log.d("SERVER_ERROR","Error dealing with the response: " + t.getMessage());
                startActivityForResult(new Intent(AddVehicleActivity.this, ErrorActivity.class),1);
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
