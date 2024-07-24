package com.example.chattingapp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.chattingapp.adapter.GroupChatAdapter
import com.example.chattingapp.databinding.ActivityGroupChatBinding
import com.example.chattingapp.model.Constants
import com.example.chattingapp.model.Group
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.ArrayList
import java.util.HashMap
import java.util.Locale
import java.util.UUID

class GroupChatActivity : AppCompatActivity() {
    private lateinit var messageAdapter: GroupChatAdapter
    private lateinit var messageList: ArrayList<Group>
    private lateinit var mDbRef: DatabaseReference
    private val PHONE_CALL_REQUEST_CODE = 1
    private val REQUEST_IMAGE_CAPTURE = 2002
    private var count:Int? = null
    private var checkBoolean = false
    private var checkBoolean1 = false
    private var groupId:String? = null
    private lateinit var fragmentManager: FragmentManager
    private lateinit var binding: ActivityGroupChatBinding
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupChatBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.window.navigationBarColor = resources.getColor(R.color.fav)

//                setThemeBack()


                mDbRef = FirebaseDatabase.getInstance().reference
                groupId = intent.getStringExtra("uid")



                setReceiverName(groupId.toString(),binding)
                setReceiverProfile(groupId.toString(), binding)

                messageList = ArrayList()
                messageAdapter = GroupChatAdapter(this, messageList,groupId!!)


                val layoutManager = LinearLayoutManager(this)
                layoutManager.stackFromEnd = true
                binding.chatRecyclerView.layoutManager = layoutManager
                binding.chatRecyclerView.adapter = messageAdapter

                mDbRef.child("Groups").child("message").child(groupId!!)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                            messageList.clear()
                            for (postSnapshot in snapshot.children) {
                                val message = postSnapshot.getValue(Group::class.java)

                                messageList.add(message!!)
                            }

                            binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
                            messageAdapter.notifyDataSetChanged()
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle the error if needed
                        }
                    })

                binding.groupPhoto.setOnClickListener {
                    val i = Intent(this, ProfileActivity::class.java)
                    startActivity(i)
                }

                binding.menu.setOnClickListener {

                    groupId = intent.getStringExtra("uid")
                    val dialog = Dialog(this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.chat_menu_bottom);

                    val camera1 = dialog.findViewById<TextView>(R.id.camera1)
                    val camera = dialog.findViewById<LottieAnimationView>(R.id.camera)
                    val theme = dialog.findViewById<LinearLayout>(R.id.layer1)


//                    theme.setOnClickListener {
//                        groupId = intent.getStringExtra("uid")
//
//                        val i = Intent(this, ThemesActivity::class.java)
//                        i.putExtra("uid",groupId)
//                        startActivity(i)
//                        dialog.dismiss()
//
//                    }


                    dialog.show();
                    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                    dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
                    dialog.window?.setGravity(Gravity.BOTTOM);


                }

                binding.sentBtn.setOnClickListener {

                    val message = binding.messageBox.text

                    if (message.isNotEmpty()) {
                        checkBoolean = true

                        checkBoolean1 = true

                        val timeStamp = ServerValue.TIMESTAMP
                        val senderRef =
                            mDbRef.child("Groups").child("message").child(groupId!!).push()
                        val messageId = senderRef.key
                        val groupMap = mutableMapOf<String, Any>()
                        groupMap["groupId"] = messageId!!
                        groupMap["message"] = message.toString()
                        groupMap["timestamp"] = timeStamp
                        groupMap["type"] = "text"
                        groupMap["lastMessageTime"] = timeStamp
                        groupMap["messageSenderId"] = FirebaseAuth.getInstance().uid.toString()

                        mDbRef.child("Groups").child("message").child(groupId!!).push()
                            .setValue(groupMap).addOnCompleteListener { senderTask ->
                            }


                        getToken(message.toString())

                        binding.messageBox.setText("")

                        messageAdapter.notifyDataSetChanged()
                    } else {
                        binding.messageBox.setText("")
                    }
                }

                binding.ImageBack.setOnClickListener {
                    onBackPressed()
                }

                binding.galleryEt.setOnClickListener {
                    showDialog()

                }

                binding.speech.setOnClickListener {
                    askSpeechInput()
                }
            }

