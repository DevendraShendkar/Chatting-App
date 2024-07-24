package com.example.chattingapp

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chattingapp.adapter.SearchAdapter
import com.example.chattingapp.databinding.ActivitySearchBinding
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity() {
    private  lateinit var binding:ActivitySearchBinding
    private lateinit var adapter: SearchAdapter
    private lateinit var userList: MutableList<User>
    private lateinit var databaseRef: DatabaseReference
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
        }
        databaseRef = FirebaseDatabase.getInstance().getReference("user")
        userList = mutableListOf()
        mDbRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()


        adapter = SearchAdapter(this, userList)
        binding.searchRecyclearView.layoutManager = LinearLayoutManager(this)
        binding.searchRecyclearView.adapter = adapter

        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for (postSnapShot in snapshot.children) {
                    binding.loading.isVisible = false
                    val currentUser = postSnapShot.getValue(User::class.java)
                    if (mAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                binding.loading.isVisible = true
            }
        })

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterResults(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }
    private fun filterResults(query: String) {

        userList.clear()
        adapter.notifyDataSetChanged()

        val nameQuery = databaseRef.orderByChild("name")
            .startAt(query)
            .endAt(query + "\uf8ff")

        val combinedQuery = FirebaseDatabase.getInstance().getReference("user")
            .orderByChild("name")
            .startAt(query)
            .endAt(query + "\uf8ff")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    user?.let {
                        userList.add(it)
                        adapter.notifyItemInserted(userList.size - 1)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        }

        if (nameQuery != null) {
            nameQuery.addListenerForSingleValueEvent(listener)
        } else {
            combinedQuery.addListenerForSingleValueEvent(listener)
        }
    }
}