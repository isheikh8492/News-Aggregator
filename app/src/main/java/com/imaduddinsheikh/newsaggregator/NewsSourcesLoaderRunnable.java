package com.imaduddinsheikh.newsaggregator;

import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class NewsSourcesLoaderRunnable implements Runnable {
    private static final String TAG = "NewsSourcesLoaderRunnable";

    private final MainActivity mainActivity;

    private static RequestQueue queue;

    private static final String API_URL = "https://newsapi.org/v2/sources";
    private static final String APIKey = "647ca397f32c4df8a591a2e8320429e5";

    public NewsSourcesLoaderRunnable(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(API_URL).buildUpon();
        buildURL.appendQueryParameter("apiKey", APIKey);
        String urlToUse = buildURL.toString();

        Response.Listener<JSONObject> listener =
                response -> parseJSON(response.toString());

        Response.ErrorListener error =
                error1 -> handleResults(null);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest =
                new JsonObjectRequest(Request.Method.GET, urlToUse, null, listener, error) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", "News-App");
                return headers;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void handleResults(String s) {
        if (s == null) {
            Log.d(TAG, "handleResults: Failure in data download");
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        final String newsSourcesList = parseJSON(s);
        if (newsSourcesList == null) {
            mainActivity.runOnUiThread(mainActivity::downloadFailed);
            return;
        }

        mainActivity.runOnUiThread(
                mainActivity::updateData);
    }

    private String parseJSON(String s) {
        Log.d(TAG, "parseJSON: " + s.toString());
        return null;
    }
}
