package com.getparkit.parkit.Async;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.getparkit.parkit.Classes.UserAccess;
import com.getparkit.parkit.Activities.HelperActivities.ErrorActivity;

import com.getparkit.parkit.Activities.AsyncDrawerActivities.RoleSelectActivity;
import com.getparkit.parkit.SQLite.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExchangeToken extends AsyncTask <String, Void, JSONObject> {

    private Context mContext;

    public ExchangeToken (Context context) {
        mContext = context;
    }

    public JSONObject doInBackground(String ...params) {
        HttpURLConnection connection;
        OutputStreamWriter request = null;
        URL url = null;
        String response = null;
        try
        {
            url = new URL("https://parkit-api.herokuapp.com/auth/google/token");
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type","application/json");
            connection.setRequestMethod("POST");

            String str =  "{\"id_token\": \"" + params[0].toString()  +"\"}";
            byte[] outputInBytes = str.getBytes("UTF-8");
            OutputStream os = connection.getOutputStream();
            os.write( outputInBytes );
            os.close();

            request = new OutputStreamWriter(connection.getOutputStream());
            request.flush();
            request.close();

            String line = "";

            InputStreamReader isr = new InputStreamReader(connection.getInputStream());
            BufferedReader reader = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            // Response from server after login process will be stored in response variable.
            response = sb.toString();
            // You can perform UI operations here
            isr.close();
            reader.close();
        }
        catch(IOException e)
        {
            Log.d("SERVER_ERROR","Error exchanging token with server");
            Intent i = new Intent();
            i.setClass(mContext, ErrorActivity.class);
            mContext.startActivity(i);
        }
        System.out.println(response);
        JSONObject jObj;
        // Try to parse the string to a JSON Object
        try {
            jObj = new JSONObject(response);
        } catch (JSONException e) {
            Log.e("JSON_ERROR", "Error parsing data " + e.toString());
            Intent i = new Intent();
            i.setClass(mContext, ErrorActivity.class);
            mContext.startActivity(i);
            return null;
        }

        // Return the JSONObject
        return jObj;
    }

    protected void onPostExecute(JSONObject jObj) {
        try {

            // Store in SQLite (Insert)
            UserAccess ua = new UserAccess(jObj);

            Helper helper = new Helper(mContext);
            helper.deleteAll();

            helper.insertUserAccess(ua);

            // ASYNC->ACTIVITY CALL STARTING ACTIVITY FROM ASYNC
            Intent i = new Intent();
            i.putExtra("user-access", ua);
            i.setClass(mContext, RoleSelectActivity.class);
            mContext.startActivity(i);

            return;

        } catch (Exception e) {
            Log.d("ASYNC_SQLITE_ERROR","Error inserting user access in SQLite DB");
            Intent i = new Intent();
            i.setClass(mContext, ErrorActivity.class);
            mContext.startActivity(i);
            return;
        }
    }
}
