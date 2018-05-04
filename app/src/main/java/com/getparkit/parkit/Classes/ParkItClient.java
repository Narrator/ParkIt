package com.getparkit.parkit.Classes;

/**
 * Created by kauboy on 4/1/18.
 */
import com.loopj.android.http.*;

public class ParkItClient {
    private static final String BASE_URL = "https://parkit-api.herokuapp.com/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}