package com.dormitorymanager

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "DormitoryPrefs"
        private const val KEY_DORMITORY_NAME = "dormitory_name"
        private const val KEY_START_DATE = "start_date"
        private const val KEY_DORMITORY_SIZE = "dormitory_size"
        private const val KEY_REMINDER_ENABLED = "reminder_enabled"
        private const val KEY_REMINDER_HOUR = "reminder_hour"
        private const val KEY_REMINDER_MINUTE = "reminder_minute"
        private const val KEY_THEME_MODE = "theme_mode"
        private const val DEFAULT_DORMITORY_NAME = "我的宿舍"
        private const val DEFAULT_DORMITORY_SIZE = 6
        private const val DEFAULT_REMINDER_HOUR = 8
        private const val DEFAULT_REMINDER_MINUTE = 0
        const val THEME_MODE_SYSTEM = 0
        const val THEME_MODE_LIGHT = 1
        const val THEME_MODE_DARK = 2
    }

    var dormitoryName: String
        get() = prefs.getString(KEY_DORMITORY_NAME, DEFAULT_DORMITORY_NAME) ?: DEFAULT_DORMITORY_NAME
        set(value) = prefs.edit().putString(KEY_DORMITORY_NAME, value).apply()

    var startDate: String?
        get() = prefs.getString(KEY_START_DATE, null)
        set(value) = prefs.edit().putString(KEY_START_DATE, value).apply()

    var dormitorySize: Int
        get() = prefs.getInt(KEY_DORMITORY_SIZE, DEFAULT_DORMITORY_SIZE)
        set(value) = prefs.edit().putInt(KEY_DORMITORY_SIZE, value).apply()

    var reminderEnabled: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_ENABLED, false)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER_ENABLED, value).apply()

    var reminderHour: Int
        get() = prefs.getInt(KEY_REMINDER_HOUR, DEFAULT_REMINDER_HOUR)
        set(value) = prefs.edit().putInt(KEY_REMINDER_HOUR, value).apply()

    var reminderMinute: Int
        get() = prefs.getInt(KEY_REMINDER_MINUTE, DEFAULT_REMINDER_MINUTE)
        set(value) = prefs.edit().putInt(KEY_REMINDER_MINUTE, value).apply()

    var themeMode: Int
        get() = prefs.getInt(KEY_THEME_MODE, THEME_MODE_SYSTEM)
        set(value) = prefs.edit().putInt(KEY_THEME_MODE, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
