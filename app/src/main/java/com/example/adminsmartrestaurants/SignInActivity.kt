package com.example.adminsmartrestaurants

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.adminsmartrestaurants.databinding.ActivitySignInBinding
import com.example.adminsmartrestaurants.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivitySignInBinding by lazy {
        ActivitySignInBinding.inflate(layoutInflater)
    }

    companion object {
        private const val RC_SIGN_IN = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Setup Google Sign-In Options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Location dropdown
        val locationList = arrayOf("Jaipur", "Faridabad", "Delhi", "Noida", "Mumbai", "Kolkata")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        binding.listofloaction.setAdapter(adapter)

        // Manual Signup
        binding.Createbutton.setOnClickListener {
            val username = binding.editname.text.toString().trim()
            val restaurant = binding.editRestaurant.text.toString().trim()
            val email = binding.editphoneoremail.text.toString().trim()
            val password = binding.editpassword.text.toString().trim()

            if (username.isBlank() || restaurant.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                createAccount(username, restaurant, email, password)
            }
        }

        // Already have account
        binding.alreadyhaveaccount.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Google Sign-In
        binding.Googlebutton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    // Manual Account Create
    private fun createAccount(username: String, restaurant: String, email: String, password: String) {
        auth.fetchSignInMethodsForEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val signInMethods = task.result?.signInMethods ?: emptyList<String>()
                if (signInMethods.isNotEmpty()) {
                    Toast.makeText(this, "Account already exists, please login", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { createTask ->
                            if (createTask.isSuccessful) {
                                val user = UserModel(username, restaurant, email, password)
                                val uid = auth.currentUser!!.uid
                                database.child("admins").child(uid).setValue(user)

                                Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show()
                                Log.e("Account", "CreateAccount: Failure", createTask.exception)
                            }
                        }
                }
            }
        }
    }

    // Google Sign-In result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val uid = user?.uid ?: ""

                // Check if this user already exists in DB
                database.child("admins").child(uid).get().addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        Toast.makeText(this, "Welcome Back!", Toast.LENGTH_SHORT).show()
                    } else {
                        val userMap = mapOf(
                            "username" to (user?.displayName ?: "Admin"),
                            "restaurantName" to "Not Set",
                            "email" to (user?.email ?: "")
                        )
                        database.child("admins").child(uid).setValue(userMap)
                        Toast.makeText(this, "Account Created Successfully", Toast.LENGTH_SHORT).show()
                    }

                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            } else {
                val ex = task.exception
                if (ex is FirebaseAuthUserCollisionException) {
                    Toast.makeText(this, "This email is already linked with another account", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Firebase Auth Failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
