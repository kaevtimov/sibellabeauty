package com.example.sibellabeauty

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.example.sibellabeauty.dashboard.EventRepository
import com.example.sibellabeauty.splash.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class SibellaBeautyApplication: Application() {

    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { AppDatabase.getInstance(this, applicationScope) }
    val usersRepo by lazy { UserRepository.getInstance(database.userDao()) }
    val eventsRepo by lazy { EventRepository.getInstance(database.eventDao()) }

    override fun onCreate() {
        super.onCreate()
        ctx = this
    }

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var ctx: Context

        fun context(): Context {
            return ctx
        }
    }
}