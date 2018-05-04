package com.getparkit.parkit.Activities.HelperActivities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.getparkit.parkit.Activities.BaseInjectionActivity;
import com.getparkit.parkit.R;

public class ErrorActivity extends BaseInjectionActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        // First destroy loading activity
        if (loadingActivity != null && !loadingActivity.isDestroyed()) {
            loadingActivity.finish();
        }
    }

    public void onTryAgain(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("REFRESH",1);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
    @Override
    public void callback(View view, String result) {

    }
}
