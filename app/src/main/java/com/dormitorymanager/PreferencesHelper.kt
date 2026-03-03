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
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_REMINDER_ADVANCE_TIME = "reminder_advance_time"
        private const val KEY_REMINDER_REPEAT = "reminder_repeat"
        private const val KEY_REMINDER_VOICE = "reminder_voice"
        private const val KEY_SPLASH_ANIMATION = "splash_animation"
        private const val DEFAULT_DORMITORY_NAME = "我的宿舍"
        private const val DEFAULT_DORMITORY_SIZE = 6
        private const val DEFAULT_REMINDER_HOUR = 8
        private const val DEFAULT_REMINDER_MINUTE = 0
        private const val DEFAULT_CURRENT_USER_ID = -1L
        private const val DEFAULT_REMINDER_ADVANCE_TIME = 0
        private const val DEFAULT_REMINDER_REPEAT = false
        private const val DEFAULT_REMINDER_VOICE = false
        private const val DEFAULT_SPLASH_ANIMATION = true
        const val THEME_MODE_SYSTEM = 0
        const val THEME_MODE_LIGHT = 1
        const val THEME_MODE_DARK = 2
        const val REMINDER_ADVANCE_NONE = 0
        const val REMINDER_ADVANCE_15_MIN = 15
        const val REMINDER_ADVANCE_30_MIN = 30
        const val REMINDER_ADVANCE_1_HOUR = 60
        const val REMINDER_ADVANCE_2_HOURS = 120
        const val REMINDER_ADVANCE_1_DAY = 1440
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

    var currentUserId: Long
        get() = prefs.getLong(KEY_CURRENT_USER_ID, DEFAULT_CURRENT_USER_ID)
        set(value) = prefs.edit().putLong(KEY_CURRENT_USER_ID, value).apply()

    var reminderAdvanceTime: Int
        get() = prefs.getInt(KEY_REMINDER_ADVANCE_TIME, DEFAULT_REMINDER_ADVANCE_TIME)
        set(value) = prefs.edit().putInt(KEY_REMINDER_ADVANCE_TIME, value).apply()

    var reminderRepeat: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_REPEAT, DEFAULT_REMINDER_REPEAT)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER_REPEAT, value).apply()

    var reminderVoice: Boolean
        get() = prefs.getBoolean(KEY_REMINDER_VOICE, DEFAULT_REMINDER_VOICE)
        set(value) = prefs.edit().putBoolean(KEY_REMINDER_VOICE, value).apply()

    var splashAnimation: Boolean
        get() = prefs.getBoolean(KEY_SPLASH_ANIMATION, DEFAULT_SPLASH_ANIMATION)
        set(value) = prefs.edit().putBoolean(KEY_SPLASH_ANIMATION, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }
}
