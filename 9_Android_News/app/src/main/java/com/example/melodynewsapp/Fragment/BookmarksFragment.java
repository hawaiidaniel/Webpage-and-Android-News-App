package com.example.melodynewsapp.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.melodynewsapp.Adapter.BookmarksAdapter;
import com.example.melodynewsapp.Interfact.OnBookmarkEmptyListener;
import com.example.melodynewsapp.Model.Article;
import com.example.melodynewsapp.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookmarksFragment extends Fragment {
    private Context mContext;
    private RecyclerView recyclerView;
    private TextView emptyView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> myModel = new ArrayList<>();
    SharedPreferences pref;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        mContext = this.getActivity();
        pref = mContext.getSharedPreferences("favList" , Context.MODE_PRIVATE);
        recyclerView = v.findViewById(R.id.bmRecycleView);
        emptyView = v.findViewById(R.id.empty_view);
        GetNews();
        setAdapter(myModel);
        layoutManager = new GridLayoutManager(mContext,2);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        if(myModel.isEmpty()){
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else{
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
        return v;
    }

    public void setAdapter(List<Article> articles) {
        BookmarksAdapter adapter = new BookmarksAdapter(mContext, articles, new OnBookmarkEmptyListener() {
            @Override
            public void Empty() {
                recyclerView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
    }

    public void GetNews() {
        Map<String, ?> prefsMap = pref.getAll();
        for (Map.Entry<String, ?> entry: prefsMap.entrySet()) {
            Gson gson = new Gson();
            Article model = gson.fromJson(entry.getValue().toString(), Article.class);
            myModel.add(model);
        }
    }
}
