package com.example.chattingapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.adapter.GroupDetailsAdapter
import com.example.chattingapp.databinding.ActivityGroupDetailsBinding
import com.example.chattingapp.model.Group
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class GroupDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupDetailsBinding
    private lateinit var adapter: GroupDetailsAdapter
    private lateinit var list: ArrayList<User>
    private var imageUrl: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.window.navigationBarColor = resources.getColor(R.color.fav)
        val uid = intent.getStringExtra("groupId")

        list = ArrayList()
        binding.groupMemberRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        adapter = GroupDetailsAdapter(this,list,uid!!)
        binding.groupMemberRecycler.adapter = adapter


        binding.profileImg.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 10)
        }
        binding.groupName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed in this example
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed in this example
            }

            override fun afterTextChanged(s: Editable?) {
                val input = s?.toString()?.trim()

                if (input.isNullOrEmpty()) {
                    binding.groupName.error = "Name cannot be empty"
                    binding.selected.isVisible = false
                } else {
                    binding.groupName.error = null
                    binding.selected.isVisible = true
                }
            }
        })

        binding.selected.setOnClickListener {
            if (binding.groupName.text.toString().isNotEmpty()) {
                Log.d("name of group",binding.groupName.text.toString())
                binding.selected.isVisible = false

                val user = FirebaseAuth.getInstance().currentUser?.uid
                val name = binding.groupName.text.toString()
                val uid = intent.getStringExtra("groupId")
                val database =
                    FirebaseDatabase.getInstance().reference.child("Groups").child("Data")
                        .child(uid!!)

                val groupMap = mutableMapOf<String, Any>()
                groupMap["groupId"] = uid!!
                groupMap["groupCreateId"] = user!!
                groupMap["groupName"] = name
                database.setValue(groupMap).addOnSuccessListener {
                    if (imageUrl != null) {
                        saveProfileImg(imageUrl!!)
                    } else {
                        val i = Intent(this, ChatViewActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        val database = FirebaseDatabase.getInstance().reference.child("Groups").child("GroupMembers")
                        database.child(uid).child(FirebaseAuth.getInstance().uid!!).setValue(true)
                        startActivity(i)
                        finish()
                        Toast.makeText(this, "Group Created", Toast.LENGTH_SHORT).show()
                    }
                }
            }else{
                Toast.makeText(this,"Group Name Required",Toast.LENGTH_SHORT).show()

            }
        }

        val mDbRef = FirebaseDatabase.getInstance().reference
        val mAuth = FirebaseAuth.getInstance()
        mDbRef.child("Groups").child("GroupMembers").child(uid!!).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val userTimestampMap = mutableMapOf<String, Boolean>()

                for (snap in snapshot.children) {
                    val userID = snap.key
                    val timestamp = snap.getValue(Boolean::class.java)
                    if (userID != null && timestamp != null) {
                        userTimestampMap[userID] = timestamp
                    }
                }

                // Sort the user IDs based on timestamps in descending order
                val sortedUserIDs = userTimestampMap.keys.sortedByDescending { userTimestampMap[it] }

                list.clear() // Clear the list before adding new users
                for (userID in sortedUserIDs) {
                    // Fetch user data using the user ID from the "user" node
                    mDbRef.child("user").child(userID).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(userDataSnapshot: DataSnapshot) {
                            val currentUser = userDataSnapshot.getValue(User::class.java)
                            if (currentUser != null && !list.contains(currentUser)) {
                                list.add(currentUser)

                                Log.d("listttttttttttt",currentUser.status.toString())

                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImageUri = data.data
            imageUrl = selectedImageUri!!
            val user = FirebaseAuth.getInstance().currentUser?.uid
            val name = binding.groupName.text.toString()
            val uid = intent.getStringExtra("groupId")
            val database = FirebaseDatabase.getInstance().reference.child("Groups").child("Data").child(uid!!)

            binding.profileImg.setImageURI(selectedImageUri)
            val data = Group(uid,user,name)
            database.setValue(data)
        }
    }
    private fun saveProfileImg(imageUri: Uri) {

        val uid = intent.getStringExtra("groupId")
        val firebaseStorage = FirebaseStorage.getInstance().reference
        val imageRef = firebaseStorage.child("Groupimage/${uid}")
        imageRef.putFile(imageUri).addOnSuccessListener { taskSnapshot ->

            imageRef.downloadUrl.addOnSuccessListener { uri ->
                FirebaseDatabase.getInstance().getReference("Groups").child("Data").child(uid!!).child("groupImage")
                    .setValue(uri.toString())
                    .addOnCompleteListener{
                        Toast.makeText(this,"Updated", Toast.LENGTH_SHORT).show()
                        val i = Intent(this,ChatViewActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        val database = FirebaseDatabase.getInstance().reference.child("Groups").child("GroupMembers")
                        database.child(uid).child(FirebaseAuth.getInstance().uid!!).setValue(true)
                        startActivity(i)
                        finish()
                    }
            }

        }
            .addOnFailureListener{ exception ->
                Toast.makeText(this,"Can't Uploaded", Toast.LENGTH_SHORT).show()
            }
    }

//    override fun onBackPressed() {
//        super.onBackPressed()
//        val databaseGroup = FirebaseDatabase.getInstance().reference.child("Groups").child("GroupMembers")
//
//        databaseGroup.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val groupId = intent.getStringExtra("groupId")
//
//                if(snapshot.child(groupId!!).exists()){
//                    databaseGroup.child(groupId).child(FirebaseAuth.getInstance().uid!!).removeValue()
//
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })
//    }
}