//            private fun updateSeenCount() {
//                groupId = intent.getStringExtra("uid")
//                val senderRoomRef = FirebaseDatabase.getInstance().reference.child("messageCount").child(groupId!!).child(
//                    FirebaseAuth.getInstance().uid!!).setValue(0)
//
//            }


            fun getToken(message: String) {
                groupId = intent.getStringExtra("uid")
                val id = mDbRef.child("Groups").child("GroupMembers").child(groupId!!)
                    .addListenerForSingleValueEvent(object :ValueEventListener
                {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (snap in snapshot.children) {
                            val userID = snap.key

                            if (userID != FirebaseAuth.getInstance().uid) {
                                val databaseReference =
                                    FirebaseDatabase.getInstance().getReference("user")
                                        .child(userID!!)
                                databaseReference.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                                        val user = dataSnapshot.getValue(User::class.java)
                                        if (dataSnapshot.exists()) {
                                            val token = user?.token

                                            val to = JSONObject()
                                            to.put("to", token)
                                            val data = JSONObject()

                                            val uid = FirebaseAuth.getInstance().currentUser?.uid
                                            val firebaseRef =
                                                FirebaseDatabase.getInstance().getReference("user")
                                                    .child(uid!!)

                                            firebaseRef.addListenerForSingleValueEvent(object :
                                                ValueEventListener {
                                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                                    val user =
                                                        dataSnapshot.getValue(User::class.java)

                                                    val myName = user?.name
                                                    val myId = user?.uid
                                                    val myImage = user?.profileImageUrl
                                                    val chatId = "${groupId}"

                                                    data.put("hisId", myId)
                                                    data.put("hisImage", myImage)
                                                    data.put("title", myName)
                                                    data.put("message", message)
                                                    data.put("chatId", chatId)

                                                    Log.d("hisToken", token.toString())

                                                    to.put("data", data)
                                                    sendNotification(to)
                                                    Log.d("messageDataHere", to.toString())
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }
                                            })
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        TODO("Not yet implemented")
                                    }
                                })
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }

            private fun sendNotification(to: JSONObject) {
                val request: JsonObjectRequest = object : JsonObjectRequest(
                    Method.POST,
                    Constants.NOTIFICATION_URL,
                    to,
                    Response.Listener { response: JSONObject ->
                        Log.d("TAG", "onResponse: message notification devendra $response")
                    },
                    Response.ErrorListener {
                        Log.d("TAG", "onError:message notification devendra $it")
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val map: MutableMap<String, String> = HashMap()

                        map["Authorization"] = "key=" + Constants.SERVER_KEY
                        map["Content-type"] = "application/json"
                        return map
                    }

                    override fun getBodyContentType(): String {
                        return "application/json"
                    }
                }

                val requestQueue = Volley.newRequestQueue(this@GroupChatActivity)
                request.retryPolicy = DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
                requestQueue.add(request)
            }

            override fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<out String>,
                grantResults: IntArray
            ) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)

                if(requestCode == REQUEST_IMAGE_CAPTURE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
            }

            private fun askSpeechInput() {
                if(!SpeechRecognizer.isRecognitionAvailable(this)){
                    Toast.makeText(this,"Speech Recognition Is Not Available", Toast.LENGTH_SHORT).show()
                }else{
                    val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                    i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say Something!")
                    startActivityForResult(i,10)
                }
            }

            override fun onBackPressed() {
                super.onBackPressed()
                finish()
            }


            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                super.onActivityResult(requestCode, resultCode, data)

                if(requestCode == 10 && resultCode == Activity.RESULT_OK){
                    val result = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val editableResult = Editable.Factory.getInstance().newEditable(result?.get(0))
                    val textMessage = Editable.Factory.getInstance().newEditable(binding.messageBox.text.toString() + " " + editableResult)
                    binding.messageBox.text = textMessage

                }

                if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null){
                    val imageUri = data?.extras?.get("data") as Bitmap
                    val imageUri2 = getImageUriFromBitmap(applicationContext, imageUri)
                    sendImageMessage(imageUri2, groupId)
                }

                if (requestCode == 44 && resultCode == Activity.RESULT_OK && data != null) {
                    val imageUris = mutableListOf<Uri>()

                    if (data.clipData != null) {
                        val count = data.clipData!!.itemCount
                        for (i in 0 until count) {
                            val imageUri = data.clipData!!.getItemAt(i).uri
                            imageUris.add(imageUri)
                        }
                    } else if (data.data != null) {
                        val imageUri = data.data
                        if (imageUri != null) {
                            imageUris.add(imageUri)
                        }
                    }

                    for (imageUri in imageUris) {
                        sendImageMessage(imageUri, groupId)
                    }
                }
            }
            fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
                return Uri.parse(path)
            }


