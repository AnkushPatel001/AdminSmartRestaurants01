package com.example.adminsmartrestaurants.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adminsmartrestaurants.databinding.PendingorderitemBinding

class PendingOrderAdapter(
    private val customerName: ArrayList<String>,
    private val foodImage: ArrayList<Int>,
    private val quantity: ArrayList<Int>,
    private val context: Context
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = PendingorderitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = customerName.size

    inner class PendingOrderViewHolder(private val binding: PendingorderitemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isAccepted = false

        fun bind(position: Int) {
            binding.apply {
                customername.text = customerName[position]
                Quantity.text = quantity[position].toString()
                foodImageView.setImageResource(foodImage[position])

                OrderAcceptButton.apply {
                    text = if (!isAccepted) "Accept" else "Dispatch"

                    setOnClickListener {
                        if (!isAccepted) {
                            isAccepted = true
                            text = "Dispatch"
                            showToast("Order is Accepted")
                        } else {
                            customerName.removeAt(adapterPosition)
                            foodImage.removeAt(adapterPosition)
                            quantity.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            showToast("Order is Dispatched")
                        }
                    }
                }
            }
        }

        private fun showToast(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
