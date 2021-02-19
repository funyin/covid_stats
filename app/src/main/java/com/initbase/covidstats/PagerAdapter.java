package com.initbase.covidstats;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class PagerAdapter extends FragmentPagerAdapter {
    private  ArrayList<CountryActivity.TabFrag> fragments = new ArrayList<>();
    public PagerAdapter(@NonNull FragmentManager fm,ArrayList<CountryActivity.TabFrag> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragments.get(position).getFrag();
        fragment.setArguments(fragments.get(position).getArguments());
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }



}
