package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.Network
import com.udacity.asteroidradar.api.parseStringToAsteroidList
import com.udacity.asteroidradar.database.AppDatabase
import com.udacity.asteroidradar.database.asteroid.toAsteroidEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AsteroidsRepository(val database: AppDatabase) {

    private val sdf = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

    private fun dateFormat(date: Date): String {
        return sdf.format(date)
    }

    val asteroids = MutableLiveData<List<Asteroid>>()

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay> get() = _pictureOfDay

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            val sevenDaysFromNow = Date(Date().time + 604800000L)
            val response = Network.asteroids.getAsteroids(
                dateFormat(Date()),
                dateFormat(sevenDaysFromNow)
            ).execute()
            if (response.body() == null)
                return@withContext
            val asteroids =
                parseStringToAsteroidList(response.body()!!).map {
                    it.toAsteroidEntity()
                }
            database.asteroidDao.insertAll(asteroids)
            getAsteroids()
        }
    }

    suspend fun getPictureOfDay() {
        withContext(Dispatchers.IO) {
            val response = Network.asteroids.getPictureOfDay().execute()
            if (response.body() == null)
                return@withContext
            _pictureOfDay.postValue(response.body())
        }
    }

    suspend fun getAsteroids(initialDate: Date = Date(), finalDate: Date = Date()) {
        withContext(Dispatchers.IO) {
            asteroids.postValue(
                database.asteroidDao.getAllAsteroid(
                    dateFormat(initialDate),
                    dateFormat(finalDate)
                )
            )
        }
    }

    fun clearAsteroidsBeforeToday() {
        database.asteroidDao.clearAsteroids(dateFormat(Date()))
    }

}