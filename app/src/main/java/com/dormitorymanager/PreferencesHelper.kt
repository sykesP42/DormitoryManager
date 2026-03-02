package com.dormitorymanager

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "DormitoryPrefs"
        private const val KEY_DORMITORY_NAME = "dormitory_name"
        private const val KEY_START_DATE = "start_date"
        private const val DEFAULT_DORMITORY_NAME = "我的宿舍"
    }

    var dormitoryName: String
        get() = prefs.getString(KEY_DORMITORY_NAME, DEFAULT_DORMITORY_NAME) ?: DEFAULT_DORMITORY_NAME
        set(value) = prefs.edit().putString(KEY_DORMITORY_NAME, value).apply()

    var startDate: String?
        get() = prefs.getString(KEY_START_DATE, null)
        set(value) = prefs.edit().putString(KEY_START_DATE, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
