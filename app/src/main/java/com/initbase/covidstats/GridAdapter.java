package com.initbase.covidstats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blongho.country_data.World;

import java.util.ArrayList;

public class GridAdapter extends ArrayAdapter<SingleStat> {
    Context context;
    public GridAdapter(@NonNull Context context, @NonNull ArrayList<SingleStat> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View layout = convertView;
        if (layout==null){
            layout = LayoutInflater.from(context).inflate(R.layout.grid_item,parent,false);
        }
        SingleStat singleStat = getItem(position);
        ImageView flag = layout.findViewById(R.id.flag);
        TextView country = layout.findViewById(R.id.countryName);
        TextView cases = layout.findViewById(R.id.totalCases);
        flag.setImageResource(singleStat.getFlagResId());
        country.setText(singleStat.getCountry());
        cases.setText((String)singleStat.getCases().get(StatContract.casesKeys.ACTIVE));
        return layout;
    }
}
