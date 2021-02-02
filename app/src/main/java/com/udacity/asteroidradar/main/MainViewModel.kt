package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import java.io.IOException

enum class NasaApiStatus { LOADING, ERROR, DONE}
enum class MediaType(val value: String) { IMAGE("image"), VIDEO("video") }

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AsteroidDatabase.getInstance(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    init {
        viewModelScope.launch {
            asteroidsRepository.refreshCachedAsteroids()
            getImageOfDay()
        }
    }

    val asteroids = asteroidsRepository.asteroids

    private val _status = MutableLiveData<NasaApiStatus>()
    val status: LiveData<NasaApiStatus>
        get() = _status

    private var _nasaImage = MutableLiveData<PictureOfDay>()
    val nasaImage: LiveData<PictureOfDay>
        get() = _nasaImage

    private fun getImageOfDay() {
        viewModelScope.launch {
            _status.value = NasaApiStatus.LOADING
            try {
                asteroidsRepository.refreshImageOfDay().let {
                    if (it.mediaType == MediaType.IMAGE.value) {
                        _nasaImage.value = it
                    }
                _status.value = NasaApiStatus.DONE
                }
            } catch (e: IOException) {
                _status.value = NasaApiStatus.ERROR
                _nasaImage.value = null
                Log.i("text", "getImageOfDay not working")
            }
        }
    }
}
