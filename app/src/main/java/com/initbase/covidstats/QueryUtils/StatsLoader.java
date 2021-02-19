package com.initbase.covidstats.QueryUtils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.initbase.covidstats.SingleStat;

import java.io.IOException;
import java.util.ArrayList;

public class StatsLoader extends AsyncTaskLoader<ArrayList<SingleStat>> {
    String url;
    static Context context;

    public StatsLoader(@NonNull Context context, String query_url) {
        super(context);
        this.context = context;
        url = query_url;
    }

    @Nullable
    @Override
    public ArrayList<SingleStat> loadInBackground() {
        ArrayList<SingleStat> stats = new ArrayList<>();
        try {
            stats = QueryUtil.parseJson(QueryUtil.makeConnection(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stats;
    }
}