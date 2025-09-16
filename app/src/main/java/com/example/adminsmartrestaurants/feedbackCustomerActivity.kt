package com.example.adminsmartrestaurants

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class FeedbackCustomerActivity : AppCompatActivity() {

    // UI Components
    private lateinit var backButton: ImageView
    private lateinit var editFeedback: TextInputEditText
    private lateinit var btnSubmitFeedback: AppCompatButton

    // Firebase
    private lateinit var firestore: FirebaseFirestore

    // CustomerId → jisko feedback dena hai (Intent se aayega)
    private var customerId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback_customer)

        // Firebase init
        firestore = FirebaseFirestore.getInstance()

        // Intent se customerId le lo
        customerId = intent.getStringExtra("CUSTOMER_ID")

        // Initialize UI
        initializeViews()

        // Click listeners
        setupClickListeners()
    }

    private fun initializeViews() {
        backButton = findViewById(R.id.backbutton)
        editFeedback = findViewById(R.id.editFeedback)
        btnSubmitFeedback = findViewById(R.id.btnSubmitFeedback)
    }

    private fun setupClickListeners() {
        // Back button
        backButton.setOnClickListener {
            finish()
        }

        // Submit button
        btnSubmitFeedback.setOnClickListener {
            submitFeedback()
        }
    }

    private fun submitFeedback() {
        val feedbackText = editFeedback.text.toString().trim()

        // Validation
        if (feedbackText.isEmpty()) {
            editFeedback.error = "Please write feedback"
            editFeedback.requestFocus()
            return
        }
        if (customerId == null) {
            Toast.makeText(this, "Customer not found!", Toast.LENGTH_SHORT).show()
            return
        }

        // Create feedback data
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val formattedDate = dateFormat.format(Date(currentTime))

        val feedbackData = mapOf(
            "customerId" to customerId,
            "feedback" to feedbackText,
            "adminId" to "ADMIN_123", // Optional: yaha admin ka UID dalna hai agar chahiye
            "timestamp" to currentTime,
            "date" to formattedDate
        )

        // Save to Firebase
        btnSubmitFeedback.isEnabled = false
        btnSubmitFeedback.text = "Submitting..."

        firestore.collection("admin_feedback")
            .add(feedbackData)
            .addOnSuccessListener {
                Toast.makeText(this, "Feedback submitted!", Toast.LENGTH_LONG).show()
                clearForm()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
            .addOnCompleteListener {
                btnSubmitFeedback.isEnabled = true
                btnSubmitFeedback.text = "Submit Feedback"
            }
    }

    private fun clearForm() {
        editFeedback.setText("")
        editFeedback.error = null
    }
}
