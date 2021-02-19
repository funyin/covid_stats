package com.initbase.covidstats;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class TestsFragment extends Fragment {
    TextView total;
    public TestsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_tests, container, false);

        total = layout.findViewById(R.id.testsTotal);

        Bundle bundle = getArguments();
        total.setText(bundle.getString(StatContract.testsKeys.TOTAL));
        return layout;
    }
}
