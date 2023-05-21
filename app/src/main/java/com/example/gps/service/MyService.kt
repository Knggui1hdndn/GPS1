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
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.gps.Map
import com.example.gps.MyLocationConstants
import com.example.gps.R
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


class MyService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private var fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(applicationContext)
    private var map: Map? = null
    private var shared: SharedViewModel? = null
    private var sharedPreferences = getSharedPreferences("state", MODE_PRIVATE)
    private var myDataBase = MyDataBase.getInstance(applicationContext)
    private var millis = 0L
    private val countDownTimer = Handler(Looper.getMainLooper())
    private val runnable = {
        countDownTimer()
    }

    private fun countDownTimer() {
        millis += 1000
        val bundle = Bundle()
        bundle.putFloat(MyLocationConstants.TIME, millis.toFloat())
        sendBroadCast(MyLocationConstants.START, bundle)
        countDownTimer.postDelayed(runnable, 1000)
    }

    override fun onCreate() {
        super.onCreate()
        map = Map(applicationContext, null)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        map?.startListenerLocationChanges()
        handle(intent?.action)
        return START_NOT_STICKY
    }

    private var distance = 0f
    var timePrevious: Long? = null
    private var checkStart = false
    private var checkEnd = false
    private var previousLocation: Location? = null
    private val locationCallback1 = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation = locationResult.lastLocation
            if (lastLocation != null && previousLocation != null) {
                distance += previousLocation?.let { lastLocation.distanceTo(it) }!!
            }

            if (!checkStart) {
                getAddress(lastLocation).let {
                    myDataBase.movementDao().insertMovementData(
                        MovementData(
                            0,
                            it?.getAddressLine(0).toString(),
                            null,
                            0F,
                            0F,
                            0F,
                            0F
                        )
                    )
                }
                checkStart = true
            }
            if (checkEnd) {
                getAddress(lastLocation)?.let {
                    val movementData = myDataBase.movementDao().getLastMovementData()
                    movementData.end = it.getAddressLine(0)
                    movementData.averageSpeed = ((distance/millis)*3.6).toFloat()
                    movementData.distance=distance/1000
                    movementData.time= millis.toFloat()
                    movementData.maxSpeed=locationResult.locations.map { it.speed }.max()
                        myDataBase.movementDao().updateMovementData(movementData)
                }
            }
            previousLocation = lastLocation
            timePrevious=System.currentTimeMillis()
        }

    }

    private fun getAddress(lastLocation: Location?): Address? {
        val geocoder = Geocoder(applicationContext, Locale.getDefault())
        val address = geocoder.getFromLocation(
            lastLocation?.latitude!!,
            lastLocation.longitude, 1
        )

        return address?.get(0)
    }

    private val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 0)
        .setWaitForAccurateLocation(true)
        .setMinUpdateIntervalMillis(0)
        .setMaxUpdateDelayMillis(100)
        .build()

    private fun handle(action: String?) {

        sharedPreferences.edit().putString("state", action).apply()
        when (action) {
            MyLocationConstants.START -> {
                start()
                countDownTimer.postDelayed(runnable, 1000)
            }

            MyLocationConstants.PAUSE -> {
                pause()
                countDownTimer.removeCallbacks(runnable)
            }

            MyLocationConstants.RESUME -> {
                resume()
                countDownTimer.postDelayed(runnable, 1000)
            }

            MyLocationConstants.STOP -> {
                stop()
                stopSelf()
            }
        }

    }

    private fun stop() {

    }

    private fun resume() {

    }

    private fun pause() {
        checkEnd = true
    }

    private fun sendBroadCast(action: String?, bundle: Bundle?) {
        val intent = Intent(action)
        intent.putExtra("Bundle", bundle)
        applicationContext.sendBroadcast(intent)
    }

    @SuppressLint("MissingPermission")
    private fun start() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback1,
            Looper.getMainLooper()
        )
    }

    private fun showNotification(movementData: MovementData) {
        val notificationLayout = RemoteViews(packageName, R.layout.notification_custom)
//        notificationLayout.setOnClickPendingIntent(
//            R.id.btnAccept,
//            onButtonNotificationClick(R.id.btnAccept)
//        )
//        notificationLayout.setOnClickPendingIntent(
//            R.id.btnDenied,
//            onButtonNotificationClick(R.id.btnDenied)
//        )
        val intent = Intent(applicationContext, MainActivity2::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT else PendingIntent.FLAG_IMMUTABLE
        )
        notificationLayout.setTextViewText(R.id.txtKm, "s")
        notificationLayout.setTextViewText(R.id.txtDistance, "s")
        notificationLayout.setTextViewText(R.id.txtMaxSpeed, "s")
        val notification: Notification = NotificationCompat.Builder(this, "1")
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setCustomContentView(notificationLayout)
            .build()
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }
}


