package com.dormitorymanager

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    fun applyTheme(context: Context) {
        val prefs = PreferencesHelper(context)
        when (prefs.themeMode) {
            PreferencesHelper.THEME_MODE_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            PreferencesHelper.THEME_MODE_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    fun setThemeMode(context: Context, mode: Int) {
        val prefs = PreferencesHelper(context)
        prefs.themeMode = mode
        applyTheme(context)
    }

    fun recreateActivity(activity: Activity) {
        activity.recreate()
    }
}
