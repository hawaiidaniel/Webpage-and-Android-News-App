package com.example.melodynewsapp.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.melodynewsapp.Adapter.ArticleAdapter;
import com.example.melodynewsapp.Model.Article;
import com.example.melodynewsapp.Model.QueueSingleton;
import com.example.melodynewsapp.R;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ProgressBar spinnerSearch;
    private RecyclerView recyclerView;
    private List<Article> myModel = new ArrayList<>();
    private Context mContext;
    private Button tbBackBtn;
    private TextView titleView, progressBarTextSearch;
    private SwipeRefreshLayout swipeRefreshLayout;
    String query;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        query = getIntent().getExtras().getString("article_title");
        setContentView(R.layout.activity_search);

        mContext = this;

        spinnerSearch = findViewById(R.id.progressBarSearch);
        progressBarTextSearch = findViewById(R.id.progressBarTextSearch);
        spinnerSearch.setVisibility(View.VISIBLE);
        progressBarTextSearch.setText("Fetching News");

        recyclerView = findViewById(R.id.searchRecycler);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        titleView = findViewById(R.id.tb_search_text);
        tbBackBtn = findViewById(R.id.tb_search_back);
        String title = "Search Results for " + query;
        titleView.setText(title);
        QueryNews(query);

        tbBackBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SearchActivity.this.finish();
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_search);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                QueryNews(query);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);
            }
        });

    }
    private void setAdapter(List<Article> articles) {
        ArticleAdapter articleAdapter = new ArticleAdapter(mContext, articles);
        recyclerView.setAdapter(articleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void QueryNews(String query) {
        String url = "https://csci571hw9-276111.ue.r.appspot.com/searchEngine?qKey="+query;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("SearchResponse",response.toString());
                        if (response.length() > 0) {
                            Gson gson = new Gson();
                            myModel = Arrays.asList(gson.fromJson(response.toString(), Article[].class));
                        }
                        setAdapter(myModel);
                        spinnerSearch.setVisibility(View.GONE);
                        progressBarTextSearch.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("SearchError",error.toString());
                    }
                });
        QueueSingleton.getInstance(mContext).addToRequestQueue(jsonArrayRequest);
    }
}

