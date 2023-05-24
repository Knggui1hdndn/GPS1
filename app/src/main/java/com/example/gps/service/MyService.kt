package com.example.gps.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.widget.EdgeEffectCompat.getDistance
import androidx.lifecycle.ViewModelProvider
import com.example.gps.MyLocationConstants
import com.example.gps.R
import com.example.gps.SharedData
import com.example.gps.dao.MyDataBase
import com.example.gps.model.MovementData
import com.example.gps.ui.MainActivity2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Calendar
import java.util.Locale


class MyService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private var checkStop: Boolean = false
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var myDataBase: MyDataBase? = null
    private var millis = 0L
    private val countDownTimer = Handler(Looper.getMainLooper())
    private var distance = 0f
    private var checkStart = false
    private var checkEnd = false
    private var lastMovementDataId = 0
    private var mili = 0L
    private var previousLocation: Location? = null
    private var listSpeed = mutableListOf<Float>()
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        myDataBase = MyDataBase.getInstance(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        handle(intent?.action)
        return START_NOT_STICKY
    }

    private val runnable = {
        countDownTimer()
    }

    private fun countDownTimer() {
        millis += 1000
        SharedData.time.value = millis
        countDownTimer.postDelayed(runnable, 1000)
    }


    private val locationCallback1 = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        @SuppressLint("SuspiciousIndentation")
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            if (lastLocation != null && previousLocation != null && mili != 0L && myDataBase != null) {
                if (!checkStart) {
                     myDataBase!!.movementDao().insertMovementData(
                        MovementData(
                            0,
                            System.currentTimeMillis(),
                            lastLocation.latitude ,
                            lastLocation.longitude ,
                            0.0,
                            0.0,
                            0F,
                            0F,
                            0F,
                            0F
                        )
                    )
                    lastMovementDataId = myDataBase!!.movementDao().getLastMovementDataId()
                    checkStart = true
                }
                listSpeed.add(getCurrentSpeed(lastLocation))
                val distance=getDistance(lastLocation)
                val currentSpeed=getCurrentSpeed(lastLocation)
                val maxSpeed= getMaxSpeed()
                val averageSpeed=getAverageSpeed()
                 with(SharedData) {
                    locationLiveData.value = lastLocation
                    distanceLiveData.value = distance
                    currentSpeedLiveData.value = currentSpeed
                    maxSpeedLiveData.value =maxSpeed
                    averageSpeedLiveData.value = averageSpeed
                }

                if (checkEnd || checkStop) {
                    val movementData = myDataBase!!.movementDao().getLastMovementData()
                    movementData.time = millis.toFloat()
                    movementData.averageSpeed = averageSpeed
                    movementData.maxSpeed =maxSpeed
                    movementData.endLatitude = lastLocation.latitude
                    movementData.endLongitude = lastLocation.longitude
                    movementData.distance = distance

                    myDataBase!!.movementDao().updateMovementData(movementData)
                    if (checkStop) {
                        stopSelf()
                        removeCallBack()
                        countDownTimer.removeCallbacks(runnable)
                        with(SharedData) {
                            locationLiveData.value = null
                            distanceLiveData.value = 0F
                            currentSpeedLiveData.value = 0F
                            maxSpeedLiveData.value = 0F
                            averageSpeedLiveData.value = 0F
                            time.value = 0

                        }
                    }
                }
            }
            //s=vt
            previousLocation = lastLocation
            mili = System.currentTimeMillis()
        }
    }

    private fun getCurrentSpeed(lastLocation: Location): Float {
        val time = (System.currentTimeMillis() - mili) / 1000.0
        val s = nearestDistance(lastLocation)
        return ((s / time) * 3.6).toFloat()
    }

    private fun getMaxSpeed(): Float {
        return listSpeed.max()
    }

    private fun nearestDistance(lastLocation: Location): Float {
        return previousLocation!!.distanceTo(lastLocation)
    }

    private fun getDistance(lastLocation: Location): Float {
        distance += previousLocation!!.distanceTo(lastLocation)
        return distance/1000
    }

    private fun getAverageSpeed(): Float {
        return (3.6 * (distance / (millis / 1000.0))).toFloat()
    }


    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
        .setWaitForAccurateLocation(true)
        .setMinUpdateIntervalMillis(0)
        .setMaxUpdateDelayMillis(0)
        .build()

    private fun handle(action: String?) {
        when (action) {
            MyLocationConstants.START -> {
                checkStart = false
                startCallBack()
                startForeground(1, getNotifications("0", "0", "0"))
                countDownTimer.postDelayed(runnable, 1000)
            }

            MyLocationConstants.PAUSE -> {
                checkEnd = true
                fusedLocationClient?.removeLocationUpdates(locationCallback1)
                countDownTimer.removeCallbacks(runnable)
            }

            MyLocationConstants.RESUME -> {
                checkEnd = false
                countDownTimer.postDelayed(runnable, 1000)
                startCallBack()

            }

            MyLocationConstants.STOP -> {
                checkStop = true


            }
        }
    }


    private fun sendBroadCast(action: String?, bundle: Bundle?) {
        val intent = Intent(action)
        intent.putExtra("Bundle", bundle)
        applicationContext.sendBroadcast(intent)
    }

    @SuppressLint("MissingPermission")
    private fun startCallBack() {
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback1,
            Looper.getMainLooper()
        )
    }

    private fun removeCallBack() {
        fusedLocationClient?.removeLocationUpdates(
            locationCallback1
        )
    }

    private fun updateNotification(km: String, distance: String, maxSpeed: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, getNotifications(km, distance, maxSpeed))
    }

    private fun getNotifications(km: String, distance: String, maxSpeed: String): Notification {
        val notificationLayout = RemoteViews(packageName, R.layout.notification_custom)
        val intent = Intent(applicationContext, MainActivity2::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_IMMUTABLE
        )
        notificationLayout.setTextViewText(R.id.txtKm, km)
        notificationLayout.setTextViewText(R.id.txtDistance, distance)
        notificationLayout.setTextViewText(R.id.txtMaxSpeed, maxSpeed)
        return NotificationCompat.Builder(this, "1")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setCustomContentView(notificationLayout)
            .build()
    }
}


