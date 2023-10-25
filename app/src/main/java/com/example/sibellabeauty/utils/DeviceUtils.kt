package com.example.sibellabeauty.utils

import android.annotation.SuppressLint
import android.provider.Settings
import com.example.sibellabeauty.SibellaBeautyApplication

object DeviceUtils {

    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(SibellaBeautyApplication.context().contentResolver, Settings.Secure.ANDROID_ID)
    }
}