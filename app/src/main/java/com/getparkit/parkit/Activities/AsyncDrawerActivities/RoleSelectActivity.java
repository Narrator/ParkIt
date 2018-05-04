package com.getparkit.parkit.Activities.AsyncDrawerActivities;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.getparkit.parkit.Activities.AsyncDrawerActivity;
import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.Activities.HelperActivities.LoadingActivity;
import com.getparkit.parkit.Activities.SplashActivity;
import com.getparkit.parkit.Async.DownloadImageTask;
import com.getparkit.parkit.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.swagger.client.model.ParkingSpace;
import io.swagger.client.model.Role;
import io.swagger.client.model.Vehicle;
import retrofit2.Call;
import retrofit2.Callback;


public class RoleSelectActivity extends AsyncDrawerActivity {

    private TextView fullName;
    private ImageView profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout wrapper = findViewById(R.id.asyncDrawerWrapper);
        View inflated = getLayoutInflater().inflate(R.layout.activity_role_select, wrapper, false);

        wrapper.addView(inflated);

        profilePic = findViewById(R.id.profile_image);
        fullName = findViewById(R.id.full_name);
    }

    // method is invoked when all async tasks for drawer are done
    // Implement all logic dependent on drawer tasks and variables here..
    @Override
    public void callback(View view, String result) {
        new DownloadImageTask(profilePic)
                .execute(profilePicUrl);
        fullName.setText(fullNameText);
    }


    public void addRole(View v) {

        final String role;

        if (v.getId() == R.id.parker) {
            role = "parker";
        } else {
            role = "psOnwer";
        }

        List<String> roles = ua.getRoles();
        ArrayList<String> updatableRoles = new ArrayList<String>();

        if (!Arrays.asList(roles).contains(role)) {
            updatableRoles.addAll(roles);
            updatableRoles.add(role.toString());
            // Update roles in local cache
            roles = updatableRoles;
            ua.setRoles(roles);
            ua.setCurrentRole(role);
            helper.updateUserAccess(ua);

        }

        // Update server too
        JSONObject postBody = new JSONObject();
        try {
            postBody.put("role", role);
        } catch(Exception e) {
            e.printStackTrace();
        }

        Call<Object> call = userApi.userPrototypeAddRole("me", postBody.toString());
        Intent loading = new Intent(mContext, LoadingActivity.class);
        startActivity(loading);

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                if (client.handleError(response, RoleSelectActivity.this, RoleSelectActivity.class, ua)) {
                    return;
                }
                if (role == "parker") {
                    checkVehicles(client, RoleSelectActivity.this, HomeScreenActivity.class, ua);
                    return;
                } else {
                    checkParkingSpaces(client, RoleSelectActivity.this, OwnerHomeScreenActivity.class, ua);
                    return;
                }

            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d("SERVER_ERROR","Error dealing with the response: " + t.getMessage());
                startActivityForResult(new Intent(RoleSelectActivity.this, ErrorActivity.class),1);
                finish();
            }
        });
    }
}
