package com.example.common.di

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureStore @Inject constructor(
    private val prefs: SharedPreferences
) {

    fun putString(key: String, str: String?) = prefs.edit { putString(key, str) }

    fun getString(key: String, defaultStr: String?) = prefs.getString(key, defaultStr)

    fun putBoolean(key: String, bool: Boolean) = prefs.edit { putBoolean(key, bool) }

    fun getBoolean(key: String, defaultBoolean: Boolean) = prefs.getBoolean(key, defaultBoolean)

    fun remove(key: String) = prefs.edit { remove(key) }

    fun removeAll() = prefs.edit { clear() }
}