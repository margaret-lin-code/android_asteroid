package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants.APIKEY
import com.udacity.asteroidradar.api.AsteroidApiService
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _asteroid = MutableLiveData<Asteroid>()
    val asteroid: LiveData<Asteroid>
        get() = _asteroid

    init {
//        _asteroid.value = Asteroid(
//            1, "me", "mee", 1.2,
//            1.1, 2.2, 2.2, false
//        )
        getAsteroidsToday()
    }

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private fun getAsteroidsToday() {
        viewModelScope.launch {
            try {
                var listResult = AsteroidApiService.AsteroidApi.retrofitService.getAsteroids(
                    "2021-01-30", "", APIKEY
                )
                val asteroidList = JSONObject(listResult)
                val parseAsteroidsJsonResult = parseAsteroidsJsonResult(asteroidList)
//                Log.i("test", parseAsteroidsJsonResult)
                _response.value = "Success: ${listResult} Asteroid retrieved"
            } catch (e: Exception) {
                _response.value = "Failure: ${e.message}"
                Log.i("test", "not working")
            }
        }
    }
}
