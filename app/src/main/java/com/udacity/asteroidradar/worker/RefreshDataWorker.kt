package com.udacity.asteroidradar.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import retrofit2.HttpException
import timber.log.Timber

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
    CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Timber.i("Trying do the work...")
            val database = getDatabase(applicationContext)
            val repository = AsteroidsRepository(database)
            repository.refreshAsteroids()
            repository.clearAsteroidsBeforeToday()
            Result.success()
        } catch (e: HttpException) {
            Timber.e("Network error..., ${e.message}")
            Result.retry()
        } catch (e: Exception) {
            Timber.e("Unknown error..., ${e.message}")
            Result.failure()
        }
    }
}