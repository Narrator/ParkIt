package com.getparkit.parkit.Activities.HelperActivities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.getparkit.parkit.R;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);
    }

    public void onTryAgain(View v) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("REFRESH",1);
        setResult(RESULT_OK, returnIntent);
        finish();
    }
}
