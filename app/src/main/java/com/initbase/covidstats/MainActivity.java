package com.initbase.covidstats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.SharedPreferencesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.initbase.covidstats.QueryUtils.QueryUtil;
import com.initbase.covidstats.QueryUtils.StatsLoader;

import java.util.ArrayList;
import java.util.HashMap;
import com.initbase.covidstats.StatContract.*;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<SingleStat>> {

    private GridAdapter gridAdapter;
    private ArrayList<SingleStat> stats;
    private GridView gridView;
    TextView countryCount,updatetime;
    Button viewMore;
    Spinner selectCountry;
    private View myCountryCon, loadIndicator, conInfo;
    private boolean countrySelected;
    private ArrayList<String> countryNames;
    private SharedPreferences.Editor spEditor;
    private SharedPreferences sharedPreferences;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolBar = findViewById(R.id.toolBar);
        setSupportActionBar(toolBar);

        gridView = findViewById(R.id.countryGrid);
        countryCount = findViewById(R.id.countryCount);
        loadIndicator =  findViewById(R.id.loadIndicator);
        conInfo = findViewById(R.id.connectionInfo);
        myCountryCon = findViewById(R.id.myCountryCon);
        selectCountry = findViewById(R.id.selectCountry);
        viewMore = findViewById(R.id.vieMore);
        updatetime = findViewById(R.id.updated);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SingleStat stat = gridAdapter.getItem(position);
                PassData(stat);
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        spEditor = sharedPreferences.edit();
        countrySelected = sharedPreferences.getBoolean(sharedPrefs.MY_COUNTRY_SELECTED,false);

        stats = new ArrayList<>();
        refresh(stats);

        loadData();
    }
    public void refresh(final ArrayList<SingleStat> data){
        loadIndicator.setVisibility(View.GONE);

        gridAdapter = new GridAdapter(MainActivity.this,data);
        gridView.setAdapter(gridAdapter);
        countryCount.setText(String.valueOf(QueryUtil.getTotalInfectedCountries()));
        if (data.size()!=0){
            updatetime.setText(gridAdapter.getItem(0).getTime());
        }
        countryNames = new ArrayList<>();
        if (countrySelected){
            countryNames.add(getString(R.string.none));
        }else{
            countryNames.add(getString(R.string.selectMyCountry));
        }
        for (SingleStat stat : data) {
            countryNames.add(stat.getCountry());
        }
        selectCountry.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,countryNames));

        selectCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position!=0){
                    position --;
                    final SingleStat country = data.get(position);
                    MyCountry(
                            country.getFlagResId(),
                            country.getCountry(),
                            (String)country.getCases().get(casesKeys.NEW),
                            (String)country.getDeaths().get(deathsKeys.NEW),
                            (String)country.getTests().get(testsKeys.TOTAL));
                    spEditor.putString(sharedPrefs.MY_COUNTRY_NAME,country.getCountry());
                    spEditor.putBoolean(sharedPrefs.MY_COUNTRY_SELECTED,true);
                    spEditor.apply();
                    myCountryCon.setVisibility(View.VISIBLE);
                    viewMore.setVisibility(View.VISIBLE);
                    viewMore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PassData(country);
                        }
                    });
                }else{
                    spEditor.putBoolean(sharedPrefs.MY_COUNTRY_SELECTED,false);
                    spEditor.apply();
                    myCountryCon.setVisibility(View.GONE);
                    viewMore.setVisibility(View.GONE);
                }
                try{
                    TextView mviw = (TextView)parent.getChildAt(0);
                    mviw.setTextColor(getResources().getColor(android.R.color.white));
                    mviw.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }catch (NullPointerException e){}
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        if (countrySelected){
            String countryName = sharedPreferences.getString(sharedPrefs.MY_COUNTRY_NAME,"Nigeria");
            int i=0;
            for (SingleStat country : data) {
                i++;
                if (countryName.equalsIgnoreCase(country.getCountry())){
                    break;
                }
            }
            if (selectCountry.getSelectedItemPosition()!=i){
                selectCountry.setSelection(i);
            }
        }
    }
    public void PassData(SingleStat stat){
        Bundle casesbundle = new Bundle();
        Bundle deathsbundle = new Bundle();
        Bundle testsbundle = new Bundle();

        HashMap casesHash = stat.getCases();
        HashMap deathsHash = stat.getDeaths();
        HashMap testsHash = stat.getTests();

        casesbundle.putString(casesKeys.NEW      ,(String)casesHash.get(casesKeys.NEW      ));
        casesbundle.putString(casesKeys.ACTIVE   ,(String)casesHash.get(casesKeys.ACTIVE   ));
        casesbundle.putString(casesKeys.CRITICAL ,(String)casesHash.get(casesKeys.CRITICAL ));
        casesbundle.putString(casesKeys.RECOVERED,(String)casesHash.get(casesKeys.RECOVERED));
        casesbundle.putString(casesKeys.TOTAL    ,(String)casesHash.get(casesKeys.TOTAL    ));

        deathsbundle.putString(deathsKeys.NEW      ,(String)deathsHash.get(deathsKeys.NEW      ));
        deathsbundle.putString(deathsKeys.TOTAL  ,(String)deathsHash.get(deathsKeys.TOTAL   ));

        testsbundle.putString(testsKeys.TOTAL ,(String)testsHash.get(testsKeys.TOTAL ));

        Intent countryActivity = new Intent(MainActivity.this,CountryActivity.class);

        countryActivity.putExtra(StatContract.FLAG_RES_ID,stat.getFlagResId());
        countryActivity.putExtra(StatContract.COUNTRY_NAME_KEY,stat.getCountry());
        countryActivity.putExtra(casesKeys.BUNDLE_KEY,casesbundle);
        countryActivity.putExtra(deathsKeys.BUNDLE_KEY,deathsbundle);
        countryActivity.putExtra(testsKeys.BUNDLE_KEY,testsbundle);
        countryActivity.putExtra(StatContract.UPDATE_TIME_KEY,stat.getTime());

        startActivity(countryActivity);
    }

    public void loadData(){
        ConnectivityManager conmag = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conmag.getActiveNetworkInfo();
        if (networkInfo!=null &&networkInfo.isConnected()){
            getSupportLoaderManager().initLoader(1,null,this).forceLoad();
            conInfo.setVisibility(View.GONE);
            loadIndicator.setVisibility(View.VISIBLE);
            findViewById(R.id.mloadImage).startAnimation(AnimationUtils.loadAnimation(this,R.anim.load_anim));
        }else {
            conInfo.setVisibility(View.VISIBLE);
            findViewById(R.id.mconInfoImage).startAnimation(AnimationUtils.loadAnimation(this,R.anim.in_out));
        }
    }
    public void MyCountry(int flag,String name, String cases,String deaths,String tests){
        TextView mname, mcases, mdeaths,mtests;
        ImageView mflag;
        mname = findViewById(R.id.myCountryName);
        mflag = findViewById(R.id.myCountryFlag);
        mcases = findViewById(R.id.myCountryCases);
        mdeaths = findViewById(R.id.myCountryDeaths);
        mtests= findViewById(R.id.myCountryTests);
        mname.setText(name);
        mcases.setText(cases);
        mdeaths.setText(deaths);
        mtests.setText(tests);
        mflag.setImageResource(flag);
    }

    @NonNull
    @Override
    public Loader<ArrayList<SingleStat>> onCreateLoader(int id, @Nullable Bundle args) {
        return new StatsLoader(this,QueryUtil.QUERY_STRING);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<SingleStat>> loader, ArrayList<SingleStat> data) {
        refresh(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<SingleStat>> loader) {
        gridAdapter = new GridAdapter(this,new ArrayList<SingleStat>());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        loadData();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdView.destroy();
    }
}