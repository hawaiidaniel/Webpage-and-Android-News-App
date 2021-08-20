package com.example.melodynewsapp.Model;

import android.content.Context;
import android.content.SharedPreferences;

public class BookmarkAction {
    SharedPreferences pref;
    private Context context;
    private String identity;
    SharedPreferences.Editor editor;
    public BookmarkAction(Context ct, String key) {
        this.context = ct;
        identity = key;
        pref = context.getSharedPreferences("favList" , Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public boolean IsExist(){
        return pref.contains(identity);
    }

    public void AddNew(String value){
        editor.putString(identity,value);
        editor.commit();
    }

    public void RemoveExists(){
        if(IsExist()){
            editor.remove(identity);
            editor.commit();
        }
    }
}
