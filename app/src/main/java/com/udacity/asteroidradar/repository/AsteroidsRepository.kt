package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.*
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

// Repository for fetching from network Asteroids to store them in local DB
class AsteroidsRepository(private val database: AsteroidDatabase) {
    private val nextSevenDaysFormattedDates = getNextSevenDaysFormattedDates()

    // Load the asteroids from offline cache
    // Need to convert from database Asteroid to domain Asteroid so it can be shown on the screen
    val asteroidsForSaved: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao.getAllAsteroidsFromDB()) {
        it.asDomainModel()
    }
    
    val asteroidsForWeek: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao
        .getNextSevenDaysAsteroidFromDB(nextSevenDaysFormattedDates[0], nextSevenDaysFormattedDates[6])) {
        it.asDomainModel()
    }

    val asteroidsForToday: LiveData<List<Asteroid>> = Transformations.map(database.asteroidDao
        .getTodayAsteroidsFromDB(nextSevenDaysFormattedDates[0])) {
        it.asDomainModel()
    }


    // Refresh the database to make sure it's up-to-date
    suspend fun refreshCachedAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val listResult = AsteroidApiService.AsteroidApi.retrofitService.getAsteroids(
                    nextSevenDaysFormattedDates[0], nextSevenDaysFormattedDates[7], API_KEY
                )
                val jsonAsteroids = JSONObject(listResult)
                val asteroidsList = parseAsteroidsJsonResult(jsonAsteroids)
                Log.i("test", asteroidsList.toString())

                database.asteroidDao.insertAll(*NetworkAsteroidsContainer(asteroidsList).asDatabaseModel())

            } catch (e: Exception) {
                Log.i("test", "refreshCachedAsteroids not working")
            }
        }
    }

    // Get Image Of Day
    suspend fun refreshImageOfDay(): PictureOfDay {
        return AsteroidApiService.AsteroidApi.retrofitService.getImageOfDayFromNasa(API_KEY)
    }
}