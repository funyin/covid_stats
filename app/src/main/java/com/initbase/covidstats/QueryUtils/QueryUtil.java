package com.initbase.covidstats.QueryUtils;

import android.util.Log;

import com.blongho.country_data.World;
import com.initbase.covidstats.SingleStat;
import com.initbase.covidstats.StatContract;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;

import com.initbase.covidstats.StatContract.*;

public class QueryUtil {
    private QueryUtil(){
    }
    private static int TOTAL_INFECTED_COUNTRIES = 0;
    public static final String QUERY_STRING = "https://covid-193.p.rapidapi.com/statistics";

    public static int getTotalInfectedCountries() {
        return TOTAL_INFECTED_COUNTRIES;
    }

    public static String makeConnection(String url) throws IOException {

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-host", "covid-193.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "0107d10e7fmsh11e33f87f04eb0bp1b88a5jsn887214672b56")
                .build();

        Response response = client.newCall(request).execute();
        String mresponse = null;
        mresponse = response.body().string();
        return mresponse;
    }

    public static ArrayList<SingleStat> parseJson(String json){
        if (json==null){
            return null;
        }
        HashMap cases ,deaths,tests;
        ArrayList<SingleStat> statArrayList = new ArrayList<>();

        try {
            JSONObject  body = new JSONObject(json);
            JSONArray response = body.getJSONArray("response");
            for (int i=0;i<response.length();i++){
                JSONObject country = response.getJSONObject(i);
                String countryName = country.getString("country");
                if (countryName.matches(".*[a-z].*")){
                    String countryCode = getCountryCode(countryName);
                    World.init(StatsLoader.context.getApplicationContext());
                    int flagResId = World.getFlagOf(countryCode);
                    JSONObject countryCases = country.getJSONObject("cases");
                    JSONObject countrydeaths = country.getJSONObject("deaths");
                    JSONObject countrytests = country.getJSONObject("tests");
                    String time = country.getString("time");
                    try {
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM");
                        time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(time).toString();
                        String[] splitTime = time.split(" ");
                        String[] splitSplitTime = splitTime[3].split(":");
                        splitTime[3] = splitSplitTime[0]+":"+splitSplitTime[1];
                        time = splitTime[0]+" "+splitTime[1]+" "+splitTime[2]+", "+splitTime[3]+" "+splitTime[4];
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    cases = casesToHashMap(countryCases);
                    deaths = deathsToHashMap(countrydeaths);
                    tests = testsToHashMap(countrytests);
                    statArrayList.add(new SingleStat(flagResId,countryName,cases,deaths,tests,time));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        TOTAL_INFECTED_COUNTRIES = statArrayList.size();
        Collections.sort(statArrayList, new Comparator<SingleStat>() {
            @Override
            public int compare(SingleStat o1, SingleStat o2) {
                return o1.getCountry().compareTo(o2.getCountry());
            }
        });
        return  statArrayList;
    }

    public static String nullTest(String string){
        if (string.equalsIgnoreCase("null")){
            return "0";
        }else {
            return string;
        }
    }

    public static  HashMap casesToHashMap(JSONObject cases) throws JSONException {
        String mnew, active, critical,recovered,total;
        mnew = cases.getString("new");
        active = String.valueOf(cases.getInt("active"));
        critical = String.valueOf(cases.getInt("critical"));
        recovered = String.valueOf(cases.getInt("recovered"));
        total = String.valueOf(cases.getInt("total"));

        mnew =    nullTest(mnew);
        active =    nullTest(active);
        critical =  nullTest(critical);
        recovered = nullTest(recovered);
        total =     nullTest(total);

        active = getFormatedNumber(active);
        critical = getFormatedNumber(critical);
        recovered = getFormatedNumber(recovered);
        total = getFormatedNumber(total);

        HashMap hashMap = new HashMap();
        hashMap.put(casesKeys.NEW,mnew);
        hashMap.put(casesKeys.ACTIVE,active);
        hashMap.put(casesKeys.CRITICAL,critical);
        hashMap.put(casesKeys.RECOVERED,recovered);
        hashMap.put(casesKeys.TOTAL,total);
        return hashMap;
    }
    public static HashMap deathsToHashMap(JSONObject cases) throws JSONException {
        String mnew, total;
        mnew = cases.getString("new");
        total = cases.getString("total");

        mnew = nullTest(mnew);
        total = nullTest(total);

        HashMap hashMap = new HashMap();
        hashMap.put(deathsKeys.NEW,mnew);
        hashMap.put(deathsKeys.TOTAL,total);
        return hashMap;
    }
    public static HashMap testsToHashMap(JSONObject cases) throws JSONException {
        String total;
        total = String.valueOf(cases.optInt("total"));

        total = getFormatedNumber(total);

        HashMap hashMap = new HashMap();
        hashMap.put(testsKeys.TOTAL,total);
        return hashMap;
    }
    public static String getCountryCode(String countryName){
        String[] isoCountryCodes = Locale.getISOCountries();
        countryName = countryName.replace("-"," ");
        for (String code : isoCountryCodes) {
            Locale locale = new Locale("",code);
            if (countryName.equalsIgnoreCase(locale.getDisplayCountry())){
                return  code;
            }else {
                if (countryName.equalsIgnoreCase("Ivory Coast")){
                    return "civ";
                }else if (countryName.equalsIgnoreCase("Congo")){
                    return "cog";
                }else if (countryName.equalsIgnoreCase("Guinea Bissau")){
                    return "gnb";
                }else if (countryName.equalsIgnoreCase("US Virgin Islands")){
                    return "vir";
                }else if (countryName.equalsIgnoreCase("S Korea")){
                    return "kor";
                }
            }
        }

        return countryName;
    }

    public static String getFormatedNumber(String number){
        try {
            return NumberFormat.getNumberInstance(Locale.getDefault()).format(Double.parseDouble(number));
        }catch (Exception e){
            return  number;
        }
    }
}
