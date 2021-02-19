package com.initbase.covidstats;

import java.util.HashMap;

public class SingleStat {
    private int flagResId;
    private String country, time;
    private HashMap cases, deaths, tests;
    public SingleStat(int flagResId, String country, HashMap cases, HashMap deaths, HashMap tests, String time){
        this.flagResId = flagResId;
        this.country = country;
        this.cases = cases;
        this.deaths = deaths;
        this.tests = tests;
        this.time = time;
    }

    public int getFlagResId() {
        return flagResId;
    }

    public String getCountry() {
        return country;
    }

    public HashMap getCases() {
        return cases;
    }

    public HashMap getDeaths() {
        return deaths;
    }

    public HashMap getTests() {
        return tests;
    }

    public String getTime() {
        return time;
    }

}
