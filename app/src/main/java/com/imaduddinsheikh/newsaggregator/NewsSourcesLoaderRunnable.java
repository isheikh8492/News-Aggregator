package com.imaduddinsheikh.newsaggregator;

import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import androidx.core.graphics.ColorUtils;

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
import java.util.Objects;

public class NewsSourcesLoaderRunnable implements Runnable {
    private static final String TAG = "NewsSourcesLoaderRunnable";

    private final MainActivity mainActivity;

    private final ArrayList<NewsSource> newsSourceList =
            new ArrayList<>();

    private final HashMap<String, ArrayList<String>> categoryToSourceName = new HashMap<>();

    private static RequestQueue queue;

    private static final String API_URL = "https://newsapi.org/v2/sources";
    private static final String APIKey = "647ca397f32c4df8a591a2e8320429e5";
    private static final HashMap<String, Integer> categoryColor = new HashMap<>();

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
                mainActivity.updateNewsSourceData(newsSourceList, categoryToSourceName, categoryColor));
    }

    private String parseJSON(String s) {

        try {
            JSONObject jObjMain = new JSONObject(s);

            // "sources" section
            JSONArray sources = jObjMain.getJSONArray("sources");
            for (int i = 0; i < sources.length(); i++) {
                JSONObject source = sources.getJSONObject(i);

                String jId = source.getString("id");
                String jName = source.getString("name");
                String jCategory = source.getString("category");
                newsSourceList.add(new NewsSource(jId, jName, jCategory));
                if (!categoryToSourceName.containsKey(jCategory))
                    categoryToSourceName.put(jCategory, new ArrayList<>());
                Objects.requireNonNull(categoryToSourceName.get(jCategory)).add(jName);

                if (!categoryColor.containsKey(jCategory))
                    categoryColor.put(jCategory, getCategoryColor(jCategory));
            }
            mainActivity.updateNewsSourceData(newsSourceList, categoryToSourceName, categoryColor);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private int getCategoryColor(String category) {
        float[] hsl = new float[3];
        int hash = category.hashCode();
        float hue = Math.abs(hash % 360);

        if ((hue >= 240 && hue <= 260) || (hue >= 300 && hue <= 320)) {
            hue = (hue + 60) % 360;
        }

        float saturation = 0.94f; // Saturation between 0 and 1
        float lightness = 0.64f; // Lightness between 0 and 1

        hsl[0] = hue;
        hsl[1] = saturation;
        hsl[2] = lightness;
        int color = ColorUtils.HSLToColor(hsl);

        return color;
    }

}
