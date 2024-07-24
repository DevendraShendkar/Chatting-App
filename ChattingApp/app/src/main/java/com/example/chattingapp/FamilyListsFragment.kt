package com.example.chattingapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.chattingapp.adapter.RecyclearUserAdapter
import com.example.chattingapp.databinding.FragmentFamiliyListsBinding
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FamilyListsFragment : Fragment() {
    private lateinit var binding: FragmentFamiliyListsBinding
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: RecyclearUserAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFamiliyListsBinding.inflate(layoutInflater)

        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)

        mDbRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()

        val i = Intent()
        val senderRoom = i.getStringExtra("receiverUid")
        userList = ArrayList()
        Log.d("idididiididid",senderRoom.toString())
        binding.recycler.layoutManager = LinearLayoutManager(context)
        adapter = RecyclearUserAdapter(requireContext(), userList, senderRoom.toString())
        binding.recycler.adapter = adapter

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

                    // Sort the user IDs based on timestamps in descending order
                    val sortedUserIDs =
                        userTimestampMap.keys.sortedByDescending { userTimestampMap[it] }

                    userList.clear() // Clear the list before adding new users
                    for (userID in sortedUserIDs) {
                        mDbRef.child("user").child(userID)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userDataSnapshot: DataSnapshot) {
                                    val currentUser = userDataSnapshot.getValue(User::class.java)
                                    if (currentUser != null) {
                                        mDbRef.child("Family").child("FamilyMembers")
                                            .child(mAuth.currentUser?.uid!!)
                                            .addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    if (snapshot.hasChild(currentUser.uid!!)) {
                                                        userList.add(currentUser)
                                                        adapter.notifyDataSetChanged()
                                                        binding.cuteDogLootie.isVisible = false
                                                        binding.noGroupText.isVisible = false
                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    // Handle error
                                                }
                                            })
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Handle error
                                }
                            })
                    }
                }


                override fun onCancelled(error: DatabaseError) {
                }
            })

        if(userList.isEmpty()){
            binding.cuteDogLootie.isVisible = true
            binding.noGroupText.isVisible = true
            binding.noGroupText.setText("No Family Added")
        }else{
            binding.cuteDogLootie.isVisible = false
            binding.noGroupText.isVisible = false
        }

        return binding.root
    }

}