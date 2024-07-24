package com.example.chattingapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.chattingapp.databinding.ActivityProfileBinding
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : AppCompatActivity() {
    private lateinit var profileImg1: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var etEmail: TextView
    private lateinit var etPhoneNo: TextView
    private lateinit var etAge: TextView
    private lateinit var etcity: TextView
    private lateinit var friendsImg: ImageView
    private lateinit var etBio: TextView
    private lateinit var binding: ActivityProfileBinding
    private var uid:String? = null
    private var name:String? = null
    private var followBoolean = false
    private var followingBoolean = false

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window =this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.teal_200)
        }

        uid = intent.getStringExtra("uid")
        name = intent.getStringExtra("name")
        profileImg1 = findViewById(R.id.profileImg)
        tvName = findViewById(R.id.tv_name)
        etEmail = findViewById(R.id.etEmail)
        etPhoneNo = findViewById(R.id.etPhoneNo)
        etAge = findViewById(R.id.etAge)
        etcity = findViewById(R.id.tv_address)

        etBio = findViewById(R.id.etBio)

        val intent = getIntent()
        // You can now use the intent to extract data or perform other operations
        val uid = intent.getStringExtra("uid")

        val firebaseRef = FirebaseDatabase.getInstance().getReference("user").child(uid.toString())
        firebaseRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentuser = snapshot.getValue(User::class.java)

                if (currentuser?.name == null) {
                    tvName.text = currentuser?.userName
                }else{
                    tvName.text = currentuser.name
                }
                etEmail.text = currentuser?.email
                etPhoneNo.text = currentuser?.phoneNo
                etAge.text = currentuser?.age.toString()
                etcity.text = currentuser?.city
                etBio.text = currentuser?.bio

                val profileImg = currentuser?.profileImageUrl

                Glide.with(this@ProfileActivity).load(profileImg).into(profileImg1)
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

//        binding.followerCount.setOnClickListener {
//            val i = Intent(this,FollowersActivity::class.java)
//            i.putExtra("Follow",uid)
//            i.putExtra("check","Followers")
//            startActivity(i)
//        }
//        binding.followerClick.setOnClickListener {
//            val i = Intent(this,FollowersActivity::class.java)
//            i.putExtra("Follow",uid)
//            i.putExtra("check","Followers")
//            startActivity(i)
//        }
//        binding.followerClick1.setOnClickListener {
//            val i = Intent(this,FollowersActivity::class.java)
//            i.putExtra("Follow",uid)
//            i.putExtra("check","Followers")
//            startActivity(i)
//        }
//        binding.followingCount.setOnClickListener {
//            val i = Intent(this,FollowersActivity::class.java)
//            i.putExtra("Follow",uid)
//            i.putExtra("check","Following")
//            startActivity(i)
//        }
//        binding.followingClick1.setOnClickListener {
//            val i = Intent(this,FollowersActivity::class.java)
//            i.putExtra("Follow",uid)
//            i.putExtra("check","Following")
//            startActivity(i)
//        }
//
//        binding.followingClick.setOnClickListener {
//            val i = Intent(this,FollowersActivity::class.java)
//            i.putExtra("Follow",uid)
//            i.putExtra("check","Following")
//            startActivity(i)
//        }

        followCount(uid)
        binding.follow.setOnClickListener {
            val database = FirebaseDatabase.getInstance().reference.child("Followers")
            followBoolean = true
            val logInUid = FirebaseAuth.getInstance().currentUser?.uid
            database.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(followBoolean){
                        if(snapshot.child(uid!!).hasChild(logInUid!!)){
                            database.child(uid!!).child(logInUid).removeValue()
                            binding.follow.text = "Follow"
                            followBoolean = false

                        }else{
                            database.child(uid).child(logInUid).setValue(true)
                            binding.follow.text = "Following"
                            followBoolean = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

            val database1 = FirebaseDatabase.getInstance().reference.child("Following")
            followingBoolean = true
            database1.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(followingBoolean){
                        if(snapshot.child(logInUid!!).hasChild(uid!!)){
                            database1.child(logInUid).child(uid).removeValue()
                            followingBoolean = false

                        }else{
                            database1.child(logInUid).child(uid).setValue(true)
                            followingBoolean = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        }
//        friendsImg.setOnClickListener{
//            val i = Intent(this@ProfileActivity,FriendsActivity::class.java)
//            i.putExtra("uid",uid)
//            startActivity(i)
//        }

        binding.message.setOnClickListener {
            val i = Intent(applicationContext, ChatActivity::class.java)
            i.putExtra("name", name)
            i.putExtra("uid", uid)
            startActivity(i)
        }


    }

    private fun followCount(uid: String?) {

        val database = FirebaseDatabase.getInstance().reference.child("Followers")
        val logInUid = FirebaseAuth.getInstance().currentUser?.uid
        database.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(uid!!).hasChild(logInUid!!)){
                    val likeCountFirebase = snapshot.child(uid).childrenCount
                    binding.followerCount.text = likeCountFirebase.toString()
                    binding.follow.text = "Following"

                }else{
                    val likeCountFirebase = snapshot.child(uid).childrenCount
                    binding.followerCount.text = likeCountFirebase.toString()
                    binding.follow.text = "Follow"

                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


        val database1 = FirebaseDatabase.getInstance().reference.child("Following")
        database1.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(uid!!).hasChild(logInUid!!)){
                    val likeCountFirebase = snapshot.child(uid).childrenCount
                    binding.followingCount.text = likeCountFirebase.toString()

                }else{
                    val likeCountFirebase = snapshot.child(uid).childrenCount
                    binding.followingCount.text = likeCountFirebase.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
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

    private fun updateUserStatus(status: Boolean) {
        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.currentUser?.uid
        val firebaseDef = FirebaseDatabase.getInstance().getReference("user/$uid")
        firebaseDef.child("status").setValue(status)
    }
}