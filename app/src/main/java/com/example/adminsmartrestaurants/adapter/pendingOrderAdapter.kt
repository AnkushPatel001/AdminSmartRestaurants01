package com.example.adminsmartrestaurants.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.adminsmartrestaurants.databinding.PendingorderitemBinding
import com.example.adminsmartrestaurants.model.OrderModel
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class PendingOrderAdapter(
    private val context: Context,
    private val orderList: ArrayList<OrderModel>
) : RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder>() {

    private val database = FirebaseDatabase.getInstance().getReference("OrderDetails")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = PendingorderitemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int = orderList.size

    inner class PendingOrderViewHolder(private val binding: PendingorderitemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderModel) {
            binding.apply {
                customername.text = order.userName
                Quantity.text = order.foodQuantities?.joinToString(", ")

                // Load first food image using Picasso
                order.foodImages?.firstOrNull()?.let { imageUrl ->
                    Picasso.get().load(imageUrl).into(foodImageView)
                }

                OrderAcceptButton.text = if (!order.orderAccepted) "Accept" else "Dispatch"

                OrderAcceptButton.setOnClickListener {
                    order.itemPushKey?.let { key ->
                        if (!order.orderAccepted) {
                            database.child(order.userId ?: "unknown").child(key)
                                .child("orderAccepted").setValue(true)
                            Toast.makeText(context, "Order Accepted", Toast.LENGTH_SHORT).show()
                            order.orderAccepted = true
                            OrderAcceptButton.text = "Dispatch"
                        } else {
                            database.child(order.userId ?: "unknown").child(key).removeValue()
                            Toast.makeText(context, "Order Dispatched", Toast.LENGTH_SHORT).show()
                            orderList.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                        }
                    }
                }
            }
        }
    }
}
