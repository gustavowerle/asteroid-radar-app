package com.udacity.asteroidradar.database.asteroid

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {

    @Query("SELECT * FROM asteroid WHERE closeApproachDate >= :initialDate AND closeApproachDate <= :finalDate ORDER BY closeApproachDate DESC")
    fun getAllAsteroid(initialDate: String, finalDate: String): List<Asteroid>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(asteroids: List<AsteroidEntity>)

    @Query("DELETE FROM asteroid WHERE closeApproachDate < :date")
    fun clearAsteroids(date: String)

}