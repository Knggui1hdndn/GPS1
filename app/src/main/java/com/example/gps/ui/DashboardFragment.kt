package com.example.gps.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.gps.utils.FontUtils
import com.example.gps.R
import com.example.gps.viewModel.SharedViewModel
import com.example.gps.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private var binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentDashboardBinding.bind(view)
        val  sharedViewModel=ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        with(binding) {
            FontUtils.setFont(requireContext(), this!!.txtSpeed)
            sharedViewModel.getCurrentSpeedLiveData().observe(viewLifecycleOwner){
                txtSpeed.text=String.format("%.1f",it)
            }
            sharedViewModel.getDistanceLiveData().observe(viewLifecycleOwner){
                txtDistance1.text=String.format("%.1f",it)

            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}