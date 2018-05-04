package com.getparkit.parkit.Activities.HelperActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.getparkit.parkit.Activities.BaseInjectionActivity;
import com.getparkit.parkit.Classes.UserAccess;
import com.getparkit.parkit.R;

public class LoadingActivity extends BaseInjectionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        loadingActivity = this;
    }

    @Override
    public void callback(View view, String result) {

    }
}
