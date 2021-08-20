package com.example.melodynewsapp.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.melodynewsapp.Adapter.ArticleAdapter;
import com.example.melodynewsapp.Model.Article;
import com.example.melodynewsapp.Model.QueueSingleton;
import com.example.melodynewsapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private ProgressBar spinner;
    ImageView iv1;
    TextView tv1, tv2, tv3, tv4, progressBarText;
    private RecyclerView recyclerView;
    private List<Article> myModel = new ArrayList<>();
    FusedLocationProviderClient fusedLocationProviderClient;
    private RecyclerView.LayoutManager layoutManager;
    private Context mContext;
    private SwipeRefreshLayout swipeRefreshLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        iv1 = v.findViewById(R.id.img);
        tv1 = v.findViewById(R.id.tv1);
        tv2 = v.findViewById(R.id.tv2);
        tv3 = v.findViewById(R.id.tv3);
        tv4 = v.findViewById(R.id.tv4);

        spinner = v.findViewById(R.id.progressBar);
        progressBarText = v.findViewById(R.id.progressBarText);
        spinner.setVisibility(View.VISIBLE);
        progressBarText.setText("Fetching News");

        mContext = this.getActivity();
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        GetNews();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.getActivity());

        if (ActivityCompat.checkSelfPermission(mContext,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //When permission granted
            getLocation();
        } else {
            //When permission denied
            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLocation();
                GetNews();
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

    private void setAdapter(List<Article> articles) {
        ArticleAdapter articleAdapter = new ArticleAdapter(mContext, articles);
        recyclerView.setAdapter(articleAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
    }

    private void GetNews() {
        String url = "https://csci571hw9-276111.ue.r.appspot.com/home";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("HomeResponse",response.toString());
                        if (response.length() > 0) {
                            Gson gson = new Gson();
                            myModel = Arrays.asList(gson.fromJson(response.toString(), Article[].class));
                        }
                        setAdapter(myModel);
                        spinner.setVisibility(View.GONE);
                        progressBarText.setText("");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("HomeError",error.toString());

                    }
                });
        QueueSingleton.getInstance(mContext).addToRequestQueue(jsonArrayRequest);
    }

    private void getLocation() {
        Log.d("tag", "here lar");
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this.getActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                Log.d("tag", String.valueOf(location));
                if(location != null){
                    try {
                        //Initialize geoCoder
                        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        String myCity = addresses.get(0).getLocality();
                        String myState = addresses.get(0).getAdminArea();
                        tv1.setText(myCity);
                        tv3.setText(myState);
                        getWeather(myCity);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void getWeather(String city){
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=89c89a53ca4b004774a3236049e09182";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try{
                    JSONObject obj1 = response.getJSONObject("main");
                    String temp = obj1.getString("temp");

                    JSONArray arr1 = response.getJSONArray("weather");
                    JSONObject obj2 = arr1.getJSONObject(0);
                    String summary = obj2.getString("main");

                    double value = Double.parseDouble(temp);
                    int val = (int) Math.round(value);

                    tv2.setText(Integer.toString(val) + " ℃");
                    tv4.setText(summary);

                    switch(summary){
                        case "Clouds":
                            iv1.setImageResource(R.drawable.cloudy_weather);
                            break;
                        case "Clear":
                            iv1.setImageResource(R.drawable.clear_weather);
                            break;
                        case "Snow":
                            iv1.setImageResource(R.drawable.snowy_weather);
                            break;
                        case "Rain":
                        case "Drizzle":
                            iv1.setImageResource(R.drawable.rainy_weather);
                            break;
                        case "Thunderstorm":
                            iv1.setImageResource(R.drawable.thunder_weather);
                            break;
                        default:
                            iv1.setImageResource(R.drawable.sunny_weather);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("myError",error.toString());

                    }
                });
        QueueSingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
    }
}
