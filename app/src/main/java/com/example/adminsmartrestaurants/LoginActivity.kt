package com.example.adminsmartrestaurants

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.adminsmartrestaurants.databinding.ActivityLoginBinding
import com.example.adminsmartrestaurants.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var email: String
    private lateinit var password: String
    private var username: String? = null
    private var nameOfRestaurant: String? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Manual Login
        binding.loginbutton.setOnClickListener {
            email = binding.email.text.toString().trim()
            password = binding.password.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
            } else {
                loginWithEmail(email, password)
            }
        }

        // Google Login
        binding.Googlebutton.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }

        // Navigate to SignUp
        binding.donthaveaccount.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
    }

    // Manual Login + Fallback to SignUp
    private fun loginWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                updatedUi(auth.currentUser)
            } else {
                // Agar user exist nahi hai -> new account create karna
                auth.fetchSignInMethodsForEmail(email).addOnSuccessListener { methods ->
                    if (methods.signInMethods?.isNotEmpty() == true) {
                        Toast.makeText(this, "Wrong password, try again", Toast.LENGTH_SHORT).show()
                    } else {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { createTask ->
                                if (createTask.isSuccessful) {
                                    saveUserData()
                                    Toast.makeText(this, "Account Created & Logged In", Toast.LENGTH_SHORT).show()
                                    updatedUi(auth.currentUser)
                                } else {
                                    Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show()
                                    Log.e("Account", "createUserAccount: Failed", createTask.exception)
                                }
                            }
                    }
                }
            }
        }
    }

    private fun saveUserData() {
        email = binding.email.text.toString().trim()
        password = binding.password.text.toString().trim()

        val user = UserModel(
            email = email,
            password = password,
            username = username ?: "Admin",
            nameOfRestaurant = nameOfRestaurant ?: "Not Set"
        )
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            database.child("admins").child(it).setValue(user)
        }
    }

    // Auto-login if already logged in
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun updatedUi(user: FirebaseUser?) {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // Google Sign-In Launcher
    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account: GoogleSignInAccount = task.result
                firebaseAuthWithGoogle(account)
            } catch (e: Exception) {
                Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    // Firebase Auth with Google
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                val uid = user?.uid ?: ""

                // Check if already in DB
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
                    updatedUi(user)
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
