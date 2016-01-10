package com.bromancelabs.photogallery.services;

import android.content.Context;
import android.content.SharedPreferences;

public final class QueryPreferences {

    private static final String PREF_NAME = "query_preferences";
    private static final String SEARCH_QUERY = "search_query";
    private static final String LAST_RESULT_ID = "lastResultId";
    private static final String IS_ALARM_ON = "is_alarm_on";

    private QueryPreferences() {}

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static String getSearchQuery(Context context) {
        return getPreferences(context).getString(SEARCH_QUERY, null);
    }

    public static void setSearchQuery(Context context, String query) {
        getEditor(context).putString(SEARCH_QUERY, query).apply();
    }

    public static String getLastResultId(Context context) {
        return getPreferences(context).getString(LAST_RESULT_ID, null);
    }

    public static void setLastResultId(Context context, String id) {
        getEditor(context).putString(LAST_RESULT_ID, id).apply();
    }

    public static boolean isAlarmOn(Context context) {
        return getPreferences(context).getBoolean(IS_ALARM_ON, false);
    }

    public static void setAlarmOn(Context context, boolean isOn) {
        getEditor(context).putBoolean(IS_ALARM_ON, isOn);
    }
}
