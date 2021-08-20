package com.example.melodynewsapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.melodynewsapp.Interfact.OnBookmarkListener;
import com.example.melodynewsapp.Model.Article;
import com.example.melodynewsapp.Model.BookmarkAction;
import com.example.melodynewsapp.R;
import com.google.gson.Gson;

public class ArticleDialog extends Dialog implements View.OnClickListener {
    private Article article;
    private ImageButton btnTwitter, btnBookmark;
    private ImageView imageView;
    private TextView textTitle;
    private Context mContext;
    private OnBookmarkListener mListener;
    private boolean dismissBookmark;
    public ArticleDialog(Context context, Article article,OnBookmarkListener mListener,boolean dismissBookmark) {
        super(context);
        this.article = article;
        this.mContext = context;
        this.mListener = mListener;
        this.dismissBookmark = dismissBookmark;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_article);
        btnTwitter = findViewById(R.id.dialogBtnT);
        btnBookmark = findViewById(R.id.favBtn);
        imageView = findViewById(R.id.dialogImage);
        textTitle = findViewById(R.id.dialogTitle);

        btnTwitter.setOnClickListener(this);
        btnBookmark.setOnClickListener(this);
        textTitle.setText(article.getTitle());
        Glide.with(mContext).load(article.getImage()).into(imageView);

        BookmarkAction bookmarkAction = new BookmarkAction(mContext,article.getIdentity());
        if(bookmarkAction.IsExist()){
            btnBookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
        }else{
            btnBookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogBtnT:
                TwitterHandler(article);
                break;
            case R.id.favBtn:
                BookmarkHandler(article);
                if(dismissBookmark){
                    dismiss();
                }
                break;
            default:
                break;
        }
    }

    private void TwitterHandler(Article article){
        String sharedlink = "https://theguardian.com/" + article.getIdentity();
        String url = "https://twitter.com/intent/tweet/?text=Check out this link:&url=" + sharedlink + "&hashtags=CSCI571NewsSearch";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        mContext.startActivity(i);
    }


    private void BookmarkHandler(Article article){
        Gson gson = new Gson();
        BookmarkAction bookmarkAction = new BookmarkAction(mContext,article.getIdentity());
        if(bookmarkAction.IsExist()){
            btnBookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
            bookmarkAction.RemoveExists();
            String showToast = article.getTitle() + " was removed from Favorites";
            Toast.makeText(mContext.getApplicationContext(), showToast, Toast.LENGTH_SHORT).show();
            mListener.CancelBookmark();
        }else{
            btnBookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);
            bookmarkAction.AddNew(gson.toJson(article));
            String showToast = article.getTitle() + " was added to Bookmarks";
            Toast.makeText(mContext.getApplicationContext(), showToast, Toast.LENGTH_SHORT).show();
            mListener.AddBookmark();
        }
    }
}
