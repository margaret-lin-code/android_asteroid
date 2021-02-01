package com.udacity.asteroidradar.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AsteroidDatabase.getInstance(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    init {
        viewModelScope.launch {
            asteroidsRepository.refreshCachedAsteroids()
        }
    }

    val asteroids = asteroidsRepository.asteroids

//    private var _nasaImage = MutableLiveData<PictureOfDay>()
//    val nasaImage: LiveData<PictureOfDay>
//        get() = _nasaImage
//
//    private fun getImageOfDay() {
//        viewModelScope.launch {
//            try {
//                _nasaImage = asteroidsRepository.refreshImageOfDay()
//            } catch (e: Exception) {
//                Log.i("text", "getImageOfDay not working")
//            }
//        }
//    }
}
