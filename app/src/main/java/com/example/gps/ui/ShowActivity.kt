package com.example.gps.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.gps.R
import com.example.gps.dao.MyDataBase
import com.example.gps.databinding.ActivityShowBinding
import com.example.gps.databinding.BottomSheetBinding
import com.example.gps.databinding.FragmentNotificationsBinding
import com.example.gps.model.MovementData
import com.example.gps.utils.TimeUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

class ShowActivity : AppCompatActivity() {
    private lateinit var bottomSheet: LinearLayout
    private lateinit var binding: ActivityShowBinding
    private lateinit var bottom: BottomSheetBinding
    @SuppressLint("MissingPermission")
    private val callback = OnMapReadyCallback { p0 ->
        map = p0
        map!!.apply {
             setMinZoomPreference(15.0f);
            setMaxZoomPreference(35.0f);
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isRotateGesturesEnabled = true
            map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(20.99605906969354, 105.74779462069273)))
            map?.mapType = GoogleMap.MAP_TYPE_HYBRID
            setOnCameraMoveListener {
                resetMinMaxZoomPreference()
            }
        }
    }
    private var map: GoogleMap? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowBinding.inflate(layoutInflater)
        bottom = binding.bottom
        setContentView(binding.root)
        bottomSheet = bottom.bottomSheet
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(callback)

        val bottom = BottomSheetBehavior.from(bottomSheet)
        bottomSheet.post {
            bottom.peekHeight = bottomSheet.getHeight() / 2 //height is ready
        }
        val intent = intent
        val movementData = intent.extras?.getSerializable("movementData")
        setData(movementData as MovementData?)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("SimpleDateFormat", "SetTextI18n")
    private fun setData(movementData: MovementData?) {
        val polylineOptions = PolylineOptions()
        val df: DateFormat = SimpleDateFormat("dd/MM/yy_HH:mm:ss")
        with(bottom)
        {
            if (movementData != null) {
                Log.d("sssssssss", MyDataBase.getInstance(this@ShowActivity).locationDao()
                    .getLocationData(movementData.id).toString()
                )
                map?.addPolyline(
                    polylineOptions.addAll(
                        MyDataBase.getInstance(this@ShowActivity).locationDao()
                            .getLocationData(movementData.id).map {
                            LatLng(
                                it.latitude, it.longitude
                            )
                        })
                        .color(Color.GREEN).width(15f)
                )
                timeStart.text = "Trip${df.format(movementData.date)}"

                txtAddressEnd.text=  Geocoder(
                    this@ShowActivity,
                    Locale.getDefault()
                ).getFromLocation(movementData.endLatitude, movementData.endLongitude, 1)!![0].getAddressLine(0)
                txtAddressStart.text=  Geocoder(
                    this@ShowActivity,
                    Locale.getDefault()
                ).getFromLocation(movementData.startLatitude, movementData.startLongitude, 1)!![0].getAddressLine(0)
                txtSpeed.text = "${movementData.maxSpeed}km/h"
                txtAverageSpeed.text = "${movementData.averageSpeed}km/h"
                txtTime.text = "${TimeUtils.formatTime(movementData.time.toLong())}"
                txtDistance.text = "${movementData.distance}km"
            }
        }
    }
}