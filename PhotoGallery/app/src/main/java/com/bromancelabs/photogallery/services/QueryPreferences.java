package com.bromancelabs.photogallery.services;

import android.content.Context;
import android.content.SharedPreferences;

public final class QueryPreferences {

    private static final String PREF_NAME = "query_preferences";
    private static final String SEARCH_QUERY = "search_query";

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
}
