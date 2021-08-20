package com.example.melodynewsapp.Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.melodynewsapp.Model.QueueSingleton;
import com.example.melodynewsapp.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class TrendingFragment extends Fragment {
    ArrayList<Entry> myArray = new ArrayList<>();
    String word;
    LineChart lineChart;
    EditText textInput;
    private Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_trending, container, false);
        mContext = this.getActivity();
        lineChart = v.findViewById(R.id.lineChart);
        textInput = (EditText) v.findViewById(R.id.textInput);

        jsonParse("Coronavirus");

        //get user input
        textInput.setOnKeyListener(new View.OnKeyListener(){

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    word = textInput.getText().toString();
                    jsonParse(word);
                }
                return false;
            }
        });
        return v;
    }

    private void jsonParse(final String word) {
        String url = "https://csci571hw9-276111.ue.r.appspot.com/trends?word=" + word;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try
                        {
                            myArray.clear();
                            for (int i = 0; i < response.length(); i++)
                            {
                                myArray.add(new Entry(i, Integer.parseInt(String.valueOf(response.get(i)))));
                            }
                            LineDataSet lineDataSet = new LineDataSet(myArray, "Trending Chart for " + word);
                            lineDataSet.setColor(Color.BLUE);

                            ArrayList<ILineDataSet> iLineDataSets = new ArrayList<>();
                            iLineDataSets.add(lineDataSet);

                            LineData lineData = new LineData(iLineDataSets);
                            lineChart.clear();
                            lineChart.setData(lineData);
                            lineChart.invalidate();

                            lineChart.setNoDataText("Data not Available ar");

                            Legend legend = lineChart.getLegend();
                            legend.setEnabled(true);
                            legend.setTextSize(13);

                            lineDataSet.setColor(Color.BLUE);
                            lineDataSet.setCircleColor(Color.BLUE);

                            lineDataSet.setValueTextSize(10);
                            lineDataSet.setDrawCircles(true);
                            lineDataSet.setCircleHoleRadius(10);
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
        //add request to queue
        QueueSingleton.getInstance(mContext).addToRequestQueue(jsonArrayRequest);
    }
}
