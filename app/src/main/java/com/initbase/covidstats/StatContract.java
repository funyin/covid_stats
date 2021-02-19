package com.initbase.covidstats;

public class StatContract {

    public static String FLAG_RES_ID = "flagRsId";
    public static String COUNTRY_NAME_KEY = "countryName";
    public static String UPDATE_TIME_KEY = "updateTime";

    public static class casesKeys{
         public final static String BUNDLE_KEY = "cases_bundle";
         public final static String NEW = "new";
         public final static String ACTIVE = "active";
         public final static String CRITICAL = "critical";
         public final static String RECOVERED = "recovered";
         public final static String TOTAL = "total";

    }
    public static class deathsKeys{
        public final static String BUNDLE_KEY= "deaths_bundle";
        public final static String NEW = "new";
        public final static String TOTAL = "total";
    }
    public static class testsKeys{
        public final static String BUNDLE_KEY = "tests_bundle";
        public final static String TOTAL = "total";
    }

    public static class sharedPrefs{
        public final static String MY_COUNTRY_SELECTED = "country_selected";
        public final static String MY_COUNTRY_NAME = "country_name";
    }

}
