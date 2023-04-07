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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewsArticlesLoaderRunnable implements Runnable{
    private static final String TAG = "NewsArticlesLoaderRunnable";

    private final MainActivity mainActivity;

    private final String sourceId;

    private final String sourceName;

    private final ArrayList<NewsArticle> newsArticleList =
            new ArrayList<>();

    private static RequestQueue queue;

    private static final String API_URL = "https://newsapi.org/v2/top-headlines";
    private static final String APIKey = "647ca397f32c4df8a591a2e8320429e5";

    public NewsArticlesLoaderRunnable(MainActivity mainActivity, String sourceId, String sourceName) {
        this.mainActivity = mainActivity;
        this.sourceId = sourceId;
        this.sourceName = sourceName;
    }

    @Override
    public void run() {
        queue = Volley.newRequestQueue(mainActivity);

        Uri.Builder buildURL = Uri.parse(API_URL).buildUpon();
        buildURL.appendQueryParameter("sources", sourceId);
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
                mainActivity.updateData(newsArticleList, sourceId, sourceName));
    }

    private String parseJSON(String s) {
        Log.d(TAG, "parseJSON: " + s);

        try {
            JSONObject jObjMain = new JSONObject(s);

            // "articles" section
            JSONArray articles = jObjMain.getJSONArray("articles");

            for (int i = 0; i < articles.length(); i++) {
                JSONObject article = articles.getJSONObject(i);

                String jAuthor = article.getString("author");
                String jTitle = article.getString("title");
                String jDescription = article.getString("description");
                String jUrl = article.getString("url");
                String jImageUrl = article.getString("urlToImage");
                String jPublishDate = article.getString("publishedAt");
                String[] jSource = new String[]{
                        article.getJSONObject("source").getString("id"),
                        article.getJSONObject("source").getString("name")
                };


                 newsArticleList.add(new NewsArticle(jAuthor, jTitle, jDescription, jUrl, jImageUrl, jPublishDate, jSource));
            }
            mainActivity.updateData(newsArticleList, this.sourceId, this.sourceName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
