package com.example.sibellabeauty

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.sibellabeauty.dashboard.EventDao
import com.example.sibellabeauty.dashboard.EventFb
import com.example.sibellabeauty.splash.User
import com.example.sibellabeauty.splash.UserDao
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.charset.Charset

@Database(entities = [User::class], exportSchema = true, version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun eventDao(): EventDao

    companion object {
        // tdsfsdgsgsgs
        private const val DB_NAME = "app_db"
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val TAG = this::class.java.toString()

        fun getInstance(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, scope).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .addCallback(
                    object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            val initUsers = getUsers(context)
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    prepopulateDb(initUsers)
                                }
                            }
                        }
                    }
                )
                .build()
        }

        private fun getUsers(context: Context): List<User> {
            return try {
                context.applicationContext.assets.open("users.json").use { inputStream ->
                    val size: Int = inputStream.available()
                    val buffer = ByteArray(size)
                    inputStream.read(buffer)
                    inputStream.close()
                    val jsonString = String(buffer, Charset.defaultCharset())

                    val plantType = object : TypeToken<List<User>>() {}.type
                    Gson().fromJson(jsonString, plantType)
                }
            } catch (ex: Exception) {
                Log.e(TAG, "Error pre-filling database", ex)
                emptyList()
            }
        }

        private suspend fun prepopulateDb(users: List<User>) {
            INSTANCE?.userDao()?.fillWithUsers(users)
        }
    }

}