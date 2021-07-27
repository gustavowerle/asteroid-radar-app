package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import java.util.*

class MainViewModel(val repository: AsteroidsRepository) : ViewModel() {

    val asteroidsList = repository.asteroids
    val pictureOfDay = repository.pictureOfDay

    private val _navigateToAsteroidDetail = MutableLiveData<Asteroid>()

    val navigateToAsteroidDetail: LiveData<Asteroid>
        get() = _navigateToAsteroidDetail

    fun displayAsteroidDetail(asteroid: Asteroid) {
        _navigateToAsteroidDetail.value = asteroid
    }

    fun displayAsteroidDetailComplete() {
        _navigateToAsteroidDetail.value = null
    }

    init {
        getAsteroids()
        refreshAsteroids()
        getPictureOfDay()
    }

    private fun getAsteroids() {
        viewModelScope.launch {
            repository.getAsteroids()
        }
    }

    private fun getPictureOfDay() {
        viewModelScope.launch {
            try {
                repository.getPictureOfDay()
            } catch (e: Exception) {
                Log.e("getPictureOfDay", e.message.toString())
            }
        }
    }

    private fun refreshAsteroids() {
        viewModelScope.launch {
            try {
                repository.refreshAsteroids()
            } catch (e: Exception) {
                Log.e("refreshAsteroids", e.message.toString())
            }
        }
    }

    fun filterAsteroids(filter: String) {
        viewModelScope.launch {
            try {
                val initialDate = Calendar.getInstance()
                val finalDate = Calendar.getInstance()
                when (filter) {
                    WEEK_ASTEROIDS -> {
                        initialDate.set(Calendar.DAY_OF_WEEK, 1)
                        finalDate.set(Calendar.DAY_OF_WEEK, 7)
                    }
                    SAVED_ASTEROIDS -> {
                        initialDate.set(Calendar.YEAR, 0)
                        finalDate.set(Calendar.YEAR, 9999)
                    }
                }
                repository.getAsteroids(initialDate.time, finalDate.time)
            } catch (e: Exception) {
                Log.e("filterAsteroids", e.message.toString())
            }
        }
    }

    companion object {
        const val WEEK_ASTEROIDS = "WEEK"
        const val TODAY_ASTEROIDS = "TODAY"
        const val SAVED_ASTEROIDS = "SAVED"
    }
}