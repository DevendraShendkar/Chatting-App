package com.example.chattingapp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.adapter.GroupViewAdapter
import com.example.chattingapp.databinding.FragmentGroupListBinding
import com.example.chattingapp.model.Group
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class GroupListFragment : Fragment() {
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentGroupListBinding
    private lateinit var groupList: ArrayList<Group>
    private lateinit var adapterGroup: GroupViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGroupListBinding.inflate(layoutInflater,container,false)

        mDbRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()

        groupList = ArrayList()
        binding.recycler.layoutManager = LinearLayoutManager(context)
        adapterGroup = GroupViewAdapter(requireContext(), groupList)
        binding.recycler.adapter = adapterGroup

        mDbRef.child("Groups").child("GroupMembers")
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userGroups = mutableListOf<String>()

                            val userUid = FirebaseAuth.getInstance().currentUser?.uid

                            for (groupSnapshot in snapshot.children) {
                                val groupID = groupSnapshot.key
                                val groupMemberList = groupSnapshot.children.map { it.key }.toSet()

                                Log.d("listishereagain", groupMemberList.toString())
                                if (groupID != null && userUid != null) {
                                    for (user in groupMemberList) {
                                        if (userUid == user) {
                                            userGroups.add(groupID)
                                            Log.d("listishereagain", userUid)
                                        }
                                    }
                                }
                            }

                            Log.d("listishereagain", userGroups.toString())
                            groupList.clear() // Clear the list before adding new users
                            for (userID in userGroups) {
                                // Fetch user data using the user ID from the "user" node
                                mDbRef.child("Groups").child("Data").child(userID)
                                    .addValueEventListener(object :
                                        ValueEventListener {
                                        override fun onDataChange(userDataSnapshot: DataSnapshot) {
                                            val currentUser =
                                                userDataSnapshot.getValue(Group::class.java)
                                            if (currentUser != null) {
                                                groupList.add(currentUser)

                                                Log.d(
                                                    "listttttttttttt",
                                                    currentUser.groupId.toString()
                                                )

                                            }
                                            binding.cuteDogLootie.isVisible = false
                                            binding.noGroupText.isVisible = false
                                            adapterGroup.notifyDataSetChanged()
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                        }
                                    })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle the error if needed
                        }
                    })

        if(groupList.isEmpty()){
            binding.cuteDogLootie.isVisible = true
            binding.noGroupText.isVisible = true
            binding.noGroupText.setText("No Group Added")
        }else{
            binding.cuteDogLootie.isVisible = false
            binding.noGroupText.isVisible = false
        }


        return binding.root
    }
}