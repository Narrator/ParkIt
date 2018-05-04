package com.getparkit.parkit.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.getparkit.parkit.Activities.AsyncDrawerActivities.AddParkingSpaceActivity;
import com.getparkit.parkit.Activities.AsyncDrawerActivities.AddVehicleActivity;
import com.getparkit.parkit.Activities.AsyncDrawerActivities.HomeScreenActivity;
import com.getparkit.parkit.Activities.AsyncDrawerActivities.OwnerHomeScreenActivity;
import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.Classes.AuthenticatedApiClient;
import com.getparkit.parkit.Classes.UserAccess;
import com.getparkit.parkit.Interfaces.GenericListener;

import java.util.List;

import io.swagger.client.api.UserApi;
import io.swagger.client.model.ParkingSpace;
import io.swagger.client.model.Vehicle;
import retrofit2.Call;
import retrofit2.Callback;

public abstract class BaseInjectionActivity extends AppCompatActivity implements GenericListener {

    // Generic Listener
    public GenericListener gl = this;

    public Activity loadingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void checkVehicles(final AuthenticatedApiClient client, final AppCompatActivity act, final Class tar, final UserAccess ua) {
        // Update server too
        UserApi userApi = client.createService(UserApi.class);

        Call<List<Vehicle>> call = userApi.userPrototypeGetVehicles("me","");

        call.enqueue(new Callback<List<Vehicle>>() {
            @Override
            public void onResponse(Call<List<Vehicle>> call, retrofit2.Response<List<Vehicle>> response) {
                if (client.handleError(response, act, tar, ua)) {
                    return;
                }

                List<Vehicle> vehicles = response.body();

                Class target;
                if (vehicles.size() < 1) {
                    target = AddVehicleActivity.class;
                } else {
                    target = HomeScreenActivity.class;
                }
                // No vehicles, send user to add vehicle activity
                Intent i = new Intent(act, target);
                i.putExtra("user-access", ua);
                startActivity(i);

                // close splash activity
                finish();
            }

            @Override
            public void onFailure(Call<List<Vehicle>> call, Throwable t) {
                Log.d("SERVER_ERROR","Error dealing with the response: " + t.getMessage());
                startActivityForResult(new Intent(act, ErrorActivity.class),1);
                finish();
            }
        });
    }

    public void checkParkingSpaces(final AuthenticatedApiClient client, final AppCompatActivity act, final Class tar, final UserAccess ua)  {
        UserApi userApi = client.createService(UserApi.class);
        // Update server too
        Call<List<ParkingSpace>> call = userApi.userPrototypeGetParkingSpaces("me", "");

        call.enqueue(new Callback<List<ParkingSpace>>() {
            @Override
            public void onResponse(Call<List<ParkingSpace>> call, retrofit2.Response<List<ParkingSpace>> response) {
                if (client.handleError(response, act, tar, ua)) {
                    return;
                }

                List<ParkingSpace> parkingSpaces = response.body();

                Class target;
                if (parkingSpaces.size() < 1) {
                    target = AddParkingSpaceActivity.class;
                } else {
                    target = OwnerHomeScreenActivity.class;
                }
                // No vehicles, send user to add vehicle activity
                Intent i = new Intent(act, target);
                i.putExtra("user-access", ua);
                startActivity(i);
                finish();
            }

            @Override
            public void onFailure(Call<List<ParkingSpace>> call, Throwable t) {
                Log.d("SERVER_ERROR","Error dealing with the response: " + t.getMessage());
                startActivityForResult(new Intent(act, ErrorActivity.class),1);
                finish();
            }
        });
    }
}
