package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroid_table")
    fun getAsteroidsFromDB(): LiveData<List<AsteroidDatabase>>
}