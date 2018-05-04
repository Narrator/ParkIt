package com.getparkit.parkit.Activities.HelperActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.getparkit.parkit.Classes.UserAccess;
import com.getparkit.parkit.R;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        String activity = this.getIntent().getStringExtra("activity");
        UserAccess ua = this.getIntent().getParcelableExtra("user-access");
    }
}
