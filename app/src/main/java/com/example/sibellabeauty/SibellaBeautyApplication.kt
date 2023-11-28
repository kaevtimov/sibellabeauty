package com.example.sibellabeauty

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SibellaBeautyApplication @Inject constructor(): Application()