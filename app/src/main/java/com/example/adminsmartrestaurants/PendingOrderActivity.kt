package com.example.adminsmartrestaurants

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminsmartrestaurants.adapter.PendingOrderAdapter
import com.example.adminsmartrestaurants.databinding.ActivityPendingOrderBinding
import com.example.adminsmartrestaurants.model.OrderModel
import com.google.firebase.database.*

class PendingOrderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPendingOrderBinding
    private lateinit var adapter: PendingOrderAdapter
    private lateinit var database: DatabaseReference
    private var orderList = ArrayList<OrderModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPendingOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().getReference("OrderDetails")

        adapter = PendingOrderAdapter(this, orderList)
        binding.pendingOrderRecycleView.layoutManager = LinearLayoutManager(this)
        binding.pendingOrderRecycleView.adapter = adapter

        findViewById<ImageView>(R.id.backbutton).setOnClickListener {
            finish()
        }

        loadPendingOrders()
    }

    private fun loadPendingOrders() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()

                for (userSnap in snapshot.children) {
                    for (orderSnap in userSnap.children) {
                        // Only try to convert if it's a Map, not a String
                        if (orderSnap.value is Map<*, *>) {
                            val order = orderSnap.getValue(OrderModel::class.java)
                            if (order != null && order.orderAccepted == false) {
                                order.itemPushKey = orderSnap.key
                                order.userId = userSnap.key
                                orderList.add(order)
                            }
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
