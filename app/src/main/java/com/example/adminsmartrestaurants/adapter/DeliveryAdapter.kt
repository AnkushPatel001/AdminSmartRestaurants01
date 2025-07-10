package com.example.adminsmartrestaurants.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.adminsmartrestaurants.databinding.DeliveryitemBinding

class DeliveryAdapter(
    private val customerName: Array<String>,
    private val moneyStatus: Array<String>
) : RecyclerView.Adapter<DeliveryAdapter.DeliveryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeliveryViewHolder {
        val binding = DeliveryitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DeliveryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DeliveryViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerName.size

    inner class DeliveryViewHolder(private val binding: DeliveryitemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                customername.text = customerName[position]
                StatusMoney.text = moneyStatus[position]


                val colorMap = mapOf(
                    "received" to Color.GREEN,
                    "notReceived" to Color.RED,
                    "Pending" to Color.GRAY
                )

                val statusColor = colorMap[moneyStatus[position]] ?: Color.BLACK
                StatusMoney.setTextColor(statusColor)
                StatusColor.backgroundTintList = ColorStateList.valueOf(statusColor)
            }
        }
    }
}
