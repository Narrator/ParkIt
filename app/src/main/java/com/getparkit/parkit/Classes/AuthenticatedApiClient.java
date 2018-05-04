package com.getparkit.parkit.Classes;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.getparkit.parkit.Activities.AsyncDrawerActivity;
import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;
import com.getparkit.parkit.Async.DownloadImageTask;
import com.getparkit.parkit.SQLite.Helper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.IOException;

import io.swagger.client.ApiClient;
import io.swagger.client.api.UserApi;
import io.swagger.client.model.User;
import okhttp3.Interceptor;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by kauboy on 4/1/18.
 */

public class AuthenticatedApiClient extends ApiClient {

    public AuthenticatedApiClient(final String accessToken) {
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Request newRequest;

                newRequest = request.newBuilder()
                        .addHeader("Authorization", accessToken)
                        .build();
                return chain.proceed(newRequest);
            }
        };
        this.addAuthorization("oAuth", interceptor);
    }

    public boolean handleError(retrofit2.Response response, AppCompatActivity act, Class target, UserAccess ua) {
        int code = response.code();
        if (code == 200) {
            return false;
        }
        switch (code) {
            case 401:
                refreshToken(ua, act, target);
                return true;
            default: break;
        }
        Log.d("SERVER_ERROR","Error dealing with the response: " + code);
        act.startActivityForResult(new Intent(act, ErrorActivity.class),1);
        return true;
    }

    private void refreshToken(final UserAccess ua, final AppCompatActivity act, final Class target) {
        String refreshToken = ua.getRefreshToken();

        ApiClient apiClient = new ApiClient();
        UserApi userApi = apiClient.createService(UserApi.class);

        Call<Object> call = userApi.userRefreshToken("refresh_token", refreshToken);

        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, retrofit2.Response<Object> response) {
                if (response.code() != 200) {
                    Log.d("REFRESH_TOKEN_ERROR", "Error refreshing token");
                    act.startActivityForResult(new Intent(act, ErrorActivity.class), 1);
                    act.finish();
                    return;
                }
                try {
                    Gson gson = new Gson();
                    JsonObject jsonObject = gson.toJsonTree(response.body()).getAsJsonObject();
                    JSONObject jObj = new JSONObject(jsonObject.toString());

                    // Update in SQLite (update)
                    UserAccess ua = new UserAccess(jObj);
                    Helper helper = new Helper(act);

                    helper.updateUserAccess(ua);

                    // Pass new ua to intent
                    Intent i = new Intent(act, target);
                    i.putExtra("user-access", ua);
                    act.startActivity(i);
                    act.finish();
                } catch (Exception e) {
                    Log.d("REFRESH_TOKEN_ERROR", "Error refreshing token " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                Log.d("SERVER_ERROR", "Error dealing with the response: " + t.getMessage());
                act.startActivityForResult(new Intent(act, ErrorActivity.class), 1);
                act.finish();
            }
        });
    }

}