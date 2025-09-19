package com.example.adminsmartrestaurants

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminsmartrestaurants.databinding.ActivityAddItemBinding
import com.example.adminsmartrestaurants.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class AddItemActivity : AppCompatActivity() {

    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredient: String
    private lateinit var foodImageUrl: String

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.backbutton.setOnClickListener {
            finish()
        }
        binding.imageUrl.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val url = binding.imageUrl.text.toString().trim()
                if (url.isNotEmpty()) {
                    Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.img_4)
                        .error(R.drawable.img_4)
                        .into(binding.itemImagechnge, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                Toast.makeText(this@AddItemActivity, "Image Loaded", Toast.LENGTH_SHORT).show()
                            }

                            override fun onError(e: java.lang.Exception?) {
                                Toast.makeText(this@AddItemActivity, "Invalid Image URL", Toast.LENGTH_SHORT).show()
                                e?.printStackTrace()
                            }
                        })
                }
            }
        }


        // Add item button listener
        binding.Additembutton.setOnClickListener {
            foodName = binding.foodName.text.toString().trim()
            foodPrice = binding.foodPrice.text.toString().trim()
            foodDescription = binding.foodDescription.text.toString().trim()
            foodIngredient = binding.foodIngredients.text.toString().trim()
            foodImageUrl = binding.imageUrl.text.toString().trim()

            if (foodName.isNotBlank() && foodPrice.isNotBlank() &&
                foodDescription.isNotBlank() && foodIngredient.isNotBlank() &&
                foodImageUrl.isNotBlank()
            ) {
                uploadData()
            } else {
                Toast.makeText(this, "Fill All The Details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadData() {
        // ✅ Admin ke andar Menu node me item save hoga
        val menuRef: DatabaseReference = database.getReference("admins").child("menu")
        val key = "${foodName}_${System.currentTimeMillis()}"

        val newItem = AllMenu(
            foodName = foodName,
            foodPrice = foodPrice,
            foodDescription = foodDescription,
            foodIngredients = foodIngredient,
            foodImage = foodImageUrl
        )

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading Item...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        menuRef.child(key).setValue(newItem).addOnSuccessListener {
            progressDialog.dismiss()
            Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_SHORT).show()
            clearInputs()
            setResult(RESULT_OK)
            finish()
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(this, "Data Upload Failed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun clearInputs() {
        binding.foodName.text.clear()
        binding.foodPrice.text.clear()
        binding.foodDescription.text.clear()
        binding.foodIngredients.text.clear()
        binding.imageUrl.text.clear()
        binding.itemImagechnge.setImageResource(R.drawable.img_3)
    }
}
