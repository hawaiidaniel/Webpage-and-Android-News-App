package com.example.melodynewsapp.Activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.melodynewsapp.Model.QueueSingleton;
import com.example.melodynewsapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private androidx.appcompat.widget.SearchView.SearchAutoComplete searchAutoComplete;
    private SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_headlines, R.id.navigation_trending,R.id.navigation_bookmarks)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.actionSearch).getActionView();
        searchAutoComplete = searchView.findViewById(R.id.search_src_text);
        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);
        searchView.setSuggestionsAdapter(new SimpleCursorAdapter(
                this, android.R.layout.simple_list_item_1, null,
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
                new int[] { android.R.id.text1 }));

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {

                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String term = cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
                cursor.close();
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {

                return onSuggestionSelect(position);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2){
                    Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                    intent.putExtra("article_title", query);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Type more than two letters!", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2){
                    GetSuggestions(newText);
                }
                else {
                    searchView.getSuggestionsAdapter().changeCursor(null);
                }

                return true;
            }
        });
        return true;
    }

    private void GetSuggestions(String query) {
        String url = "https://api.cognitive.microsoft.com/bing/v5.0/suggestions?q="+query;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("HomeResponse",response.toString());
                        if (response.length() > 0) {
                            MatrixCursor cursor = new MatrixCursor(new String[] {
                                    BaseColumns._ID,
                                    SearchManager.SUGGEST_COLUMN_TEXT_1
                            });
                            try {
                                JSONArray arr = response.getJSONArray("suggestionGroups");
                                JSONArray sgs = arr.getJSONObject(0).getJSONArray("searchSuggestions");
                                int lg = sgs.length();
                                if(lg>5){
                                    lg=5;
                                }
                                for(int i = 0; i < lg; i++){
                                    JSONObject obj = sgs.getJSONObject(i);
                                    String item = obj.getString("displayText");
                                    Object[] row = new Object[] { i,item };
                                    cursor.addRow(row);
                                }
                                searchView.getSuggestionsAdapter().changeCursor(cursor);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("HomeError",error.toString());

                    }
                }){
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Ocp-Apim-Subscription-Key", "6c0b41fa57024e7ba09bbc6d3b8208ed");
                return headers;
            }
        };

        QueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}
