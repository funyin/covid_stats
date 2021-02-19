package com.initbase.covidstats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.initbase.covidstats.QueryUtils.QueryUtil;
import com.initbase.covidstats.QueryUtils.StatsLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class CountryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<SingleStat>> {
    TextView lastUpdated;
    String countryName,updated,newQuery;
    ArrayList<TabFrag> fragments;
    PagerAdapter adapter;
    ViewPager pager;
    TabLayout tabHost;
    View loaderIndicator,conInfo;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country);

        Toolbar toolbar = findViewById(R.id.toolBar);
        lastUpdated = findViewById(R.id.lastUpdated);
        ImageView flag = findViewById(R.id.collapseFlag);
        tabHost = findViewById(R.id.ctab);
        pager = findViewById(R.id.cviewPager);
        loaderIndicator = findViewById(R.id.loadIndicator);
        conInfo = findViewById(R.id.connectionInfo);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setSupportActionBar(toolbar);

        Intent incoming = getIntent();
        countryName = incoming.getStringExtra(StatContract.COUNTRY_NAME_KEY);
        flag.setImageResource(incoming.getIntExtra(StatContract.FLAG_RES_ID,R.drawable.ng));
        getSupportActionBar().setTitle(countryName);

        fragments = new ArrayList<>();
        refresh(incoming);

        newQuery = QueryUtil.QUERY_STRING+"?country="+countryName;
    }
    public void refresh(Intent intent){
        updated = intent.getStringExtra(StatContract.UPDATE_TIME_KEY);
        lastUpdated.setText(updated);

        CasesFragment casesFragment = new CasesFragment();
        DeathsFragment deathsFragment = new DeathsFragment();
        TestsFragment testsFragment = new TestsFragment();

        fragments.clear();

        fragments.add(new TabFrag(getResources().getString(R.string.cases),casesFragment,intent.getBundleExtra(StatContract.casesKeys.BUNDLE_KEY)));
        fragments.add(new TabFrag(getResources().getString(R.string.deaths),deathsFragment,intent.getBundleExtra(StatContract.deathsKeys.BUNDLE_KEY)));
        fragments.add(new TabFrag(getResources().getString(R.string.tests),testsFragment,intent.getBundleExtra(StatContract.testsKeys.BUNDLE_KEY)));

        adapter = new PagerAdapter(getSupportFragmentManager(),fragments);
        pager.setAdapter(adapter);
        tabHost.setupWithViewPager(pager);
        for (int m=0;m<fragments.size();m++){
            tabHost.getTabAt(m).setText(fragments.get(m).getTabTitle());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.countryactivity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.refresh:
                loadData();
                break;
        }
        return true;
    }
    public void loadData(){
        ConnectivityManager conmag = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conmag.getActiveNetworkInfo();
        if (networkInfo!=null &&networkInfo.isConnected()){
            getSupportLoaderManager().initLoader(1,null,this).forceLoad();
            conInfo.setVisibility(View.GONE);
            loaderIndicator.setVisibility(View.VISIBLE);
            findViewById(R.id.mloadImage).startAnimation(AnimationUtils.loadAnimation(this,R.anim.load_anim));
        }else {
            conInfo.setVisibility(View.VISIBLE);
            findViewById(R.id.mconInfoImage).startAnimation(AnimationUtils.loadAnimation(this,R.anim.in_out));
        }
    }

    @NonNull
    @Override
    public Loader<ArrayList<SingleStat>> onCreateLoader(int id, @Nullable Bundle args) {
        return new StatsLoader(this,newQuery);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<ArrayList<SingleStat>> loader, ArrayList<SingleStat> data) {
        SingleStat stat = data.get(0);
        Bundle casesbundle = new Bundle();
        Bundle deathsbundle = new Bundle();
        Bundle testsbundle = new Bundle();

        HashMap casesHash = stat.getCases();
        HashMap deathsHash = stat.getDeaths();
        HashMap testsHash = stat.getTests();


        casesbundle.putString(StatContract.casesKeys.NEW      ,(String)casesHash.get(StatContract.casesKeys.NEW      ));
        casesbundle.putString(StatContract.casesKeys.ACTIVE   ,(String)casesHash.get(StatContract.casesKeys.ACTIVE   ));
        casesbundle.putString(StatContract.casesKeys.CRITICAL ,(String)casesHash.get(StatContract.casesKeys.CRITICAL ));
        casesbundle.putString(StatContract.casesKeys.RECOVERED,(String)casesHash.get(StatContract.casesKeys.RECOVERED));
        casesbundle.putString(StatContract.casesKeys.TOTAL    ,(String)casesHash.get(StatContract.casesKeys.TOTAL    ));

        deathsbundle.putString(StatContract.deathsKeys.NEW      ,(String)deathsHash.get(StatContract.deathsKeys.NEW      ));
        deathsbundle.putString(StatContract.deathsKeys.TOTAL  ,(String)deathsHash.get(StatContract.deathsKeys.TOTAL   ));

        testsbundle.putString(StatContract.testsKeys.TOTAL ,(String)testsHash.get(StatContract.testsKeys.TOTAL ));

        Intent countryActivity = new Intent();

        countryActivity.putExtra(StatContract.FLAG_RES_ID,stat.getFlagResId());
        countryActivity.putExtra(StatContract.COUNTRY_NAME_KEY,stat.getCountry());
        countryActivity.putExtra(StatContract.casesKeys.BUNDLE_KEY,casesbundle);
        countryActivity.putExtra(StatContract.deathsKeys.BUNDLE_KEY,deathsbundle);
        countryActivity.putExtra(StatContract.testsKeys.BUNDLE_KEY,testsbundle);
        countryActivity.putExtra(StatContract.UPDATE_TIME_KEY,stat.getTime());

        refresh(countryActivity);
        loaderIndicator.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<ArrayList<SingleStat>> loader) {
        fragments.clear();
    }

    public void backCliked(View view) {
        finish();
    }

    static class TabFrag{
        String tabTitle;
        Fragment frag;
        Bundle arguments;
        public TabFrag(String title, Fragment fragment,Bundle arguments){
            tabTitle = title;
            frag = fragment;
            this.arguments = arguments;
        }

        public Fragment getFrag() {
            return frag;
        }

        public String getTabTitle() {
            return tabTitle;
        }

        public Bundle getArguments() {
            return arguments;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdView.destroy();
    }
}