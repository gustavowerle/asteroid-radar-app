package com.udacity.asteroidradar.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.udacity.asteroidradar.database.asteroid.AsteroidDao
import com.udacity.asteroidradar.database.asteroid.AsteroidEntity

@Database(
    entities = [
        AsteroidEntity::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AppDatabase

fun getDatabase(context: Context): AppDatabase {
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "app"
            ).build()
        }
    }
    return INSTANCE
}