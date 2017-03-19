package com.marek.codepath.nytimessearch.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by marek on 3/18/2017.
 */

public class Article implements Serializable {
    String url;
    String headline;
    String thumbnail;

    public String getUrl() {
        return url;
    }

    public String getHeadline() {
        return headline;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public Article(JSONObject jArticle) {
        try {
            url = jArticle.getString("web_url");
            headline = jArticle.getJSONObject("headline").getString("main");
            JSONArray multimedia = jArticle.getJSONArray("multimedia");
            if(multimedia != null && multimedia.length() > 0) {
                JSONObject mmj = multimedia.getJSONObject(0);
                thumbnail = "http://www.nytimes.com/" + mmj.get("url");
            } else {
                thumbnail = "";
            }
        } catch(JSONException je) {
            Log.e("ERROR", "Error parsing article from JSON", je);
        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray jArticles) {
        ArrayList<Article> results = new ArrayList<>();

        for(int x = 0; x < jArticles.length(); x++) {
            try {
                results.add(new Article(jArticles.getJSONObject(x)));
            } catch(JSONException je) {
                Log.e("ERROR", "Error parsing article list; item " + x, je);
            }
        }

        return results;
    }
}
