package com.example.melodynewsapp.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.example.melodynewsapp.Model.Article;
import com.example.melodynewsapp.Model.BookmarkAction;
import com.example.melodynewsapp.Model.Detail;
import com.example.melodynewsapp.Model.QueueSingleton;
import com.example.melodynewsapp.R;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DetailActivity extends AppCompatActivity {

    private ProgressBar spinnerDetail;
    TextView titleView,sectionView, dateView, descriptionView, tbTitle, progressBarTextDetail;
    ImageView imageView;
    Button viewButton, tbBackBtn, tbTwitterBtn, tbBookmarkBtn;
    private Detail dModel;
    private Article aModel;
    private Gson gson = new Gson();
    private BookmarkAction bookmarkAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        tbTitle = findViewById(R.id.tb_detail_text);
        tbBackBtn = findViewById(R.id.tb_detail_back);
        tbTwitterBtn = findViewById(R.id.tb_detail_twitter);
        tbBookmarkBtn = findViewById(R.id.tb_detail_bookmark);

        spinnerDetail = findViewById(R.id.progressBarDetail);
        progressBarTextDetail = findViewById(R.id.progressBarTextDetail);
        spinnerDetail.setVisibility(View.VISIBLE);
        progressBarTextDetail.setText("Fetching News");

        titleView = findViewById(R.id.detailTitle);
        imageView = findViewById(R.id.detailImage);
        sectionView = findViewById(R.id.detailSection);
        dateView = findViewById(R.id.detailDate);
        descriptionView =findViewById(R.id.detailDescription);
        viewButton = findViewById(R.id.detailButton);

        String article = getIntent().getExtras().getString("article");
        aModel = gson.fromJson(article, Article.class);
        jsonParse(aModel.getIdentity());

        tbTitle.setText(aModel.getTitle());

        bookmarkAction = new BookmarkAction(this,aModel.getIdentity());
        if(bookmarkAction.IsExist()){
            tbBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
        }
        else{
            tbBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp);
        }

        tbBookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();

                if(bookmarkAction.IsExist()){
                    tbBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp);
                    bookmarkAction.RemoveExists();
                    String showToast = aModel.getTitle() + " was removed from Favorites";
                    Toast.makeText(DetailActivity.this.getApplicationContext(), showToast, Toast.LENGTH_SHORT).show();
                }else{
                    tbBookmarkBtn.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
                    bookmarkAction.AddNew(gson.toJson(aModel));
                    String showToast = aModel.getTitle() + " was added to Bookmarks";
                    Toast.makeText(DetailActivity.this.getApplicationContext(), showToast, Toast.LENGTH_SHORT).show();
                }
            }
        });
        tbBackBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DetailActivity.this.finish();
            }
        });

        tbTwitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sharedlink = "https://theguardian.com/" + aModel.getIdentity();
                String url = "https://twitter.com/intent/tweet/?text=Check out this link:&url=" + sharedlink + "&hashtags=CSCI571NewsSearch";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                DetailActivity.this.startActivity(i);
            }
        });

    }

    private void jsonParse(String identity) {
        String url = "https://csci571hw9-276111.ue.r.appspot.com/detailEngine?identity="+identity;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("DetailResponse",response.toString());
                        try{
                            JSONObject jsonObj = response.getJSONObject(0);
                            Gson gson = new Gson();
                            dModel = gson.fromJson(jsonObj.toString(), Detail.class);
                            Glide.with(DetailActivity.this).load(dModel.getImage()).into(imageView);
                            titleView.setText(dModel.getTitle());
                            sectionView.setText(dModel.getSection());

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLL yyyy");
                            ZonedDateTime ldt = ZonedDateTime.now();
                            ZonedDateTime pub = ZonedDateTime.parse(dModel.getDate());
                            ZonedDateTime publishedTime = pub.withZoneSameInstant(ldt.getZone());
                            String formattedString = publishedTime.format(formatter);


                            dateView.setText(formattedString);
                            descriptionView.setText(Html.fromHtml(dModel.getDescription()));
                            viewButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String url = dModel.getSharedlink();
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });

                            spinnerDetail.setVisibility(View.GONE);
                            progressBarTextDetail.setText("");
                        }catch(JSONException e){
                            Log.d("DetailError",e.toString());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("DetailError",error.toString());
                    }
                });
        //add request to queue
        QueueSingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }
}
