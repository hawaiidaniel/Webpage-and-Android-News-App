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
import com.example.melodynewsapp.Interfact.OnBookmarkListener;
import com.example.melodynewsapp.Model.Article;
import com.example.melodynewsapp.Model.BookmarkAction;
import com.example.melodynewsapp.R;
import com.google.gson.Gson;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder> {

    private List<Article> data;
    private Context context;
    public ArticleAdapter(Context ct, List articles) {
        this.context = ct;
        this.data = articles;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_article, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final Article item  = data.get(position);
        holder.myTitleView.setText(item.getTitle());
        Glide.with(context).load(item.getImage()).into(holder.myImageView);
        final Button favBtn = holder.itemView.findViewById(R.id.favBtn);
        BookmarkAction bookmarkAction = new BookmarkAction(context,item.getIdentity());
        if(bookmarkAction.IsExist()){
            favBtn.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
        }else{
            favBtn.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp);
        }

        ZonedDateTime ldt = ZonedDateTime.now();
        ZonedDateTime pub = ZonedDateTime.parse(data.get(position).getDate());
        ZonedDateTime publishedTime = pub.withZoneSameInstant(ldt.getZone());

        Duration duration = Duration.between(publishedTime, ldt);
        if (duration.toDays() != 0){
            holder.myTimeView.setText(duration.toDays() + "d ago");
        } else if (duration.toHours() != 0){
            holder.myTimeView.setText(duration.toHours() + "h ago");
        } else if (duration.toMinutes() != 0){
            holder.myTimeView.setText(duration.toMinutes() + "m ago");
        } else {
            if (duration.getSeconds() < 0){
                Long temp = duration.getSeconds();
                temp = -1*temp;
                holder.myTimeView.setText(temp + "s ago");
            }else{
                holder.myTimeView.setText(duration.getSeconds() + "s ago");
            }

        }

        holder.mySourceView.setText(item.getSection());

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Gson gson = new Gson();
                intent.putExtra("article", gson.toJson(data.get(position)));
                context.startActivity(intent);
            }
        });

        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                ArticleDialog cdd = new ArticleDialog(context, data.get(position), new OnBookmarkListener() {
                    @Override
                    public void AddBookmark() {
                        favBtn.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
                    }

                    @Override
                    public void CancelBookmark() {
                        favBtn.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp);
                    }
                },false);
                cdd.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView myTitleView, myTimeView, mySourceView;
        ImageView myImageView;
        ConstraintLayout mainLayout;
        Button favBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myTitleView = itemView.findViewById(R.id.titleView);
            myImageView = itemView.findViewById(R.id.imageView);
            myTimeView = itemView.findViewById(R.id.timeView);
            mySourceView = itemView.findViewById(R.id.sourceView);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            favBtn = itemView.findViewById(R.id.favBtn);

            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gson = new Gson();
                    int position = getAdapterPosition();
                    Log.d("position", String.valueOf(position));
                    Article item = data.get(position);
                    BookmarkAction bookmarkAction = new BookmarkAction(context,item.getIdentity());
                    if(bookmarkAction.IsExist()){
                        favBtn.setBackgroundResource(R.drawable.ic_bookmark_border_black_24dp);
                        bookmarkAction.RemoveExists();
                        String showToast = item.getTitle() + " was removed from Favorites";
                        Toast.makeText(context.getApplicationContext(), showToast, Toast.LENGTH_SHORT).show();
                    }else{
                        favBtn.setBackgroundResource(R.drawable.ic_bookmark_black_24dp);
                        bookmarkAction.AddNew(gson.toJson(item));
                        String showToast = item.getTitle() + " was added to Bookmarks";
                        Toast.makeText(context.getApplicationContext(), showToast, Toast.LENGTH_SHORT).show();
                    }
                    Log.d("item", gson.toJson(item));
                }
            });
        }
    }
}