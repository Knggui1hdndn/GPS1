package com.example.gps.ui


import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.gps.MyLocationConstants
import com.example.gps.R
import com.example.gps.databinding.FragmentParameterBinding
import com.example.gps.service.MyService
import com.example.gps.utils.FontUtils
import com.example.gps.viewModel.SharedViewModel

class ParameterFragment : Fragment(R.layout.fragment_parameter) {
    private var binding: FragmentParameterBinding? = null
    private var check = false
    private var sharedPreferences: SharedPreferences? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentParameterBinding.bind(view)
        sharedPreferences = requireActivity().getSharedPreferences("state", Service.MODE_PRIVATE)
        with(binding) {
            val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
            sharedViewModel.getMaxSpeedLiveData().observe(viewLifecycleOwner) {
                this!!.txtMaxSpeed.text=String.format("%.1f",it)
            }
            sharedViewModel.getDistanceLiveData().observe(viewLifecycleOwner) {
                this!!.txtDistance.text=String.format("%.1f",it)
                Log.d("ssssss",it.toString())
            }
            sharedViewModel.getAverageSpeedLiveData().observe(viewLifecycleOwner) {
                this!!.txtAverageSpeed.text=String.format("%.1f",it)
            }

            showOrHideView()
            setFont(binding!!)
            this!!.btnStart.setOnClickListener {
                setState(MyLocationConstants.START)
                hideBtnStart()
                startService(MyLocationConstants.START)
            }

            btnResume.setOnClickListener {
                setState(MyLocationConstants.RESUME)
                hideBtnResume()
                startService(MyLocationConstants.RESUME)
            }
            btnPause.setOnClickListener {
                setState(MyLocationConstants.PAUSE)
                hideBtnPause()
                startService(MyLocationConstants.PAUSE)
            }
            btnStop.setOnClickListener {
                setState(MyLocationConstants.STOP)
                hideBtnStop()
                startService(MyLocationConstants.STOP)

            }
        }
    }

    private fun showOrHideView() {
        when (sharedPreferences?.getString(MyLocationConstants.STATE, null)) {
            MyLocationConstants.START -> {
                hideBtnStart()
            }

            MyLocationConstants.PAUSE -> {
                hideBtnPause()
            }

            MyLocationConstants.RESUME -> {
                hideBtnResume()
            }

            MyLocationConstants.STOP -> {
                hideBtnStop()
            }
        }
    }

    private fun hideBtnStart() {
        with(binding) {
            this!!.btnStart.visibility = View.GONE
            mframeLayout.visibility = View.VISIBLE
            btnStop.visibility = View.VISIBLE
        }
    }

    private fun hideBtnPause() {
        with(binding) {
            this!!.btnStart.visibility = View.GONE
            btnPause.visibility = View.GONE
            btnResume.visibility = View.VISIBLE
            mframeLayout.visibility = View.VISIBLE
            btnStop.visibility = View.VISIBLE
        }
    }

    private fun hideBtnResume() {
        with(binding) {
            this!!.btnStart.visibility = View.GONE
            btnPause.visibility = View.VISIBLE
            btnResume.visibility = View.GONE
            mframeLayout.visibility = View.VISIBLE
            btnStop.visibility = View.VISIBLE
        }
    }

    private fun hideBtnStop() {
        with(binding) {
            this!!.btnStart.visibility = View.VISIBLE
            mframeLayout.visibility = View.GONE
            btnStop.visibility = View.GONE
        }
    }


    private fun startService(action: String) {
        val intent = Intent(requireContext(), MyService::class.java)
        intent.action = action
        requireActivity().startService(intent)
    }

    private fun setState(state: String) {
        sharedPreferences!!.edit().putString(MyLocationConstants.STATE, state).apply()
    }


    override fun onPause() {
        super.onPause()
        check = false
    }

    private fun setFont(binding: FragmentParameterBinding) {
        with(binding) {
            FontUtils.setFont(
                requireContext(), txtKm, txtKm1, txtKm2, txtDistance, txtAverageSpeed, txtMaxSpeed
            )
        }
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onDestroy() {
        super.onDestroy()
    }

}