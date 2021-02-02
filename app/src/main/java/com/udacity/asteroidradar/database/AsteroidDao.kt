package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AsteroidDao {
    @Query("SELECT * FROM asteroid_table ORDER BY closeApproachDate ASC")
    fun getAllAsteroidsFromDB(): LiveData<List<AsteroidEntities>>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate >=:startDate AND closeApproachDate <=:endDate ORDER BY closeApproachDate ASC")
    fun getNextSevenDaysAsteroidFromDB(startDate: String, endDate: String): LiveData<List<AsteroidEntities>>

    @Query("SELECT * FROM asteroid_table WHERE closeApproachDate =:startDate ORDER BY closeApproachDate ASC")
    fun getTodayAsteroidsFromDB(startDate: String): LiveData<List<AsteroidEntities>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: AsteroidEntities)
}