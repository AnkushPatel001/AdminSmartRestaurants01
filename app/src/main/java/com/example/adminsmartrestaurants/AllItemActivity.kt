package com.example.adminsmartrestaurants

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.adminsmartrestaurants.adapter.AddItemAdapter
import com.example.adminsmartrestaurants.databinding.ActivityAllItemBinding
import com.example.adminsmartrestaurants.model.AllMenu
import com.google.firebase.database.*
class AllItemActivity : AppCompatActivity() {

    private val binding: ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: AddItemAdapter
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // ✅ Ab items Admin/Menu ke andar jayenge
        database = FirebaseDatabase.getInstance()
            .getReference("admins")
            .child("menu")

        adapter = AddItemAdapter(this)
        binding.MenuRecycleView.layoutManager = LinearLayoutManager(this)
        binding.MenuRecycleView.adapter = adapter

        binding.backbutton.setOnClickListener {
            finish()
        }

        fetchItemsOnceFromDatabase()
    }

    private fun fetchItemsOnceFromDatabase() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = ArrayList<AllMenu>()
                for (itemSnapshot in snapshot.children) {
                    val item = itemSnapshot.getValue(AllMenu::class.java)
                    item?.let { tempList.add(it) }
                }
                adapter.updateData(tempList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@AllItemActivity,
                    "Failed to fetch data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        fetchItemsOnceFromDatabase()
    }
}
