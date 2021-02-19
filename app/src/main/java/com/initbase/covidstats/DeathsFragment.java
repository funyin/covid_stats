package com.initbase.covidstats;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DeathsFragment extends Fragment {
    TextView mnew,total;
    public DeathsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_deaths, container, false);

        mnew = layout.findViewById(R.id.deathsNew);
        total = layout.findViewById(R.id.deathsTotal);

        Bundle bundle = getArguments();

        mnew.setText(bundle.getString(StatContract.deathsKeys.NEW));
        total.setText(bundle.getString(StatContract.deathsKeys.TOTAL));
        return layout;
    }
}
