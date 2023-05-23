package com.example.gps.ui

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gps.databinding.ItemBinding
import com.example.gps.model.MovementData
import com.example.gps.utils.TimeUtils

class HistoryAdapter(private var list: MutableList<MovementData>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {
    class HistoryViewHolder(private val binding: ItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movementData: MovementData) {

            with(binding) {
                btnSeeMore.setOnClickListener {
                    binding.root.context.startActivity(Intent(it.context, ShowActivity::class.java))
                }
                txtDate.text = movementData.date
                txtMaxSpeed.text = movementData.maxSpeed.toString()
                txtTime.text = TimeUtils.formatTime(movementData.time.toLong())
                txtStart.text = movementData.start
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(ItemBinding.bind(parent))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(list[position])
    }
}