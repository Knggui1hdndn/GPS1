package com.example.gps.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gps.MyLocationConstants
import com.example.gps.R
import com.example.gps.dao.MyDataBase
import com.example.gps.viewModel.SharedViewModel
import com.example.gps.databinding.FragmentNotificationsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.util.Locale


class NotificationsFragment : Fragment(R.layout.fragment_notifications) {
    private var cameraPosition: CameraPosition? = null

    @SuppressLint("SuspiciousIndentation", "MissingPermission")
    private val callback = OnMapReadyCallback { p0 ->
        map = p0
        map!!.apply {
            isMyLocationEnabled = true
            setMinZoomPreference(15.0f);
            setMaxZoomPreference(35.0f);
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isRotateGesturesEnabled = true
            map?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
//            map?.moveCamera(CameraUpdateFactory.newLatLng(LatLng(20.99605906969354, 105.74779462069273)))
            map?.mapType = GoogleMap.MAP_TYPE_HYBRID
            setOnCameraMoveListener {
                resetMinMaxZoomPreference()
            }
        }
    }
    private var binding: FragmentNotificationsBinding? = null
    private var map: GoogleMap? = null
    private val polylineOptions = PolylineOptions()
    var sharedViewModel: SharedViewModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentNotificationsBinding.bind(view)
        if (savedInstanceState != null) {
            cameraPosition = savedInstanceState.getParcelable("cameraPosition")

        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(callback)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        var i = 0
        if (binding != null) {
            with(binding) {
                val shaPreferences =
                    requireActivity().getSharedPreferences("state", Context.MODE_PRIVATE)
                val state = shaPreferences.getString(MyLocationConstants.STATE, null)
                if (state == MyLocationConstants.START || state == MyLocationConstants.PAUSE || state == MyLocationConstants.RESUME) {
                    val listLating = MyDataBase.getInstance(requireContext()).locationDao()
                        .getLocationData(
                            MyDataBase.getInstance(requireContext()).movementDao()
                                .getLastMovementDataId()
                        )
                    val list =
                        listLating.map { LatLng(it.latitude.toDouble(), it.longitude.toDouble()) }
                            .toList()
                    polylineOptions.addAll(list)
                        .color(Color.GREEN).width(15f)
                }
                sharedViewModel!!.getLocationLiveData().observe(viewLifecycleOwner) { location ->
                    if (i == 0 && map != null && cameraPosition == null) {
                        val sydney = LatLng(location.latitude, location.longitude)
                        map?.addMarker(MarkerOptions().position(sydney).title("User"))
                        map?.moveCamera(CameraUpdateFactory.newLatLng(sydney))
                        i = 1
                    }
                    polylineOptions.add(LatLng(location.latitude, location.longitude))
                        .color(Color.GREEN).width(15f)
                    map?.addPolyline(polylineOptions)
                    this!!.latitude.text = location.latitude.toString()
                    this.longitude.text = location.longitude.toString()
                }

                sharedViewModel!!.getCurrentSpeedLiveData()
                    .observe(viewLifecycleOwner) { Speed ->
                        this!!.txtAverageSpeed.text =
                            String.format(Locale.getDefault(), "%.1f", Speed)
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sharedViewModel = null
        binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("cameraPosition", map?.cameraPosition)
    }

    override fun onPause() {
        super.onPause()
        Log.d("okoko", "onPause")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("okoko", "onDestroy")
    }
}