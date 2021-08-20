package com.example.melodynewsapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.melodynewsapp.Activity.DetailActivity;
import com.example.melodynewsapp.Interfact.OnBookmarkEmptyListener;
import com.example.melodynewsapp.Interfact.OnBookmarkListener;
import com.example.melodynewsapp.Model.Article;
import com.example.melodynewsapp.Model.BookmarkAction;
import com.example.melodynewsapp.R;
import com.google.gson.Gson;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BookmarksAdapter extends RecyclerView.Adapter<BookmarksAdapter.BookmarksViewHolder> {

    private List<Article> data;
    private Context context;
    private OnBookmarkEmptyListener mListener;
    private Button favBtn;

    public BookmarksAdapter(Context ct, List articles, OnBookmarkEmptyListener mListener) {
        this.context = ct;
        this.data = articles;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public BookmarksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //pref = context.getSharedPreferences("favList" , Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.row_bookmark, parent, false);
        final BookmarksAdapter.BookmarksViewHolder viewHolder = new BookmarksAdapter.BookmarksViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull BookmarksViewHolder holder, int p) {
        final int position = p;
        final Article item  = data.get(position);
        holder.myTitleView.setText(item.getTitle());
        Glide.with(context).load(item.getImage()).into(holder.myImageView);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLL");
        ZonedDateTime ldt = ZonedDateTime.now();
        ZonedDateTime pub = ZonedDateTime.parse(item.getDate());
        ZonedDateTime publishedTime = pub.withZoneSameInstant(ldt.getZone());
        String formattedString = publishedTime.format(formatter);

        holder.myDateView.setText(formattedString);
        holder.mySourceView.setText(item.getSection());
        favBtn = holder.itemView.findViewById(R.id.bmFavBtn);
        final BookmarkAction bookmarkAction = new BookmarkAction(context,item.getIdentity());
        if(bookmarkAction.IsExist()){
            favBtn.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
        }else{
            favBtn.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp);
        }
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Gson gson = new Gson();
                intent.putExtra("article", gson.toJson(item));
                context.startActivity(intent);
            }
        });

        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                final ArticleDialog cdd = new ArticleDialog(context, item, new OnBookmarkListener() {
                    @Override
                    public void AddBookmark() {
                    }

                    @Override
                    public void CancelBookmark() {
                        removeItem(position);
                        bookmarkAction.RemoveExists();
                        String showToast = item.getTitle() + " was removed from Favorites";
                        Toast.makeText(context.getApplicationContext(), showToast, Toast.LENGTH_SHORT).show();
                    }
                },true);
                cdd.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class BookmarksViewHolder extends RecyclerView.ViewHolder{
        TextView myTitleView, myDateView, mySourceView;
        ImageView myImageView;
        ConstraintLayout mainLayout;
        Button favBtn;
        public BookmarksViewHolder(@NonNull View itemView) {
            super(itemView);
            myTitleView = itemView.findViewById(R.id.bmText);
            myImageView = itemView.findViewById(R.id.bmImg);
            myDateView = itemView.findViewById(R.id.bmDate);
            mySourceView = itemView.findViewById(R.id.bmSource);
            mainLayout = itemView.findViewById(R.id.bookmarkLayout);
            favBtn = itemView.findViewById(R.id.bmFavBtn);

            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gson = new Gson();
                    int position = getAdapterPosition();
                    Log.d("position", String.valueOf(position));
                    Article item = data.get(position);
                    BookmarkAction bookmarkAction = new BookmarkAction(context,item.getIdentity());
                    if(bookmarkAction.IsExist()){
                        removeItem(position);
                        bookmarkAction.RemoveExists();
                        String showToast = item.getTitle() + " was removed from Favorites";
                        Toast.makeText(context.getApplicationContext(), showToast, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    private void removeItem(int position) {
        data.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position,data.size());
        if(data.isEmpty()){
            mListener.Empty();
        }
    }
}
