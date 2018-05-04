package com.getparkit.parkit.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.getparkit.parkit.Activities.AsyncDrawerActivities.AddParkingSpaceActivity;
import com.getparkit.parkit.Activities.AsyncDrawerActivities.AddVehicleActivity;
import com.getparkit.parkit.Activities.AsyncDrawerActivities.HomeScreenActivity;
import com.getparkit.parkit.Activities.AsyncDrawerActivities.OwnerHomeScreenActivity;
import com.getparkit.parkit.Activities.AsyncDrawerActivities.RoleSelectActivity;
import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.Activities.HelperActivities.LoadingActivity;
import com.getparkit.parkit.Classes.AuthenticatedApiClient;
import com.getparkit.parkit.Classes.UserAccess;
import com.getparkit.parkit.SQLite.Helper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.List;

import io.swagger.client.api.UserApi;
import io.swagger.client.model.ParkingSpace;
import io.swagger.client.model.Spot;
import io.swagger.client.model.Vehicle;
import retrofit2.Call;
import retrofit2.Callback;

public class SplashActivity extends BaseInjectionActivity {
    // Contexts, helpers and handlers
    public Context mContext = this;
    public Helper helper = new Helper(mContext);
    public Handler handler = new Handler();

    // SQLITE - local cache object
    public UserAccess ua;

    // API Client objects
    public AuthenticatedApiClient client;
    public UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context mContext = this;
        // Start home activity
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Updateing UserAccess every app opn
                ua = helper.searchUserAccess();

                // If the cache has userId and accessToken, make the call:
                if ((ua.getUserId() != null) && (ua.getAccessToken() != null)) {

                    client = new AuthenticatedApiClient(ua.getAccessToken());

                    userApi = client.createService(UserApi.class);

                    //Call<User> call = apiInstance.userFindById(String.valueOf(ua.getUserId().intValue()), "");
                    Call<Object> call = userApi.userPrototypeGetUser(ua.getUserId().toString());

                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                            if (client.handleError(response, SplashActivity.this, RoleSelectActivity.class, ua)) {
                                return;
                            }
                            try {
                                Gson gson = new Gson();
                                JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                                JSONObject jObj = new JSONObject(jsonObject.toString());

                                // Handle current role from cache
                                if (ua.getCurrentRole() != null) {
                                    jObj.put("currentRole", ua.getCurrentRole());
                                } else {
                                    jObj.put("currentRole", "");
                                }

                                // Update in SQLite (update)
                                UserAccess updatedUa = new UserAccess(jObj);
                                updatedUa.setId(ua.getId());
                                Helper helper = new Helper(mContext);

                                ua = helper.updateUserAccess(ua);

                                Log.d("roles", ua.getRoles().toString());

                                List<String> roles = ua.getRoles();
                                String role = ua.getCurrentRole();

                                if (roles.size() < 1 || role.isEmpty()) {
                                    // No current role, ask for it:
                                    Intent i = new Intent(SplashActivity.this, RoleSelectActivity.class);
                                    i.putExtra("user-access", ua);
                                    startActivity(i);
                                    finish();
                                } else if (role.equals("parker")) {
                                    checkVehicles(client, SplashActivity.this, HomeScreenActivity.class, ua);
                                    return;
                                } else {
                                    checkParkingSpaces(client, SplashActivity.this, OwnerHomeScreenActivity.class, ua);
                                    return;
                                }
                            } catch  (Exception e) {
                                Log.d("SQLITE_ERROR","Error dealing with the UserAccess object. :" + e.getMessage());
                                startActivityForResult(new Intent(SplashActivity.this, ErrorActivity.class),1);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            Log.d("SERVER_ERROR","Error dealing with the response: " + t.getMessage());
                            startActivityForResult(new Intent(SplashActivity.this, ErrorActivity.class),1);
                            finish();
                        }
                    });
                    return;
                }

                Log.d("Here","here");
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                // close splash activity
                finish();
            }
        }, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {

            if(resultCode == RESULT_OK){
                startActivity(this.getIntent());
            }
            if (resultCode == RESULT_CANCELED) {
                //Do nothing?
            }
        }
    }//onActivityResult

    @Override
    public void callback(View view, String result) {

    }
}
