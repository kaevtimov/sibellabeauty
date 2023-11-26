package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.event.EventDao
import com.example.data.event.EventRepository
import com.example.data.event.IEventRepository
import com.example.data.user.IUserRepository
import com.example.data.user.UserDao
import com.example.data.user.UserRepository
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val FIREBASE_DATABASE_URL = "https://sibellabeauty-default-rtdb.europe-west1.firebasedatabase.app/"
private const val DB_NAME = "app_db"

@InstallIn(SingletonComponent::class)
@Module
internal object DataModule {

    @Singleton
    @Provides
    fun provideUserRepository(
        userRepositoryImpl: UserRepository
    ): IUserRepository = userRepositoryImpl

    @Singleton
    @Provides
    fun provideEventRepository(
        eventRepositoryImpl: EventRepository
    ): IEventRepository = eventRepositoryImpl

    @Singleton
    @Provides
    fun provideFirebaseDatabase(): DatabaseReference =
        FirebaseDatabase.getInstance(FIREBASE_DATABASE_URL).reference

    @Provides
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            DB_NAME
        ).build()
    }

    @Provides
    fun provideEventDao(appDatabase: AppDatabase): EventDao {
        return appDatabase.eventDao()
    }
}
