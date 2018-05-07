package com.getparkit.parkit.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.Async.DownloadImageTask;
import com.getparkit.parkit.Classes.AuthenticatedApiClient;
import com.getparkit.parkit.Classes.UserAccess;
import com.getparkit.parkit.Interfaces.GenericListener;
import com.getparkit.parkit.R;
import com.getparkit.parkit.SQLite.Helper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.swagger.client.api.UserApi;
import retrofit2.Call;
import retrofit2.Callback;

public abstract class AsyncDrawerActivity extends BaseInjectionActivity {

    // Contexts, helpers and handlers
    public Context mContext = this;
    public Helper helper = new Helper(mContext);
    public Handler handler = new Handler();

    // List of Async Calls
    public List<Call<Object>> objCalls = new ArrayList<>();

    // SQLITE - local cache object
    public UserAccess ua;

    // API Client objects
    public AuthenticatedApiClient client;
    public UserApi userApi;

    // Navigation view (Items on the left panel)
    public NavigationView navigationView;
    public View navHeader;

    public DrawerLayout mDrawerLayout;
    public TextView drawer_fullName;
    public TextView drawer_email;
    public ImageView drawer_profilePic;

    // Helper variables
    public String profilePicUrl;
    public String email;
    public String firstName;
    public String lastName;
    public String fullNameText;

    public Button burgerMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_drawer);

        // First destroy loading activity
        if (loadingActivity != null && !loadingActivity.isDestroyed()) {
            loadingActivity.finish();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mDrawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);

        // Get the UserAccess object from the previous intent.
        Intent i = getIntent();
        ua = i.getParcelableExtra("user-access");

        // Hide the park button
        ImageView changeRoleView = navigationView.findViewById(R.id.change_role);
        Button parkButton = findViewById(R.id.park);

        if (ua.getCurrentRole().equals("parker")) {
            changeRoleView.setImageDrawable(getDrawable(R.drawable.dollar_icon));
        } else {
            changeRoleView.setImageDrawable(getDrawable(R.drawable.parker_icon));
            parkButton.setVisibility(View.INVISIBLE);
        }

        navigationView.setItemIconTintList(null);

        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                // set item as selected to persist highlight
                menuItem.setChecked(true);
                // close drawer when item is tapped
                mDrawerLayout.closeDrawers();

                // Add code here to update the UI based on the item selected
                // For example, swap UI fragments here

                return true;
                }
            });

        Menu nav_menu = navigationView.getMenu();

        MenuItem vehicles = nav_menu.findItem(R.id.nav_vehicles);
        vehicles.setVisible(false);

        navHeader = navigationView.getHeaderView(0);
        drawer_fullName = navHeader.findViewById(R.id.drawer_fullName);
        drawer_email = navHeader.findViewById(R.id.drawer_email);
        drawer_profilePic = navHeader.findViewById(R.id.drawer_profilePic);

        burgerMenu = findViewById(R.id.burger);
        burgerMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            mDrawerLayout.openDrawer(Gravity.START);
            // Code here executes on main thread after user presses button
            }
        });

        // If it exists, we'll get profile_pic, name etc.
        if ((ua.getUserId() != null) && (ua.getAccessToken() != null)) {
            // Update ua
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    client = new AuthenticatedApiClient(ua.getAccessToken());

                    userApi = client.createService(UserApi.class);

                    //Call<User> call = apiInstance.userFindById(String.valueOf(ua.getUserId().intValue()), "");

                    Call<Object> call = userApi.userPrototypeGetUser(ua.getUserId().toString());

                    call.enqueue(new Callback<Object>() {
                        @Override
                        public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                            if (client.handleError(response, AsyncDrawerActivity.this, AsyncDrawerActivity.class, ua)) {
                                return;
                            }

                            try {
                                Gson gson = new Gson();
                                JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                                JSONObject jObj = new JSONObject(jsonObject.toString());


                                profilePicUrl = new JSONObject(jObj.get("user").toString()).get("picture").toString();

                                firstName = new JSONObject(jObj.get("user").toString()).get("firstName").toString();
                                lastName = new JSONObject(jObj.get("user").toString()).get("lastName").toString();

                                fullNameText = firstName + " " + lastName;
                                email = new JSONObject(jObj.get("user").toString()).get("email").toString();

                                drawer_fullName.setText(fullNameText);
                                drawer_email.setText(email);

                                new DownloadImageTask(drawer_profilePic)
                                        .execute(profilePicUrl);
                                popCalls(mDrawerLayout, call);
                            } catch (Exception e) {
                                Log.d("SQLITE_ERROR", "Error dealing with the UserAccess object. :" + e.getMessage());
                                startActivityForResult(new Intent(AsyncDrawerActivity.this, ErrorActivity.class), 1);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(Call<Object> call, Throwable t) {
                            Log.d("SERVER_ERROR", "Error dealing with the response: " + t.getMessage());
                            startActivityForResult(new Intent(AsyncDrawerActivity.this, ErrorActivity.class), 1);
                            finish();
                        }
                    });

                    objCalls.add(call);
                }
            }, 2000);
            return;
        }
        startActivity(new Intent(AsyncDrawerActivity.this, LoginActivity.class));
        // close splash activity
        finish();
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

    private void popCalls(View v, Call<Object> call) {
        objCalls.remove(call);
        if (objCalls.size() == 0) {
            gl.callback(v,"done");
        }
    }

    public static ArrayList<View> getViewsByTag(ViewGroup root, String tag){
        ArrayList<View> views = new ArrayList<View>();
        final int childCount = root.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = root.getChildAt(i);
            if (child instanceof ViewGroup) {
                views.addAll(getViewsByTag((ViewGroup) child, tag));
            }

            final Object tagObj = child.getTag();
            if (tagObj != null && tagObj.equals(tag)) {
                views.add(child);
            }

        }
        return views;
    }
}
