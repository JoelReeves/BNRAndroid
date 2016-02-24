package com.bromancelabs.photogallery.services

import android.content.Context
import android.content.SharedPreferences

object QueryPreferencesKt {

    val PREF_NAME = "query_preferences"
    val SEARCH_QUERY = "search_query"
    val LAST_RESULT_ID = "lastResultId"
    val IS_ALARM_ON = "is_alarm_on"

    fun getPreferences(context: Context) = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getEditor(context: Context) = getPreferences(context).edit()

    fun getSearchQuery(context: Context): String? = getPreferences(context).getString(SEARCH_QUERY, null)

    fun setSearchQuery(context: Context, query: String) = getEditor(context).putString(SEARCH_QUERY, query).apply()

    fun getLastResultId(context: Context): String? = getPreferences(context).getString(LAST_RESULT_ID, null)

    fun setLastResultId(context: Context, id: String) = getEditor(context).putString(LAST_RESULT_ID, id).apply()

    fun isAlarmOn(context: Context) = getPreferences(context).getBoolean(IS_ALARM_ON, false)

    fun setAlarmOn(context: Context, isOn: Boolean) = getEditor(context).putBoolean(IS_ALARM_ON, isOn)
}