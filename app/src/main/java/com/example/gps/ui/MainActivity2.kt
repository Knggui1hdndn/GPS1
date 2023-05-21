package com.example.gps.ui

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gps.R
import com.example.gps.Send
import com.example.gps.databinding.ActivityMain2Binding
import com.example.gps.utils.TimeUtils
import com.example.gps.viewModel.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity2 : AppCompatActivity(), Send {


    private lateinit var binding: ActivityMain2Binding
    private var initialTime = 0L
    private var countDownTimer: CountDownTimer? = null
    private var sharedViewModel: SharedViewModel? = null

    @SuppressLint("SetTextI18n")
    override fun send(i: Int) {
        when (i) {
            START -> {
                countingTime(binding.times, Long.MAX_VALUE)!!.start()
                Log.d("ssssssssss", "START")
            }

            PAUSE -> {
                countDownTimer?.cancel()
            }

            RESUME -> {
                countingTime(binding.times, initialTime)!!.start()
            }

            STOP -> {
                countingTime(binding.times, 0)!!.cancel()
                binding.times.text = "00 : 00 : 00"
            }
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        binding.times.typeface = Typeface.createFromAsset(assets, "font_lcd.ttf")
        binding.times.text = "00 : 00 : 00"
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main2)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_home_black_24dp)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ), 1
        )
    }

    private fun countingTime(times: TextView, totalTime: Long): CountDownTimer? {
        sharedViewModel?.getDistanceLiveData()?.observe(this) {
            sharedViewModel?.setAverageSpeed(((it / totalTime) *3.6).toFloat())
            Log.d("ssssssssssss ","${((it / totalTime) *3.6).toFloat()}")
        }
        countDownTimer = object : CountDownTimer(totalTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                initialTime = Long.MAX_VALUE - millisUntilFinished
                times.text = TimeUtils.formatTime(Long.MAX_VALUE - millisUntilFinished)
            }

            override fun onFinish() {
            }
        }
        return countDownTimer

    }
}