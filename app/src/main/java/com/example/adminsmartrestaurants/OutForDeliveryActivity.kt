package com.example.adminsmartrestaurants

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminsmartrestaurants.adapter.DeliveryAdapter
import com.example.adminsmartrestaurants.databinding.ActivityOutForDeliveryBinding

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding : ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        binding.backbutton.setOnClickListener {
            finish()
        }

        val customerName = arrayOf(
            "Amit",
            "Akash",
            "Amit",
        )
        val moneyStatus = arrayOf(
            "received",
            "notReceived",
            "Pending"
        )
        val adapter = DeliveryAdapter(customerName,moneyStatus)
        binding.DeliveryRecycleView.adapter = adapter
        binding.DeliveryRecycleView.layoutManager = LinearLayoutManager(this)
    }
}