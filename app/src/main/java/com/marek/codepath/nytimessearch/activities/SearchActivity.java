package com.marek.codepath.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.marek.codepath.nytimessearch.adapter.ArticleArrayAdapter;
import com.marek.codepath.nytimessearch.misc.EndlessScrollListener;
import com.marek.codepath.nytimessearch.model.Article;
import com.marek.codepath.nytimessearch.R;
import com.marek.codepath.nytimessearch.activities.ArticleActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    String query = null; // query entered by user
    Integer hits = null;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etQuery = (EditText)findViewById(R.id.etQuery);
        gvResults = (GridView)findViewById(R.id.gvResults);
        btnSearch = (Button)findViewById(R.id.btnSearch);

        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                Article article = articles.get(position);
                i.putExtra("article", article);
                startActivity(i);
            }
        });

        gvResults.setOnScrollListener(new EndlessScrollListener(10 /* page size for NYT API */) {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                Log.d("DEBUG", "Scroll listener onLoadMore");
                return loadPage(page);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    public void onArticleSearch(View view) {
        query = etQuery.getText().toString();
        Toast.makeText(this, "Search for " + query, Toast.LENGTH_LONG).show();
        loadPage(1);
    }

    boolean loadPage(final int page) {
        Log.i("INFO", "loading page " + page + "; query=" + query);

        if(query == null) {
            return false;
        }

        if((hits != null && hits.intValue() <= 10 * (page-1)) || page > 120) {
            Log.d("DEBUG", "Ignoring request since it's for a page that cannot be retrieved.  hits=" + hits);
            return false;
        }

        try {
            AsyncHttpClient client = new AsyncHttpClient();
            String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
            RequestParams parms = new RequestParams();
            parms.put("api-key", "23bf04f4282a48f3b9c3bf362a934101");
            parms.put("page", page - 1);
            parms.put("q", query);

            Log.d("DEBUG", "Executing GET");
            client.get(url, parms, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("DEBUG", "Response for page " + page + ": " + response.toString());
                    JSONArray jsonArticles = null;

                    try {
                        hits = response.getJSONObject("response").getJSONObject("meta").getInt("hits");
                        Log.d("DEBUG", "Search hits " + hits);

                        jsonArticles = response.getJSONObject("response").getJSONArray("docs");
                        adapter.addAll(Article.fromJSONArray(jsonArticles));
                        Log.d("DEBUG", "Now have " + articles.size() + " articles");
                    } catch (JSONException je) {
                        Log.e("ERROR", "Error parsing article search results", je);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.e("ERROR", responseString);
                }
            });
        } catch(Exception e) {
            Log.e("ERROR", "Error querying backend", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
