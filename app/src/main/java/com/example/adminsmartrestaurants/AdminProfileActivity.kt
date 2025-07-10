package com.example.adminsmartrestaurants

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.adminsmartrestaurants.databinding.ActivityAdminProfileBinding

class AdminProfileActivity : AppCompatActivity() {

    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }

    private var isEditable = false
    private lateinit var galleryResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Disable EditTexts initially
        setEditFieldsEnabled(false)

        // Register the gallery result launcher
        galleryResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedImageUri: Uri? = result.data?.data
                selectedImageUri?.let {
                    binding.profileImage.setImageURI(it)
                }
            }
        }

        // Toggle enable/disable on edit icon click
        binding.editiconbutton.setOnClickListener {
            isEditable = !isEditable
            setEditFieldsEnabled(isEditable)
        }

        // Open gallery when profile image is clicked
        binding.profileImage.setOnClickListener {
            openGallery()
        }
        binding.backbutton.setOnClickListener{
            finish()
        }
    }

    private fun setEditFieldsEnabled(enabled: Boolean) {
        binding.namedit.isEnabled = enabled
        binding.namedit.isFocusableInTouchMode = enabled

        binding.addressedit.isEnabled = enabled
        binding.addressedit.isFocusableInTouchMode = enabled

        binding.emailedit.isEnabled = enabled
        binding.emailedit.isFocusableInTouchMode = enabled

        binding.phoneedit.isEnabled = enabled
        binding.phoneedit.isFocusableInTouchMode = enabled

        binding.passwordedit.isEnabled = enabled
        binding.passwordedit.isFocusableInTouchMode = enabled
        if (isEditable){
            binding.namedit.requestFocus()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryResultLauncher.launch(intent)
    }
}
