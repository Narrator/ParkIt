package com.getparkit.parkit.Activities.AsyncDrawerActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.getparkit.parkit.Activities.AsyncDrawerActivity;
import com.getparkit.parkit.Async.DownloadImageTask;
import com.getparkit.parkit.R;

public class AddParkingSpaceActivity extends AsyncDrawerActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout wrapper = findViewById(R.id.asyncDrawerWrapper);
        View inflated = getLayoutInflater().inflate(R.layout.activity_add_parking_space, wrapper, false);

        wrapper.addView(inflated);

    }

    // method is invoked when all async tasks for drawer are done
    // Implement all logic dependent on drawer tasks and variables here..
    @Override
    public void callback(View view, String result) {
    }


}
