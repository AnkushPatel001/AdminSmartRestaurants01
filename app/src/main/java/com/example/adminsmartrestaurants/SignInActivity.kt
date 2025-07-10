package com.example.adminsmartrestaurants

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminsmartrestaurants.databinding.ActivitySignInBinding
import com.example.adminsmartrestaurants.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var nameOfRestaurant: String

    private val binding: ActivitySignInBinding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Initialize Firebase Authentication and Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Setup location dropdown
        val locationList = arrayOf("Jaipur", "Faridabad", "Delhi", "Noida", "Mumbai", "Kolkata")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        binding.listofloaction.setAdapter(adapter)

        // Sign-up button click
        binding.Createbutton.setOnClickListener {
            username = binding.editname.text.toString().trim()
            nameOfRestaurant = binding.editRestaurant.text.toString().trim()
            email = binding.editphoneoremail.text.toString().trim()
            password = binding.editpassword.text.toString().trim()

            if (username.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(email, password)
            }
        }

        // Already have account click
        binding.alreadyhaveaccount.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                saveUserData()

                // Optional: Save extra data to Firebase Database
                val uid = auth.currentUser?.uid ?: ""
                val userMap = mapOf(
                    "username" to username,
                    "restaurantName" to nameOfRestaurant,
                    "email" to email
                )
                database.child("admins").child(uid).setValue(userMap)

                // Navigate to Login
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                Log.e("Account", "CreateAccount: Failure", task.exception)
            }
        }
    }
// save data into the database
    private fun saveUserData() {
        username = binding.editname.text.toString().trim()
        nameOfRestaurant = binding.editRestaurant.text.toString().trim()
        email = binding.editphoneoremail.text.toString().trim()
        password = binding.editpassword.text.toString().trim()
        val user = UserModel(username,nameOfRestaurant,email,password)
        val userId :String =  FirebaseAuth.getInstance().currentUser!!.uid
    //save the data in fireb12wase
        database.child("user").child(userId).setValue(user)

    }
}
