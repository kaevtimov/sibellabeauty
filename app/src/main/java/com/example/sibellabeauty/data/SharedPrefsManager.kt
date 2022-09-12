package com.example.sibellabeauty.data

import android.content.Context
import androidx.core.content.edit
import com.example.sibellabeauty.SibellaBeautyApplication

object SharedPrefsManager {

    private const val MY_PREFS = "my_custom_prefs"
    private const val USER_KEY_VALUE = "username_key"

    fun saveUserLoggedIn(user: String) {
        SibellaBeautyApplication.context().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE).edit {
            putString(USER_KEY_VALUE, user)
        }
    }

    fun logoutUser() {
        SibellaBeautyApplication.context().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE).edit {
            remove(USER_KEY_VALUE)
        }
    }

    fun getLoggedInUser(): String? =
        SibellaBeautyApplication.context().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE).getString(USER_KEY_VALUE, "")
}