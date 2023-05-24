package com.example.gps.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gps.R
import com.example.gps.SharedData
 import com.example.gps.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentHomeBinding.bind(view)
        with(binding) {

            SharedData.currentSpeedLiveData.observe(viewLifecycleOwner) {
                var speed1 = String.format("%.1f", it)
                speed1 = speed1.replace(",", ".");
                if (speed1.toFloat() > 0) {
                    this!!.speed.setSpeedAt(speed1.toFloat())
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}