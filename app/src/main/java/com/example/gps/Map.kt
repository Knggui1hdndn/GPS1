package com.example.gps

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Looper
import android.util.Log
import com.example.gps.dao.MyDataBase
import com.example.gps.model.MovementData
import com.example.gps.ui.MainActivity2
import com.example.gps.viewModel.SharedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale


class Map(context: Context, private val viewModel: SharedViewModel?) : MapContract.Presenter {
    private var distance = 0f
    private val list = mutableListOf<Float>()
    private val listLocation = mutableListOf<Location>()
    var previousLocation: Location? = null
    var check=false
    var timePrevious: Long? = null
    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val myDataBase: MyDataBase = MyDataBase.getInstance(context)
    private val locationCallback1 = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            listLocation.add(lastLocation!!)
            if(check.not()){
                val geocoder=Geocoder(context, Locale.getDefault())
                val list=geocoder.getFromLocation(lastLocation.latitude,lastLocation.longitude,1,)
                myDataBase.movementDao().insertMovementData(MovementData(0,
                    list?.get(0)?.getAddressLine(0).toString(),null,0F,0F,0F,0F))
            }
            myDataBase.locationDao().insertLocationData(lastLocation.latitude,lastLocation.longitude,myDataBase.movementDao().getLastMovementDataId())
            if (locationResult.locations.size >= 2 && previousLocation != null) {
                val timeDifference: Long = lastLocation.time - previousLocation?.time!!
                val speed = (lastLocation.distanceTo(previousLocation!!)) / (timeDifference / 1000.0f)
                list.add((speed * 3.6).toFloat())
                viewModel?.setCurrentSpeed((speed * 3.6).toFloat())
            }
            viewModel?.setDistance(calculateDistance(listLocation))
            if (list.size > 0) {
                viewModel?.setMaxSpeed(maxSpeed(list))
            }
            viewModel?.setLocationLiveData(lastLocation)
            previousLocation = locationResult.lastLocation
            timePrevious = System.currentTimeMillis()
        }
    }

    private fun calculateDistance(locations: List<Location>): Float {
        if (locations.size >= 2) {
            distance += locations[locations.size - 1].distanceTo(locations[locations.size - 2])
            return distance / 1000
        }
        return 0f
    }


    private fun maxSpeed(locations: List<Float>): Float {
        return locations.max()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            val latitude = lastLocation?.latitude
            val longitude = lastLocation?.longitude
            stopListenerLocationChanges()
        }
    }

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
        .setWaitForAccurateLocation(true)
        .setMinUpdateIntervalMillis(0)
        .setMaxUpdateDelayMillis(100)
        .build()


    @SuppressLint("MissingPermission")
    override fun getLocationCurrent() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

    }

    @SuppressLint("MissingPermission")
    override fun getLocationChanges() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback1,
            Looper.getMainLooper()
        )

    }

    override fun stopListenerLocationChanges() {
        viewModel?.setCheck(MainActivity2.STOP)
        fusedLocationClient.removeLocationUpdates(locationCallback1)
    }

    override fun resumeListenerLocationChanges() {
        viewModel?.setCheck(MainActivity2.RESUME)
        getLocationChanges()
    }

    override fun pauseListenerLocationChanges() {
        viewModel?.setCheck(MainActivity2.PAUSE)
        fusedLocationClient.removeLocationUpdates(locationCallback1)

    }

    override fun startListenerLocationChanges() {
        viewModel?.setCheck(MainActivity2.START)
        getLocationChanges()
    }
}