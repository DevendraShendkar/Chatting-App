package com.example.chattingapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.chattingapp.adapter.ChatViewRecycler
import com.example.chattingapp.adapter.ExerciseChallengeAdapter
import com.example.chattingapp.adapter.RecyclearUserAdapter
import com.example.chattingapp.chatGPT.ChatGPT
import com.example.chattingapp.databinding.ActivityChatViewBinding
import com.example.chattingapp.model.Group
import com.example.chattingapp.model.User
import com.example.chattingapp.onBoarding.ViewPagerAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatViewActivity : AppCompatActivity() {
    private lateinit var userList: ArrayList<User>
    private lateinit var onlineList: ArrayList<User>
    private lateinit var adapter: RecyclearUserAdapter
    private lateinit var adapter1: ChatViewRecycler
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: ActivityChatViewBinding
    private var firstTimeBoolean: Boolean? = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.window.navigationBarColor = resources.getColor(R.color.fav)

        mDbRef = FirebaseDatabase.getInstance().getReference()
        mAuth = FirebaseAuth.getInstance()

        val permissionManager = PermissionManager()
        permissionManager.requestAllPermissions(this)

        userList = ArrayList()
        onlineList = ArrayList()

        binding.recyclerOnline.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val i = Intent()
        val senderRoom = i.getStringExtra("receiverUid")
        adapter1 = ChatViewRecycler(this, onlineList)

        binding.recyclerOnline.adapter = adapter1

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("ResourceAsColor")
            override fun onPageSelected(position: Int) {
                // Change the background based on the current fragment
                when (position) {
                    0 -> {
                        binding.allChat.setBackgroundResource(R.drawable.button_bg1)
                        binding.group.setBackgroundResource(R.drawable.edittext_white)
                        binding.familyChat.setBackgroundResource(R.drawable.edittext_white)
                        binding.allChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.fav))
                        binding.group.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))
                        binding.familyChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))
                    }
                    1 -> {
                        binding.group.setBackgroundResource(R.drawable.button_bg1)
                        binding.familyChat.setBackgroundResource(R.drawable.edittext_white)
                        binding.allChat.setBackgroundResource(R.drawable.edittext_white)
                        binding.group.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.fav))
                        binding.familyChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))
                        binding.allChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))

                    }
                    2 ->{
                        binding.allChat.setBackgroundResource(R.drawable.edittext_white)
                        binding.group.setBackgroundResource(R.drawable.edittext_white)
                        binding.familyChat.setBackgroundResource(R.drawable.button_bg1)
                        binding.familyChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.fav))
                        binding.allChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))
                        binding.group.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))

                    }
                    // Add more cases if needed for other fragments
                }
            }
        })
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // Change the background based on the current fragment
                when (position) {
                    0 -> {
                        // Update backgrounds and text colors here
                        binding.allChat.setBackgroundResource(R.drawable.button_bg1)
                        binding.group.setBackgroundResource(R.drawable.edittext_white)
                        binding.familyChat.setBackgroundResource(R.drawable.edittext_white)
                        binding.allChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.fav))
                        binding.group.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))
                        binding.familyChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))
                    }
                    1 -> {
                        // Update backgrounds and text colors here
                        binding.group.setBackgroundResource(R.drawable.button_bg1)
                        binding.familyChat.setBackgroundResource(R.drawable.edittext_white)
                        binding.allChat.setBackgroundResource(R.drawable.edittext_white)
                        binding.group.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.fav))
                        binding.familyChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))
                        binding.allChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))
                    }
                    2 ->{
                        binding.allChat.setBackgroundResource(R.drawable.edittext_white)
                        binding.group.setBackgroundResource(R.drawable.edittext_white)
                        binding.familyChat.setBackgroundResource(R.drawable.button_bg1)
                        binding.familyChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.fav))
                        binding.allChat.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))
                        binding.group.setTextColor(ContextCompat.getColor(this@ChatViewActivity, R.color.favBlack))
                    }
                    // Add more cases if needed for other fragments
                }
            }
        })

        // Set click listeners for the TextViews (EditTexts)
        binding.allChat.setOnClickListener {
            // Navigate to the first fragment when clicked
            binding.viewPager.currentItem = 0
        }

        binding.group.setOnClickListener {
            // Navigate to the second fragment when clicked
            binding.viewPager.currentItem = 1
        }
        binding.familyChat.setOnClickListener {
            binding.viewPager.currentItem = 2
        }

        // ... (other code)


        binding.back.setOnClickListener {
            startActivity(Intent(this,ChatGPT::class.java))
        }

        val fragmentList = arrayListOf<Fragment>(
            AllChatListFragment(),
            GroupListFragment(),
            FamilyListsFragment()

        )
        val adapterView = ViewPagerAdapter(
            fragmentList,
            this.supportFragmentManager,
            lifecycle
        )
        binding.viewPager.adapter =adapterView
        ExerciseChallengeAdapter(fragmentList,this.supportFragmentManager,lifecycle)

        binding.search.setOnClickListener {
            startActivity(Intent(this,SearchActivity::class.java))
        }

        if(onlineList.isEmpty()){
            binding.noOnlineText.isVisible = true
            binding.noOnlineText.setText("No one Online")

        }else{
            binding.noOnlineText.isVisible = false
            binding.noOnlineText.setText("")
        }
        if (firstTimeBoolean!!) {

            adapter = RecyclearUserAdapter(this, userList, senderRoom.toString())
//            binding.recycler.adapter = adapter

            mDbRef.child("list").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object :
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

                    userList.clear()
                    onlineList.clear()
                    for (userID in sortedUserIDs) {
                        // Fetch user data using the user ID from the "user" node
                        mDbRef.child("user").child(userID).addListenerForSingleValueEvent(object :
                            ValueEventListener {
                            override fun onDataChange(userDataSnapshot: DataSnapshot) {
                                val currentUser = userDataSnapshot.getValue(User::class.java)
                                if (currentUser != null && !userList.contains(currentUser)) {
                                    userList.add(currentUser)

                                    Log.d("listttttttttttt", currentUser.status.toString())

                                    if (currentUser?.status == true) {
                                        onlineList.add(currentUser)
                                        binding.noOnlineText.isVisible = false
                                    }
                                }
                                adapter.notifyDataSetChanged()
                                firstTimeBoolean = false

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

        binding.menu.setOnClickListener {
            val dialog = Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.menu_chat_view);

            val group = dialog.findViewById<LinearLayout>(R.id.layer1)
            val family = dialog.findViewById<LinearLayout>(R.id.layer2)
            val profile = dialog.findViewById<LinearLayout>(R.id.layer3)

            group.setOnClickListener {
                val i = Intent(this, GroupActivity::class.java)

                val database =
                    FirebaseDatabase.getInstance().reference.child("Groups").child("Data").push()
                val uid = FirebaseAuth.getInstance().uid
                val groupData = Group(database.key!!, uid!!)
                database.setValue(groupData).addOnSuccessListener {
                    i.putExtra("groupId", database.key)
                    startActivity(i)
                }
                dialog.dismiss()

            }

            profile.setOnClickListener {
                val i = Intent(this, ProfileLogOutActivity::class.java)
                startActivity(i)
                dialog.dismiss()
            }

            family.setOnClickListener {
                val intent = Intent(this, AddFamilyActivity::class.java)
                val i = Intent()
                val senderRoom = i.getStringExtra("receiverUid")
                intent.putExtra("uid",senderRoom)
                startActivity(intent)
                dialog.dismiss()
            }

            dialog.show()
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
            dialog.window?.setGravity(Gravity.BOTTOM)

        }
  }


    override fun onResume() {
        super.onResume()
        updateUserStatus(true)

    }
    override fun onPause() {
        super.onPause()
        updateUserStatus(false)
    }

    private fun updateUserStatus(status: Boolean) {
        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid
        val firebaseDef = FirebaseDatabase.getInstance().getReference("user/$uid")
        firebaseDef.child("status").setValue(status)
    }
}