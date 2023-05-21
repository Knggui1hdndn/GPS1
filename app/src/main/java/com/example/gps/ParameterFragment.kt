package com.example.gps

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.gps.dao.MyDataBase
import com.example.gps.databinding.FragmentParameterBinding
import com.example.gps.model.MovementData
import com.example.gps.service.MyService
import com.example.gps.ui.MainActivity2
import com.example.gps.utils.FontUtils
import com.example.gps.viewModel.SharedViewModel
import java.util.Locale


class ParameterFragment : Fragment(R.layout.fragment_parameter) {
    private var binding: FragmentParameterBinding? = null
    private var check = false
    var map: Map? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding = FragmentParameterBinding.bind(view)
        val sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        map = Map(requireContext(), sharedViewModel)
        with(binding) {
             val myDataBase: MyDataBase = MyDataBase.getInstance(requireContext())

            setFont(binding!!)
            this!!.btnStart.setOnClickListener {
                (requireActivity() as MainActivity2).send(MainActivity2.START)
                check = true
                map!!.startListenerLocationChanges()
             //   requireActivity().startService(Intent(requireContext(), MyService::class.java))
            }
            btnResume.setOnClickListener {
                (requireActivity() as MainActivity2).send(MainActivity2.RESUME)
                map!!.resumeListenerLocationChanges()
            }
            btnPause.setOnClickListener {
                (requireActivity() as MainActivity2).send(MainActivity2.PAUSE)
                map!!.pauseListenerLocationChanges()
            }
            btnStop.setOnClickListener {
                (requireActivity() as MainActivity2).send(MainActivity2.STOP)
                map!!.stopListenerLocationChanges()
            }
            sharedViewModel.getCheck().observe(viewLifecycleOwner) {
                Log.d("sssssssssss", "$it")


                when (it) {

                    MainActivity2.START -> {
                        btnStart.visibility = View.GONE
                        mframeLayout.visibility = View.VISIBLE
                        btnStop.visibility = View.VISIBLE

                    }

                    MainActivity2.PAUSE -> {
                        btnStart.visibility = View.GONE
                        btnPause.visibility = View.GONE
                        btnResume.visibility = View.VISIBLE
                        mframeLayout.visibility = View.VISIBLE
                        btnStop.visibility = View.VISIBLE

                    }

                    MainActivity2.STOP -> {
                        btnStart.visibility = View.VISIBLE
                        mframeLayout.visibility = View.GONE
                        btnStop.visibility = View.GONE

                    }

                    MainActivity2.RESUME -> {
                        btnStart.visibility = View.GONE
                        btnPause.visibility = View.VISIBLE
                        btnResume.visibility = View.GONE
                        mframeLayout.visibility = View.VISIBLE
                        btnStop.visibility = View.VISIBLE

                    }
                }
            }
            sharedViewModel.getAverageSpeedLiveData().observe(viewLifecycleOwner) { averageSpeed ->
                txtAverageSpeed.text = String.format(Locale.getDefault(), "%.1f", averageSpeed)
            }
            sharedViewModel.getDistanceLiveData().observe(viewLifecycleOwner) { distance ->
                txtDistance.text = String.format(Locale.getDefault(), "%.1f", distance)

            }
            sharedViewModel.getMaxSpeedLiveData().observe(viewLifecycleOwner) { speed ->
                txtMaxSpeed.text = String.format(Locale.getDefault(), "%.1f", speed)
            }
        }
    }

    private fun setAnimation(binding: FragmentParameterBinding) {
        with(binding) {
            btnPause.post {
                val screenWidth = resources.displayMetrics.widthPixels
                val buttonWidth = btnPause.width
                val tran: Float = ((screenWidth - buttonWidth) / 2).toFloat()
                val animator1 =
                    ObjectAnimator.ofFloat(mframeLayout, "translationX", 0f, -tran)
                animator1.duration = if (!check) 0 else 500
                animator1.start()

                val animator = ObjectAnimator.ofFloat(btnStop, "translationX", 0f, tran)
                animator.duration = if (!check) 0 else 500
                animator.start()
            }
        }
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

}