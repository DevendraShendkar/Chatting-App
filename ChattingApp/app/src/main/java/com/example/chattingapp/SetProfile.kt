package com.example.chattingapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.chattingapp.databinding.ActivitySetProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class SetProfile : AppCompatActivity() {

    private lateinit var binding: ActivitySetProfileBinding

    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.window.navigationBarColor = resources.getColor(R.color.fav)

        binding.profileImg.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            i.type = "image/*"
            startActivityForResult(i, 35)
        }

        binding.setUpBtn.setOnClickListener {
            saveProfile()
            val i = Intent(this, ChatViewActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            finish()

        }
    }

    private fun saveProfile() {
        try {
            val name = binding.name.text.toString()
            val bio = binding.bio.text.toString()
            val mAuth = FirebaseAuth.getInstance()
            val uid = mAuth.currentUser?.uid

            val update = mapOf<String, Any>(
                "name" to name.toLowerCase(),
                "bio" to bio,

            )

            val mDataRef = FirebaseDatabase.getInstance().reference

            mDataRef.child("user").child(uid!!).updateChildren(update).addOnSuccessListener {
                Toast.makeText(this, "Saved",Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener{
                    Toast.makeText(this, "Can't Upload", Toast.LENGTH_SHORT).show()
                }

        }catch (e: Exception){
            Toast.makeText(this,"Can't Upload",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 35 && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data

            if (imageUri != null) {
                binding.profileImg.setImageURI(imageUri)
                saveProfileImg(imageUri)
            }
        }
    }

    private fun saveProfileImg(imageUri: Uri) {

        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("image/${FirebaseAuth.getInstance().currentUser?.uid}")
        val mDbRef = FirebaseDatabase.getInstance().reference
        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->

                imageRef.downloadUrl.addOnSuccessListener { uri ->

                    mDbRef.child("user").child(FirebaseAuth.getInstance().currentUser?.uid ?: "")
                        .child("profileImageUrl").setValue(uri.toString())
                        .addOnSuccessListener {

                        }
                }
            }
            .addOnFailureListener { exception ->
                // Handle the error
                Toast.makeText(applicationContext,"Can't Uploaded",Toast.LENGTH_SHORT).show()
            }
    }
}
