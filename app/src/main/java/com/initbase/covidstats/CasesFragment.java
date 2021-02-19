package com.initbase.covidstats;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CasesFragment extends Fragment {
    TextView mnew,active,critical,recovered,total;
    public CasesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_cases, container, false);
        mnew = layout.findViewById(R.id.casesNew);
        active = layout.findViewById(R.id.casesActive);
        critical = layout.findViewById(R.id.casesCritical);
        recovered = layout.findViewById(R.id.casesRecovered);
        total = layout.findViewById(R.id.casesTotal);

        Bundle bundle = this.getArguments();

        mnew.setText(bundle.getString(StatContract.casesKeys.NEW));
        active.setText(bundle.getString(StatContract.casesKeys.ACTIVE));
        critical.setText(bundle.getString(StatContract.casesKeys.CRITICAL));
        recovered.setText(bundle.getString(StatContract.casesKeys.RECOVERED));
        total.setText(bundle.getString(StatContract.casesKeys.TOTAL));
        return layout;
    }
}
