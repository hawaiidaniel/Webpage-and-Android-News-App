package com.example.melodynewsapp.Model;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

public class Tab extends ViewModel {

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            String result;
            switch (input){
                case 1:
                    return "world";
                case 2:
                    return "business";
                case 3:
                    return "politics";
                case 4:
                    return "sport";
                case 5:
                    return "technology";
                case 6:
                    return "science";
                default:
                    throw new IllegalStateException("Unexpected value: " + input);
            }
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
}