//            private fun setThemeBack() {
//                val sharedPref = this.getSharedPreferences("THEME", Context.MODE_PRIVATE)
//                val themeId = sharedPref.getInt("Theme", -1)
//
//                Log.d("themeId", themeId.toString())
//                when (themeId) {
//                    0 -> {
//                        binding.layer.setBackgroundResource(R.drawable.toolbar_back1)
//                        binding.sentBtn.setBackgroundResource(R.drawable.btn_img)
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            val window: Window = this.window
//                            window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
//                        }
//
//                    }
//                    1 -> {
//                        binding.layer.setBackgroundResource(R.drawable.theme2)
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            val window: Window = this.window
//                            window.statusBarColor = ContextCompat.getColor(this, R.color.violet)
//                        }
//                    }
//                    2 -> {
//                        binding.layer.setBackgroundResource(R.drawable.theme3)
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            val window: Window = this.window
//                            window.statusBarColor = ContextCompat.getColor(this, R.color.pinke)
//                        }
//                    }
//                    3 -> {
//                        binding.layer.setBackgroundResource(R.drawable.theme4)
//                        binding.groupName.setTextColor(resources.getColor(R.color.homeToolbar))
//                        binding.statusEt.setTextColor(resources.getColor(R.color.homeToolbar))
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            val window: Window = this.window
//                            window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)
//                        }
//
//                    }
//                    4 -> binding.layer.setBackgroundResource(R.drawable.theme5)
//                    5 -> {
//                        binding.layer.setBackgroundResource(R.drawable.theme6)
//                        binding.groupName.setTextColor(resources.getColor(R.color.homeToolbar))
//                        binding.statusEt.setTextColor(resources.getColor(R.color.homeToolbar))
//                    }
//                    6 -> {
//                        binding.layer.setBackgroundColor(ContextCompat.getColor(this, R.color.fav))
//                        binding.toolbar.setBackgroundColor(resources.getColor(R.color.fav))
//                        binding.groupName.setTextColor(resources.getColor(R.color.violetIcon))
//                        binding.statusEt.setTextColor(resources.getColor(R.color.violetIcon))
//                        val tintColor = ContextCompat.getColor(this, R.color.violetIcon)
//                        val tintList = ColorStateList.valueOf(tintColor)
//                        binding.call.imageTintList = tintList
//                        binding.menu.imageTintList = tintList
//                        binding.ImageBack.setBackgroundResource(R.drawable.back_arrow)
//                        this.window.navigationBarColor = resources.getColor(R.color.fav)
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            val window: Window = this.window
//                            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
//                        }
//                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                        binding.sentBtn.setBackgroundResource(R.drawable.btn_img)
//                        binding.messageBox.setBackgroundResource(R.drawable.edittext_white)
//
//                    }
//                    7 -> binding.layer.setBackgroundResource(R.drawable.theme8)
//                    8 -> {
//                        binding.layer.setBackgroundResource(R.drawable.theme9)
//                        binding.ImageBack.setBackgroundResource(R.drawable.back_arrow)
//                        binding.groupName.setTextColor(resources.getColor(R.color.homeToolbar))
//                        binding.statusEt.setTextColor(resources.getColor(R.color.homeToolbar))
//
//                    }
//                    9 -> {
//                        binding.layer.setBackgroundResource(R.drawable.theme10)
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            val window: Window = this.window
//                            window.statusBarColor = ContextCompat.getColor(this, R.color.light_dark)
//                        }
//                    }
//
//                    else -> {
//                        binding.layer.setBackgroundColor(ContextCompat.getColor(this, R.color.fav))
//                        binding.toolbar.setBackgroundColor(resources.getColor(R.color.fav))
//                        binding.groupName.setTextColor(resources.getColor(R.color.violetIcon))
//                        binding.statusEt.setTextColor(resources.getColor(R.color.violetIcon))
//                        val tintColor = ContextCompat.getColor(this, R.color.violetIcon)
//                        val tintList = ColorStateList.valueOf(tintColor)
//                        binding.call.imageTintList = tintList
//                        binding.menu.imageTintList = tintList
//                        binding.ImageBack.setBackgroundResource(R.drawable.back_arrow)
//
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                            val window: Window = this.window
//                            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
//                        }
//                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                        binding.sentBtn.setBackgroundResource(R.drawable.btn_img)
//                        binding.messageBox.setBackgroundResource(R.drawable.edittext_white)
//                        this.window.navigationBarColor = resources.getColor(R.color.fav)
//                    }
//                }
//            }

            private fun setReceiverName(receiverId: String, receiverTextView: ActivityGroupChatBinding) {
                val databaseRef = FirebaseDatabase.getInstance().reference.child("Groups").child("Data").child(receiverId)

                databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val receiverUser = snapshot.getValue(Group::class.java)

                            binding.groupName.text = receiverUser?.groupName

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
            }

            private fun setReceiverProfile(receiverId: String, profileImgReceiver: ActivityGroupChatBinding) {
                val databaseRef = FirebaseDatabase.getInstance().reference.child("Groups").child("Data").child(receiverId)

                databaseRef.addValueEventListener(object : ValueEventListener {
                    @SuppressLint("SuspiciousIndentation")
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val recipientUser = dataSnapshot.getValue(Group::class.java)

                        // Set the recipient's profile image

                            Glide.with(applicationContext).load(recipientUser?.groupImage)
                                .into(binding.groupPhoto)
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
            private fun showDialog() {
                val dialog = Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.bottomsheetlayout);

                val gallery = dialog.findViewById<LottieAnimationView>(R.id.gallery)
                val camera = dialog.findViewById<LottieAnimationView>(R.id.camera)

                camera.setOnClickListener {
                    cameraPermission()
                    dialog.dismiss()

                }
                gallery.setOnClickListener{
                    val i = Intent(Intent.ACTION_GET_CONTENT)
                    i.type = "image/*"
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    startActivityForResult(i, 44)
                    dialog.dismiss();
                }

                dialog.show();
                dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
                dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
                dialog.window?.setGravity(Gravity.BOTTOM);

            }

            private fun cameraPermission() {

                if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }else{
                    ActivityCompat.requestPermissions(this,
                        arrayOf( Manifest.permission.CAMERA),REQUEST_IMAGE_CAPTURE)
                }
            }
            fun sendImageMessage(
                imageUri: Uri,
                groupId: String?,
            ) {
                val senderId = FirebaseAuth.getInstance().currentUser?.uid
                val mDbRef = FirebaseDatabase.getInstance().reference
                val storageRef: StorageReference =
                    FirebaseStorage.getInstance().reference.child("imagesMessage/${UUID.randomUUID()}")

                val uploadTask = storageRef.putFile(imageUri)

                uploadTask.addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { uri ->

                        val timeStamp = ServerValue.TIMESTAMP
                        val senderRef = mDbRef.child("Groups").child("message").child(groupId!!).push()

                        val messageId = senderRef.key
                        val groupMap = mutableMapOf<String, Any>()
                        groupMap["groupId"] = messageId!!
                        groupMap["message"] = "Sent Image"
                        groupMap["timestamp"] = timeStamp
                        groupMap["type"] = "image"
                        groupMap["imageMessage"] = uri.toString()
                        groupMap["messageSenderId"] = FirebaseAuth.getInstance().uid.toString()


                        mDbRef.child("Groups").child("message").child(groupId!!).push()
                            .setValue(groupMap).addOnCompleteListener { senderTask ->
                            }
                        getToken("Sent Image")

                    }
                }
                    .addOnFailureListener { exception ->
                        // Handle failure
                    }
            }
}