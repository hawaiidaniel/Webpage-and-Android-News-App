package com.example.melodynewsapp.Adapter;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.melodynewsapp.Fragment.HeadlinesTabFragment;
import com.example.melodynewsapp.R;

public class HeadlinesTabAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_World, R.string.tab_Business, R.string.tab_Politics, R.string.tab_Sports, R.string.tab_Technology, R.string.tab_Science};
    private final Context mContext;

    public HeadlinesTabAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
            return HeadlinesTabFragment.newInstance(position + 1);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        return 6;
    }

}
