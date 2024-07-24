package com.example.chattingapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.chattingapp.databinding.ActivityProfileLogOutBinding
import com.example.chattingapp.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileLogOutActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityProfileLogOutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileLogOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        }
        this.window.navigationBarColor = resources.getColor(R.color.fav)


        mAuth = FirebaseAuth.getInstance()

        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid


        updateAge()

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            val window: Window = requireActivity().window
//            window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.login_back)
//        }

        // Initialize GoogleSignInOptions
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.gcm_defaultSenderId))
            .requestEmail()
            .build()


// Initialize GoogleSignInClient
        val googleSignInClient = GoogleSignIn.getClient(this, gso)


        setProfile()

        binding.profileImg.setOnClickListener{
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            i.type = "image/*"

            startActivityForResult(i, 85)
        }

        binding.buttonLogOut.setOnClickListener() {

            mAuth.signOut()


            if (this?.let { it1 -> GoogleSignIn.getLastSignedInAccount(it1) } != null) {
                googleSignInClient.signOut().addOnCompleteListener {

                    loggedOut()
                    val firebaseDef = FirebaseDatabase.getInstance().getReference("user").child(uid!!)
                    firebaseDef.child("status").setValue(false)
                    firebaseDef.child("token").removeValue()

                    val i = Intent(this, LoginActivity::class.java)
                    startActivity(i)

                }
            } else {

                loggedOut()
                val firebaseDef = FirebaseDatabase.getInstance().getReference("user").child(uid!!)
                firebaseDef.child("status").setValue(false)
                firebaseDef.child("token").removeValue()

                val i = Intent(this, LoginActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)

            }
        }

        followCount()



        binding.imageDiagonal.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            i.type = "image/*"

            startActivityForResult(i, 86)
        }

        binding.bioEdit.setOnClickListener {

            val bioEditText = EditText(this)
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(20, 20, 20, 20)
            bioEditText.layoutParams = layoutParams
            val alertDialog = AlertDialog.Builder(this)
                .setTitle("Edit Bio")
                .setView(bioEditText)
                .setPositiveButton("OK") { dialog, which ->
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    val firebaseRef =
                        FirebaseDatabase.getInstance().getReference("user").child(uid!!)
                    val bio = bioEditText.text.toString()

                    val update = mapOf<String, Any?>(
                        "bio" to bio
                    )
                    firebaseRef.updateChildren(update).addOnSuccessListener {
                        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                        binding.etBio.text = bio

                    }
                        .addOnFailureListener {
                            Toast.makeText(this, "Can't Update", Toast.LENGTH_SHORT).show()
                        }
                }
                .setNegativeButton("Cancel"){ dialog,which ->
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()
        }

        binding.phoneEdit.setOnClickListener{

            val phoneEditText = EditText(this)
            phoneEditText.inputType = InputType.TYPE_CLASS_NUMBER

            val alertDialog = AlertDialog.Builder(this)
                .setView(phoneEditText)
                .setTitle("Enter Phone Number")
                .setPositiveButton("Add"){dialog,which->
                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    val firebaseRef = FirebaseDatabase.getInstance().getReference("user").child(uid!!)

                    val phone = phoneEditText.text.toString()

                    val update = mapOf<String, Any>(
                        "phoneNo" to phone
                    )

                    firebaseRef.updateChildren(update).addOnSuccessListener {
                        Toast.makeText(this,"Added", Toast.LENGTH_SHORT).show()
                        binding.etPhoneNo.text = phone
                    }
                        .addOnFailureListener{
                            Toast.makeText(this,"Can't Update", Toast.LENGTH_SHORT).show()
                        }

                }
                .setNegativeButton("Cancel"){dialog, which->
                    dialog.dismiss()
                }
                .create()
            alertDialog.show()
        }

        binding.ageEdit.setOnClickListener {

            showDatePickerDialog()

//            val ageEditText = EditText(this)
//            ageEditText.inputType = InputType.TYPE_CLASS_NUMBER
//            val uid = FirebaseAuth.getInstance().currentUser?.uid
//            val firebaseRef = FirebaseDatabase.getInstance().getReference("user").child(uid!!)
//
//            val alertDialog = AlertDialog.Builder(this)
//                .setView(ageEditText)
//                .setTitle("Edit Age")
//                .setPositiveButton("Add"){dialog, which->
//                    val age = Integer.parseInt(ageEditText.text.toString())
//                    val update = mapOf<String, Any>(
//                        "age" to age
//                    )
//                    firebaseRef.updateChildren(update).addOnSuccessListener {
//                        Toast.makeText(this,"Added", Toast.LENGTH_SHORT).show()
//                        binding.etAge.text = age.toString()
//                    }
//                        .addOnFailureListener{
//                            Toast.makeText(this, "Can't Update", Toast.LENGTH_SHORT).show()
//                        }
//                }
//                .setNegativeButton("Cancel"){dialog, which->
//                    dialog.dismiss()
//                }
//                .create()
//            alertDialog.show()
        }

    }

    private fun updateAge() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firebaseRef = FirebaseDatabase.getInstance().getReference("user").child(uid!!)

        // Fetch the saved birthday from Firebase
        // Assuming you have a field named "birthday" in your database
        firebaseRef.child("birthday").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val birthdayString = snapshot.getValue(String::class.java)
                    val birthdayDate =
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(birthdayString)

                    val currentDate = Calendar.getInstance()
                    val birthDate = Calendar.getInstance()
                    birthDate.time = birthdayDate

                    var age = currentDate.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR)

                    if (currentDate.get(Calendar.MONTH) < birthDate.get(Calendar.MONTH) ||
                        (currentDate.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH) &&
                                currentDate.get(Calendar.DAY_OF_MONTH) < birthDate.get(Calendar.DAY_OF_MONTH))) {
                        age--
                    }

                    Log.d("Age", age.toString()) // Check the calculated age in the logs

                    Log.d("dataaaa",age.toString())
                    Log.d("dataaaa1",birthdayDate.toString())
                    Log.d("dataaaa2",birthdayDate.year.toString())
                    Log.d("dataaaa3",birthdayString.toString())
                    // Update age in Firebase
                    firebaseRef.child("age").setValue(age)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(Calendar.YEAR, year)
                selectedDate.set(Calendar.MONTH, month)
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val currentDate = Calendar.getInstance()
                var age = currentDate.get(Calendar.YEAR) - selectedDate.get(Calendar.YEAR)

                // Adjust age based on month and day
                if (currentDate.get(Calendar.MONTH) < selectedDate.get(Calendar.MONTH) ||
                    (currentDate.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) &&
                            currentDate.get(Calendar.DAY_OF_MONTH) < selectedDate.get(Calendar.DAY_OF_MONTH))
                ) {
                    age--
                }

                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(selectedDate.time)

                binding.ageEdit.setText(formattedDate)

                val uid = FirebaseAuth.getInstance().currentUser?.uid
                val firebaseRef = FirebaseDatabase.getInstance().getReference("user").child(uid!!)

                val update = mapOf<String, Any>(
                    "age" to age,
                    "birthday" to formattedDate // Save the birthday as well
                )
                firebaseRef.updateChildren(update).addOnSuccessListener {
                    Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
                    // Update the UI with age and formattedDate
                    binding.etAge.text = age.toString()

                }.addOnFailureListener {
                        Toast.makeText(this, "Can't Update", Toast.LENGTH_SHORT).show()
                    }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }


    private fun followCount() {
        val database = FirebaseDatabase.getInstance().reference.child("Followers")
        val logInUid = FirebaseAuth.getInstance().currentUser?.uid
        database.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val likeCountFirebase = snapshot.child(logInUid!!).childrenCount
                binding.followerCount.text = likeCountFirebase.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        val database1 = FirebaseDatabase.getInstance().reference.child("Following")
        database1.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val likeCountFirebase = snapshot.child(logInUid!!).childrenCount
                binding.followingCount.text = likeCountFirebase.toString()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setProfile() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val database = FirebaseDatabase.getInstance().reference.child("user").child(uid)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                binding.loading.isVisible =false
                binding.loading1.isVisible =false
                if (user != null) {
                    val name = user.name
                    val age = user.age
                    val phone = user.phoneNo
                    val profileImage = user.profileImageUrl
                    val email = user.email
                    val city = user.city
                    val bio = user.bio
                    var diagonalImage = user.backgroundDiagonal

                    binding.tvName.text = name
                    binding.etAge.text = age.toString()
                    binding.etPhoneNo.text = phone
                    binding.etEmail.text = email
                    binding.tvAddress.text = city
                    binding.etBio.text = bio
                    Glide.with(this@ProfileLogOutActivity)
                        .load(profileImage)
                        .placeholder(R.drawable.person)
                        .into(binding.profileImg)
                    Glide.with(this@ProfileLogOutActivity).load(diagonalImage).placeholder(R.drawable.placeholder_image).into(binding.imageDiagonal)

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", databaseError.toException())
                binding.loading.isVisible =true
                binding.loading1.isVisible =true




            }
        })
    }

    private fun saveProfileImg(imageUri: Uri) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firebaseStorage = FirebaseStorage.getInstance().reference
        val imageRef = firebaseStorage.child("image/${uid}")
        imageRef.putFile(imageUri).addOnSuccessListener { taskSnapshot ->

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                FirebaseDatabase.getInstance().getReference("user").child(uid!!).child("profileImageUrl")
                    .setValue(uri.toString())
                    .addOnCompleteListener{
                        Toast.makeText(this,"Updated", Toast.LENGTH_SHORT).show()
                    }
            }

        }
            .addOnFailureListener{ exception ->
                Toast.makeText(this,"Can't Uploaded",Toast.LENGTH_SHORT).show()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 85 && resultCode == RESULT_OK &&  data != null) {

            val imageUri: Uri? = data?.data

            if (imageUri != null) {
                binding.profileImg.setImageURI(imageUri)

                saveProfileImg(imageUri)
            }
        }
        if (requestCode == 86 && resultCode == RESULT_OK &&  data != null) {

            val imageUri: Uri? = data?.data

            if (imageUri != null) {
                binding.imageDiagonal.setImageURI(imageUri)

                saveDiagonalImage(imageUri)
            }
        }
    }

    private fun saveDiagonalImage(imageUri: Uri) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val firebaseStorage = FirebaseStorage.getInstance().reference
        val imageRef = firebaseStorage.child("imageDiagonal/${uid}")
        imageRef.putFile(imageUri).addOnSuccessListener { taskSnapshot ->

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                FirebaseDatabase.getInstance().getReference("user").child(uid!!).child("backgroundDiagonal")
                    .setValue(uri.toString())
                    .addOnCompleteListener{
                        Toast.makeText(this,"Updated", Toast.LENGTH_SHORT).show()
                    }
            }

        }
            .addOnFailureListener{ exception ->
                Toast.makeText(this,"Can't Uploaded",Toast.LENGTH_SHORT).show()
            }
    }

    private fun loggedOut() {
        val sharedPref = this.getSharedPreferences("CHECK", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.clear()
        editor.apply()
    }

    override fun onResume() {
        super.onResume()
//        val toolbar = requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
//        toolbar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.status_bar_color))
        updateUserStatus(true)

    }
    override fun onPause() {
        super.onPause()
//        val toolbar =
//            requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
//        toolbar.setBackgroundColor(
//            ContextCompat.getColor(
//                requireContext(),
//                R.color.status_bar_color
//            )
//        )
        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid
        if (uid != null) {
            val firebaseDef = FirebaseDatabase.getInstance().getReference("user").child(uid!!)
            firebaseDef.child("status").setValue(false)

        }
    }
    private fun updateUserStatus(status: Boolean) {
        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid
        val firebaseDef = FirebaseDatabase.getInstance().getReference("user").child(uid!!)
        firebaseDef.child("status").setValue(status)
    }
}
