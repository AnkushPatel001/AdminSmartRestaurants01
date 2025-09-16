package com.example.adminsmartrestaurants

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminsmartrestaurants.adapter.PendingOrderAdapter
import com.example.adminsmartrestaurants.databinding.ActivityPendingOrderBinding

class PendingOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendingOrderBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.backbutton.setOnClickListener {
            finish()
        }
    }
}
