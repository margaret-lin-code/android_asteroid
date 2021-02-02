package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidsRepository
import kotlinx.coroutines.launch
import java.io.IOException

enum class NasaApiStatus { LOADING, ERROR, DONE }
enum class MediaType(val value: String) { IMAGE("image") }
enum class MenuOptionSelected { TODAY, WEEK, SAVED }

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AsteroidDatabase.getInstance(application)
    private val asteroidsRepository = AsteroidsRepository(database)

    private val _onMenuOptionSelected = MutableLiveData<MenuOptionSelected>()
    val onMenuOptionSelected: LiveData<MenuOptionSelected>
        get() = _onMenuOptionSelected

    val asteroids = Transformations.switchMap(_onMenuOptionSelected) { option ->
        when (option) {
            MenuOptionSelected.TODAY -> asteroidsRepository.asteroidsForToday
            MenuOptionSelected.WEEK -> asteroidsRepository.asteroidsForWeek
            MenuOptionSelected.SAVED -> asteroidsRepository.asteroidsForSaved
        }
    }

    fun showMenuOptionSelected(selected: MenuOptionSelected) {
        _onMenuOptionSelected.value = selected
    }

    private val _status = MutableLiveData<NasaApiStatus>()
    val status: LiveData<NasaApiStatus>
        get() = _status

    private var _nasaImage = MutableLiveData<PictureOfDay>()
    val nasaImage: LiveData<PictureOfDay>
        get() = _nasaImage

    init {
        viewModelScope.launch {
            getImageOfDay()
            asteroidsRepository.refreshCachedAsteroids()
            showMenuOptionSelected(MenuOptionSelected.TODAY)
        }
    }

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
