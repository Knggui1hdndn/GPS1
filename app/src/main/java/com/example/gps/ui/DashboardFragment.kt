package com.example.gps.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
 import com.example.gps.utils.FontUtils
import com.example.gps.R
import com.example.gps.SharedData
 import com.example.gps.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentDashboardBinding.bind(view)
         with(binding) {
            FontUtils.setFont(requireContext(), this!!.txtSpeed)
            SharedData.currentSpeedLiveData.observe(viewLifecycleOwner){
                txtSpeed.text=String.format("%.1f",it)
            }
             SharedData.distanceLiveData.observe(viewLifecycleOwner){
                txtDistance1.text=String.format("%.1f",it)

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}