package com.example.gps.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.gps.MainActivity
import com.example.gps.MyLocationConstants
import com.example.gps.R
import com.example.gps.dao.MyDataBase
import com.example.gps.databinding.ActivityMain2Binding
import com.example.gps.utils.TimeUtils
import com.example.gps.viewModel.SharedViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity2 : AppCompatActivity() {


    private lateinit var binding: ActivityMain2Binding
    private var initialTime = 0L
    private var sharedViewModel: SharedViewModel? = null

    private var intentFilter: IntentFilter? = null
    private val broadCast = object : BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("action", intent?.action.toString())
            when (intent?.action) {
                MyLocationConstants.START -> {
                    val bundle = intent.getBundleExtra("Bundle")

                    binding.times.text =
                        TimeUtils.formatTime(bundle?.getLong(MyLocationConstants.TIME)!!)
                }

                MyLocationConstants.STOP -> {
                    binding.times.text = "00 : 00 : 00"
                }

                MyLocationConstants.LOCATION_CHANGE -> {
                    val bundle = intent.getBundleExtra("Bundle")
                    if (bundle != null) {
                        sharedViewModel?.setCurrentSpeed(bundle.getFloat("speed"))
                        sharedViewModel?.setAverageSpeed(bundle.getFloat("averageSpeed"))
                        sharedViewModel?.setDistance(bundle.getFloat("distance"))
                        sharedViewModel?.setMaxSpeed(bundle.getFloat("speedMax"))
                        sharedViewModel?.setLocationLiveData(bundle.getParcelable("location")!!)
                    }
                }
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.P)
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
        val movementData = MyDataBase.getInstance(this).movementDao()
        val location = MyDataBase.getInstance(this).locationDao()
        Log.d("okokok", location.getLocationData(6).toString())
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main2)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), 1)
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

    override fun onResume() {
        super.onResume()
        intentFilter = IntentFilter()
        intentFilter!!.addAction(MyLocationConstants.START)
        intentFilter!!.addAction(MyLocationConstants.STOP)
        intentFilter!!.addAction(MyLocationConstants.LOCATION_CHANGE)
        registerReceiver(broadCast, intentFilter)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.history) {
            startService(Intent(this, MainActivity::class.java))
        }
        return true
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadCast)
    }
}