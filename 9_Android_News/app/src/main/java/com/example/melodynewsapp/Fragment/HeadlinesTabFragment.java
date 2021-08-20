package com.example.melodynewsapp.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.melodynewsapp.Adapter.ArticleAdapter;
import com.example.melodynewsapp.Model.Article;
import com.example.melodynewsapp.Model.QueueSingleton;
import com.example.melodynewsapp.Model.Tab;
import com.example.melodynewsapp.R;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HeadlinesTabFragment extends Fragment {

    private ProgressBar spinnerHeadline;
    TextView pgTextHeadline;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private List<Article> myModel = new ArrayList<>();
    private Tab tab;
    private Context mContext;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RequestQueue mQueue;
    public static HeadlinesTabFragment newInstance(int index) {
        HeadlinesTabFragment fragment = new HeadlinesTabFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabhost);
        TabHost tabHost=getTabHost();
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("已接电话").setContent(new Intent(this,BeCalledActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("呼出电话").setContent(new Intent(this,CalledActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("未接电话").setContent(new Intent(this,NoCallActivity.class)));

    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_headlines_tab, container, false);

        spinnerHeadline = v.findViewById(R.id.progressBarHeadline);
        pgTextHeadline = v.findViewById(R.id.progressBarTextHeadline);
        spinnerHeadline.setVisibility(View.VISIBLE);
        pgTextHeadline.setText("Fetching News");
        mContext = this.getActivity();
        mQueue = Volley.newRequestQueue(mContext);

        tab.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s=="business" || s=="sports" || s=="science"){
                    try {
                        Thread.sleep(1500);
                        Log.d("key",s);
                        GetNews(s);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Log.d("key",s);
                    GetNews(s);
                }
            }
        });
        recyclerView = v.findViewById(R.id.headlinesRecyclerView);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_headline);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                tab.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        Log.d("result",s);
                        GetNews(s);
                    }
                });
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

        return v;
    }

    public void setAdapter(List<Article> articles) {
        ArticleAdapter articleAdapter = new ArticleAdapter(mContext, articles);
        recyclerView.setAdapter(articleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    public void GetNews(String secName) {
        String url = "https://csci571hw9-276111.ue.r.appspot.com/headlines?secName="+secName;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("myResponse",response.toString());
                        if (response.length() > 0) {
                            Gson gson = new Gson();
                            myModel = Arrays.asList(gson.fromJson(response.toString(), Article[].class));
                        }
                        setAdapter(myModel);
                        spinnerHeadline.setVisibility(View.GONE);
                        pgTextHeadline.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        spinnerHeadline.setVisibility(View.GONE);
                        pgTextHeadline.setText("");
                        Toast.makeText(getActivity(), "Connection Error, try again later! " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        if(secName=="business" || secName=="sports" || secName=="science"){
            mQueue.add(jsonArrayRequest);
        }
        else{
            QueueSingleton.getInstance(mContext).addToRequestQueue(jsonArrayRequest);
        }
    }
}
