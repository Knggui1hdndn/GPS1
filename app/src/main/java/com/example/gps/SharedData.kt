package com.example.gps

import android.location.Location
import androidx.lifecycle.MutableLiveData

object SharedData {
      val averageSpeedLiveData = MutableLiveData<Float>()
      val maxSpeedLiveData = MutableLiveData<Float>()
      val currentSpeedLiveData = MutableLiveData<Float>()
      val distanceLiveData = MutableLiveData<Float>()
      val locationLiveData = MutableLiveData<Location>()
      val check = MutableLiveData<Int>()
      val time= MutableLiveData<Long>(0)
}
