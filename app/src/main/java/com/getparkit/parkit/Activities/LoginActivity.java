package com.getparkit.parkit.Activities;

import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.Activities.HelperActivities.LoadingActivity;
import com.getparkit.parkit.Async.*;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.getparkit.parkit.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends BaseInjectionActivity {

    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signInButton;
    private Context mContext = this;

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        // Find the TextView that is inside of the SignInButton and set its text
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);

            if (v instanceof TextView) {
                TextView tv = (TextView) v;
                tv.setText(buttonText);
                return;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("351987647675-ir6tjeqqfba89l2q1e3ab8c1cfnp50cj.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        setGooglePlusButtonText(signInButton, "Sign in with Google");

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 1);
            }
        });

        // GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();

            Log.d("ID TOKEN:", idToken);

            Intent i = new Intent(LoginActivity.this, LoadingActivity.class);
            startActivity(i);
            new ExchangeToken(this).execute(idToken);
            finish();
            // Also end this activity (LoginActivity)
        } catch (ApiException e) {
            Log.d("SIGNIN_ERROR","Error signing in: " + e.getMessage());
            startActivity(new Intent(LoginActivity.this, ErrorActivity.class));
            // updateUI(null);
        }
    }
}
