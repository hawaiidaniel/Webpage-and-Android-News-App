package com.example.melodynewsapp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.melodynewsapp.R;

public class DetailTitleBarLayout extends LinearLayout {
    public DetailTitleBarLayout(Context context, AttributeSet attrs) {
        super(context,attrs);
        LayoutInflater.from(context).inflate(R.layout.toolbar_detail, this);
        Button titleBack = findViewById(R.id.tb_detail_back);
        Button titleBookmark = (Button) findViewById(R.id.tb_detail_bookmark);
        titleBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
            }
        });
        titleBookmark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "You clicked Edit button", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
