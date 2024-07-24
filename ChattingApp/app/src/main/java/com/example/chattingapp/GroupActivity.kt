package com.example.chattingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chattingapp.adapter.GroupAdapter
import com.example.chattingapp.adapter.GroupDetailsAdapter
import com.example.chattingapp.databinding.ActivityGroupBinding
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: GroupAdapter
    private lateinit var friendsList: ArrayList<User>
    private lateinit var binding: ActivityGroupBinding
    private lateinit var adapter1: GroupDetailsAdapter
    private lateinit var list: ArrayList<User>
    @SuppressLint("MissingInflatedId", "SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.window.navigationBarColor = resources.getColor(R.color.fav)

        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = GridLayoutManager(this,2)
        friendsList =ArrayList()
        val groupId = intent.getStringExtra("groupId")
        adapter = GroupAdapter(this,friendsList,groupId!!)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        list = ArrayList()
        binding.participantRecycler.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        adapter1 = GroupDetailsAdapter(this,list,groupId)
        binding.participantRecycler.adapter = adapter1

        setParticipants()
        memberCount()
        binding.selected.setOnClickListener {
            val database = FirebaseDatabase.getInstance().reference.child("Groups").child("GroupMembers")
                database.child(groupId!!).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            var count = snapshot.child(groupId).childrenCount
                            if (count.equals(0)){
                                Toast.makeText(applicationContext,"At least one member must be added",Toast.LENGTH_SHORT).show()
                            }else{
                                val i = Intent(applicationContext,GroupDetailsActivity::class.java)
                                i.putExtra("groupId",groupId)
                                startActivity(i)
                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })

        }
        val mDbRef = FirebaseDatabase.getInstance().getReference()
        mDbRef.child("list").child(auth.uid.toString()).addListenerForSingleValueEvent(object :
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

                // Sort the user IDs based on timestamps in descending order
                val sortedUserIDs =
                    userTimestampMap.keys.sortedByDescending { userTimestampMap[it] }

                friendsList.clear() // Clear the list before adding new users
                for (userID in sortedUserIDs) {
                    // Fetch user data using the user ID from the "user" node
                    mDbRef.child("user").child(userID).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(userDataSnapshot: DataSnapshot) {
                            val currentUser = userDataSnapshot.getValue(User::class.java)
                            if (currentUser != null && !friendsList.contains(currentUser)) {
                                friendsList.add(currentUser)

                                Log.d("listttttttttttt", currentUser.status.toString())

                            }
                            adapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setParticipants() {
        val mDbRef = FirebaseDatabase.getInstance().reference
        val mAuth = FirebaseAuth.getInstance()
        val uid = intent.getStringExtra("groupId")
        mDbRef.child("Groups").child("GroupMembers").child(uid!!).addValueEventListener(object :
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
                    mDbRef.child("user").child(userID).addValueEventListener(object :
                        ValueEventListener {
                        override fun onDataChange(userDataSnapshot: DataSnapshot) {
                            val currentUser = userDataSnapshot.getValue(User::class.java)
                            if (currentUser != null && !list.contains(currentUser)) {
                                list.add(currentUser)

                                Log.d("listttttttttttt",currentUser.status.toString())

                            }else list.remove(currentUser)
                            adapter1.notifyDataSetChanged()
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

    override fun onResume() {
        super.onResume()
        updateUserStatus(true)
    }

    override fun onPause() {
        super.onPause()
        updateUserStatus(false)
    }
    private fun memberCount() {
        val database =
            FirebaseDatabase.getInstance().reference.child("Groups").child("GroupMembers")
        val groupId = intent.getStringExtra("groupId")!!
        val uid = FirebaseAuth.getInstance().uid
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(groupId).hasChild(uid!!)) {
                    val likeCountFirebase = snapshot.child(groupId).childrenCount
                    if(friendsList.size == 0){
                        binding.memberCount.text = likeCountFirebase.toString() + " Selected"

                    }else  binding.memberCount.text = likeCountFirebase.toString() + " of " +friendsList.size + " Selected"
                    binding.participantRecycler.isVisible =
                        Integer.parseInt(likeCountFirebase.toString()) != 0
                } else {
                    val likeCountFirebase = snapshot.child(groupId).childrenCount
                    if(friendsList.size == 0){
                        binding.memberCount.text = likeCountFirebase.toString() + " Selected"

                    }else  binding.memberCount.text = likeCountFirebase.toString() + " of " +friendsList.size + " Selected"
                    binding.participantRecycler.isVisible =
                        Integer.parseInt(likeCountFirebase.toString()) != 0
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onBackPressed() {
        super.onBackPressed()
        val databaseGroup = FirebaseDatabase.getInstance().reference.child("Groups").child("GroupMembers")
        val databaseGroupData = FirebaseDatabase.getInstance().reference.child("Groups").child("Data")

            databaseGroup.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val groupId = intent.getStringExtra("groupId")

                    if(snapshot.child(groupId!!).exists()){
                            databaseGroup.child(groupId).removeValue()

                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        databaseGroupData.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val groupId = intent.getStringExtra("groupId")

                if(snapshot.child(groupId!!).exists()){
                    databaseGroupData.child(groupId).removeValue()

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
    private fun updateUserStatus(status: Boolean) {
        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid
        val firebaseDef = FirebaseDatabase.getInstance().getReference("user/$uid")
        firebaseDef.child("status").setValue(status)
    }
}