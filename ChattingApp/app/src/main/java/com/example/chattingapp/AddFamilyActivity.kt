package com.example.chattingapp

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.adapter.AddFamilyAdapter
import com.example.chattingapp.databinding.ActivityAddFamilyBinding
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddFamilyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddFamilyBinding
    private lateinit var listAdapter: AddFamilyAdapter
    private lateinit var list: ArrayList<User>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFamilyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.window.navigationBarColor = resources.getColor(R.color.fav)

        list = ArrayList()
        val i = Intent()
        val uid = i.getStringExtra("uid")
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        listAdapter = AddFamilyAdapter(this,list)
        binding.recyclerView.adapter = listAdapter


        mDbRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()
        mDbRef.child("list").child(mAuth.uid.toString())
            .addValueEventListener(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val userTimestampMap = mutableMapOf<String, Long>()

                    for (snap in snapshot.children) {
                        val userID = snap.key
                        val timestamp = snap.getValue(Long::class.java)
                        if (userID != null && timestamp != null) {
                            userTimestampMap[userID] = timestamp
                        }
                    }

                    val sortedUserIDs =
                        userTimestampMap.keys.sortedByDescending { userTimestampMap[it] }

                    list.clear() // Clear the list before adding new users
                    for (userID in sortedUserIDs) {
                        // Fetch user data using the user ID from the "user" node
                        mDbRef.child("user").child(userID)
                            .addValueEventListener(object :
                                ValueEventListener {
                                override fun onDataChange(userDataSnapshot: DataSnapshot) {
                                    val currentUser =
                                        userDataSnapshot.getValue(User::class.java)
                                    if (currentUser != null && !list.contains(
                                            currentUser
                                        )
                                    ) {
                                        list.add(currentUser)

                                        Log.d(
                                            "listttttttttttt",
                                            currentUser.status.toString()
                                        )

                                    }
                                    listAdapter.notifyDataSetChanged()
                                }

                                override fun onCancelled(error: DatabaseError) {
                                }
                            })
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })


        binding.selected.setOnClickListener {
            val i = Intent(this,ChatViewActivity::class.java)
            val senderRoom = i.getStringExtra("uid")
            i.putExtra("receiverUid",senderRoom)
            startActivity(i)
            finish()
        }


    }
}