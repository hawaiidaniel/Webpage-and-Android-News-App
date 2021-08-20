package com.example.melodynewsapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.melodynewsapp.Adapter.HeadlinesTabAdapter;
import com.example.melodynewsapp.R;
import com.google.android.material.tabs.TabLayout;

public class HeadlinesFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_headlines, container, false);
        HeadlinesTabAdapter headlinesTabAdapter = new HeadlinesTabAdapter(this.getActivity(), getFragmentManager());
        ViewPager viewPager = v.findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(0);
        viewPager.setAdapter(headlinesTabAdapter);
        TabLayout tabs = v.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // do your work
            }
        });
        return v;
    }
}
