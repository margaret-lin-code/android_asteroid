package com.udacity.asteroidradar.api

import com.udacity.asteroidradar.Constants.APIKEY
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface AsteroidApiService {

    @GET("neo/rest/v1/feed")
    suspend fun getAsteroids(
        @Query("start_date") start_date: String,
        @Query("end_date") end_date: String,
        @Query("api_key") api_key: String
    ): String

    @GET("planetary/apod")
    suspend fun getApod(
        @Query(APIKEY) api_key: String
    ): PictureOfDay


    /**
     * A public Api object that exposes the lazy-initialized Retrofit service to the app
     */
    object AsteroidApi {
        val retrofitService : AsteroidApiService by lazy { retrofit.create(AsteroidApiService::class.java) }
    }
}

/**
 * Use the Retrofit builder to build a retrofit object using a Scalers converter
 */
    private val retrofit
        get() = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

