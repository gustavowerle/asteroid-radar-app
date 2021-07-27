package com.udacity.asteroidradar.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.i(WORK_NAME, "Trying do the work...")
            val database = getDatabase(applicationContext)
            val repository = AsteroidsRepository(database)
            repository.refreshAsteroids()
            repository.clearAsteroidsBeforeToday()
            Result.success()
        } catch (e: HttpException) {
            Log.e(WORK_NAME, "Network error..., ${e.message}")
            Result.retry()
        } catch (e: Exception) {
            Log.e(WORK_NAME, "Unknown error..., ${e.message}")
            Result.failure()
        }
    }
}