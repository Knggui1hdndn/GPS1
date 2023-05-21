package com.example.gps.viewModel

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val averageSpeedLiveData = MutableLiveData<Float>()
    private val maxSpeedLiveData = MutableLiveData<Float>()
    private val currentSpeedLiveData = MutableLiveData<Float>()
    private val distanceLiveData = MutableLiveData<Float>()
    private val locationLiveData = MutableLiveData<Location>()
    private val check = MutableLiveData<Int>()
    fun setAverageSpeed(speed: Float) {
        averageSpeedLiveData.value = speed
    }
    fun setCheck (boolean: Int) {
        check .value = boolean
    }
    fun setLocationLiveData(location: Location) {
        locationLiveData.value = location
    }

    fun setMaxSpeed(speed: Float) {
        maxSpeedLiveData.value = speed
    }

    fun setCurrentSpeed(speed: Float) {
        currentSpeedLiveData.value = speed
    }

    fun setDistance(distance: Float) {
        distanceLiveData.value = distance
    }

    fun getAverageSpeedLiveData(): LiveData<Float> = averageSpeedLiveData
    fun getCheck(): LiveData<Int> = check

    fun getMaxSpeedLiveData(): LiveData<Float> = maxSpeedLiveData

    fun getCurrentSpeedLiveData(): LiveData<Float> = currentSpeedLiveData
    fun getLocationLiveData(): LiveData<Location> = locationLiveData

    fun getDistanceLiveData(): LiveData<Float> = distanceLiveData
}
