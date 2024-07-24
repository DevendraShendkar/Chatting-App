package com.example.chattingapp

import Message
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
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.chat.bnNavigation.message.SwipeControllerActions
import com.example.chattingapp.adapter.MessageAdapter
import com.example.chattingapp.databinding.ActivityChatBinding
import com.example.chattingapp.model.Constants
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*

class ChatActivity : AppCompatActivity(), MessageAdapter.ReplyClickListener  {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sendBtn: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var imageBack: ImageView
    private lateinit var linearLayout: LinearLayout
    private var hisId: String? = null
    private var hisImage: String? = null
    private var chatId: String? = null
    private lateinit var statusEt: TextView
    private lateinit var layer: RelativeLayout
    private lateinit var toolbar: Toolbar
    private lateinit var userName: TextView
    private val PHONE_CALL_REQUEST_CODE = 1
    private val REQUEST_IMAGE_CAPTURE = 2002
    private var count:Int? = null
    private var clickedMessage: String? = null
    private var selectedMessagePosition: Message? = null
    private var isReplay: Boolean? = false
    private var replyPosition: String? =null

    var receiverRoom: String? = null
    var senderRoom: String? = null
    var receiverUid: String? = null
    private var checkBoolean = false
    private var checkBoolean1 = false
    private lateinit var binding: ActivityChatBinding

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sendBtn = findViewById(R.id.sentBtn)
        linearLayout = findViewById(R.id.linearLayout)
        toolbar = findViewById(R.id.toolbar)
        statusEt = findViewById(R.id.statusEt)
        layer = findViewById(R.id.layer)
        imageBack = findViewById(R.id.ImageBack)
        userName = findViewById(R.id.user_name)
        val profileImgReceiver = findViewById<CircleImageView>(R.id.receiver_profile_photo)

        if (intent.hasExtra("chatId")) {
            chatId = intent.getStringExtra("chatId")
            hisId = intent.getStringExtra("hisId")
            hisImage = intent.getStringExtra("hisImage")

        } else {
            hisId = intent.getStringExtra("hisId")
            hisImage = intent.getStringExtra("hisImage")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        this.window.navigationBarColor = resources.getColor(R.color.fav)

//        setThemeBack()
        updateSeenCount()
        mDbRef = FirebaseDatabase.getInstance().reference
        receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        setSupportActionBar(toolbar)
        setStatus(receiverUid.toString())
        supportActionBar?.setDisplayShowTitleEnabled(false)

        setReceiverName(receiverUid.toString(), userName)
        setReceiverProfile(receiverUid.toString(), profileImgReceiver)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList, receiverUid.toString(),binding.chatRecyclerView)
        messageAdapter.setReplyClickListener(this)
//        val i = Intent(this@ChatActivity, HomeFragment::class.java)
//        i.putExtra("receiverUid", receiverRoom)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        binding.chatRecyclerView.layoutManager = layoutManager
        binding.chatRecyclerView.adapter = messageAdapter

        mDbRef.child("chats").child(senderRoom!!).child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                    messageList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }

                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error if needed
                }
            })
        val messageSwipeController = MessageSwipeController(this, object : SwipeControllerActions {
            override fun showReplyUI(position: Int) {
                selectedMessagePosition = messageList[position]
                replyPosition = position.toString()
                isReplay = true
                showQuotedMessage(messageList[position])
            }
        })

        val itemTouchHelper = ItemTouchHelper(messageSwipeController)
        itemTouchHelper.attachToRecyclerView(chatRecyclerView)

        binding.call.setOnClickListener {
            phoneCall()
        }

        binding.receiverProfilePhoto.setOnClickListener {
            val i = Intent(this, ProfileActivity::class.java)
            i.putExtra("uid", receiverUid)
            startActivity(i)
        }

        binding.menu.setOnClickListener {

            receiverUid = intent.getStringExtra("uid")
            val dialog = Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.chat_menu_bottom);

            val theme = dialog.findViewById<LinearLayout>(R.id.layer1)

//            theme.setOnClickListener {
//                receiverUid = intent.getStringExtra("uid")
//
//                val i = Intent(this,ThemesActivity::class.java)
//                i.putExtra("uid",receiverUid)
//                startActivity(i)
//                dialog.dismiss()
//
//            }
            dialog.show();
            dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation;
            dialog.window?.setGravity(Gravity.BOTTOM);

        }

        binding.cancelReply.setOnClickListener {
            hideReplyLayout()
        }

        sendBtn.setOnClickListener {

            val message = messageBox.text
            hideReplyLayout()
            if (message.isNotEmpty()) {
                checkBoolean = true
                val databaseLike = FirebaseDatabase.getInstance().reference.child("list")
                databaseLike.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (checkBoolean) {
                            databaseLike.child(senderUid!!).child(receiverUid!!)
                                .setValue(ServerValue.TIMESTAMP)
                            databaseLike.child(receiverUid!!).child(senderUid!!)
                                .setValue(ServerValue.TIMESTAMP)
                            checkBoolean = false

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

                checkBoolean1 = true
                val database = FirebaseDatabase.getInstance().reference.child("messageCount")
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (checkBoolean1) {
                            val currentCount = snapshot.child(senderUid!!).child(receiverUid!!)
                                .getValue(Int::class.java) ?: 0
                            count = currentCount + 1

                            database.child(senderUid).child(receiverUid!!).setValue(count)
                            checkBoolean1 = false

                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })


                val timeStamp = ServerValue.TIMESTAMP
                val senderRef = mDbRef.child("chats").child(senderRoom!!).child("message").push()
                val messageId = senderRef.key
                Log.d("ghghgh",selectedMessagePosition.toString())
                if (isReplay!!) {
                    val messageMap = mapOf(
                        "messageId" to messageId,
                        "message" to message.toString(),
                        "senderId" to senderUid,
                        "receiverId" to receiverUid,
                        "timestamp" to timeStamp,
                        "image" to null,
                        "type" to "textReplay",
                        "replayId" to selectedMessagePosition?.messageId,
                        "repliedPosition" to replyPosition
                    )
                    Log.d("rrrrr",selectedMessagePosition?.messageId.toString())
                    mDbRef.child("chats").child(senderRoom!!).child("message").child(messageId!!)
                        .updateChildren(messageMap).addOnCompleteListener { senderTask ->
                            if (senderTask.isSuccessful) {
                                mDbRef.child("chats").child(receiverRoom!!).child("message").child(messageId)
                                    .updateChildren(messageMap)
                            }
                        }
//                    val messageRef = mDbRef.child("chats").child(senderRoom!!).child("message").child(messageId!!)
//                    messageRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            val existingMessage = snapshot.getValue(Message::class.java)
//                            existingMessage?.replayId = selectedMessagePosition?.messageId // Update the replayId
//                            messageRef.setValue(existingMessage)
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//                            // Handle the error
//                        }
//                    })
                    isReplay = false

                    getToken(message.toString())

                    messageBox.setText("")

                    messageAdapter.notifyDataSetChanged()
                } else {


                    val messageMap = mapOf(
                        "messageId" to messageId,
                        "message" to message.toString(),
                        "senderId" to senderUid,
                        "receiverId" to receiverUid,
                        "timestamp" to timeStamp,
                        "image" to null,
                        "type" to "text"
                    )

                    mDbRef.child("chats").child(senderRoom!!).child("message").child(messageId!!)
                        .updateChildren(messageMap).addOnCompleteListener { senderTask ->
                            if (senderTask.isSuccessful) {
                                mDbRef.child("chats").child(receiverRoom!!).child("message").child(messageId)
                                    .updateChildren(messageMap)
                            }
                        }
                    getToken(message.toString())

                    messageBox.setText("")

                    messageAdapter.notifyDataSetChanged()
                }
                } else {
                    messageBox.setText("")
                }


            imageBack.setOnClickListener {
                val i = Intent(this@ChatActivity, HomeMain::class.java)
                startActivity(i)
            }
        }

        imageBack.setOnClickListener {
            onBackPressed()
        }



        // ... other methods

        binding.galleryEt.setOnClickListener {
            showDialog()

        }

        binding.speech.setOnClickListener {
            askSpeechInput()
        }
    }

    private fun updateSeenCount() {
        receiverUid = intent.getStringExtra("uid")
        FirebaseDatabase.getInstance().reference.child("messageCount").child(receiverUid!!).child(FirebaseAuth.getInstance().uid!!).setValue(0)

    }

    private fun hideReplyLayout() {
        binding.reply.visibility = View.GONE
        binding.cancelReply.isVisible = false
    }

    private fun showQuotedMessage(message: Message) {
        binding.messageBox.requestFocus()
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager?.showSoftInput(binding.messageBox, InputMethodManager.SHOW_IMPLICIT)
        binding.reply.text = message.message
        Log.d("ooooooo",message.messageId.toString())
        binding.reply.visibility = View.VISIBLE
        binding.cancelReply.isVisible = true

    }

    override fun onReplyClick() {
        // Display the clicked message content in the aboveEditTextMessage TextView
        binding.reply.setText(clickedMessage)
        binding.reply.visibility = View.VISIBLE
        binding.cancelReply.isVisible = true

        // Hide the keyboard
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(messageBox.windowToken, 0)

    }


    fun getToken(message: String) {
        receiverUid = intent.getStringExtra("uid")
        val databaseReference =
            FirebaseDatabase.getInstance().getReference("user").child(receiverUid!!)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (dataSnapshot.exists()) {
                    val token = user?.token

                    val to = JSONObject()
                    to.put("to", token)
                    val data = JSONObject()

                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                    val firebaseRef =
                        FirebaseDatabase.getInstance().getReference("user").child(uid!!)

                    firebaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val user = dataSnapshot.getValue(User::class.java)

                            val myName = user?.name
                            val myId = user?.uid
                            val myImage = user?.profileImageUrl
                            val chatId = "${receiverUid}${uid}"

                            data.put("hisId", myId)
                            data.put("hisImage", myImage)
                            data.put("title", myName)
                            data.put("message", message)
                            data.put("chatId", chatId)

                            Log.d("hisToken",token.toString())

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

        val requestQueue = Volley.newRequestQueue(this@ChatActivity)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }

    private fun phoneCall() {
        receiverUid = intent.getStringExtra("uid")
        var phoneNumber = ""
        val database = FirebaseDatabase.getInstance().reference.child("user").child(receiverUid!!)
       database.addListenerForSingleValueEvent(object : ValueEventListener {
           override fun onDataChange(snapshot: DataSnapshot) {
               val user = snapshot.getValue(User::class.java)

               phoneNumber =user?.phoneNo.toString()

               Log.d("devenda dad", phoneNumber)
               if (ContextCompat.checkSelfPermission(
                       applicationContext,
                       Manifest.permission.CALL_PHONE
                   ) == PackageManager.PERMISSION_GRANTED
               ) {

                   val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
                   startActivity(callIntent)
               } else {
                   ActivityCompat.requestPermissions(
                       this@ChatActivity,
                       arrayOf(Manifest.permission.CALL_PHONE),
                       PHONE_CALL_REQUEST_CODE
                   )
               }
           }

           override fun onCancelled(error: DatabaseError) {
               TODO("Not yet implemented")
           }

           })
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
        if (requestCode == PHONE_CALL_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                var phoneNumber = ""
                val database = FirebaseDatabase.getInstance().reference.child("user").child(receiverUid!!)
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val user = snapshot.getValue(User::class.java)

                        phoneNumber = user?.phoneNo.toString()
                        val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
                        startActivity(callIntent)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })

            }
        }
    }

    private fun askSpeechInput() {
        if(!SpeechRecognizer.isRecognitionAvailable(this)){
            Toast.makeText(this,"Speech Recognition Is Not Available", Toast.LENGTH_SHORT).show()
        }else{
            val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
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
            val textMessage = Editable.Factory.getInstance().newEditable(messageBox.text.toString() + " " + editableResult)
            messageBox.text = textMessage

        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK && data != null){
            val imageUri = data?.extras?.get("data") as Bitmap
            val imageUri2 = getImageUriFromBitmap(applicationContext, imageUri)
            sendImageMessage(imageUri2, senderRoom, receiverRoom, receiverUid)
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
                sendImageMessage(imageUri, senderRoom, receiverRoom, receiverUid)
            }
        }
    }
    fun getImageUriFromBitmap(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }


    @SuppressLint("ResourceAsColor")
//    private fun setThemeBack() {
//        val sharedPref = this.getSharedPreferences("THEME", Context.MODE_PRIVATE)
//        val themeId = sharedPref.getInt("Theme", -1)
//
//        Log.d("themeId", themeId.toString())
//        when (themeId) {
//            0 -> {
//                layer.setBackgroundResource(R.drawable.toolbar_back1)
//                sendBtn.setBackgroundResource(R.drawable.btn_img)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val window: Window = this.window
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.blue)
//                }
//            }
//            1 -> {
//                layer.setBackgroundResource(R.drawable.theme2)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val window: Window = this.window
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.violet)
//                }
//            }
//            2 -> {
//                layer.setBackgroundResource(R.drawable.theme3)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val window: Window = this.window
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.pinke)
//                }
//            }
//            3 -> {
//                layer.setBackgroundResource(R.drawable.theme4)
//                userName.setTextColor(resources.getColor(R.color.homeToolbar))
//                statusEt.setTextColor(resources.getColor(R.color.homeToolbar))
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val window: Window = this.window
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.light_blue)
//                }
//
//            }
//            4 -> layer.setBackgroundResource(R.drawable.theme5)
//            5 -> {
//                layer.setBackgroundResource(R.drawable.theme6)
//                userName.setTextColor(resources.getColor(R.color.homeToolbar))
//                statusEt.setTextColor(resources.getColor(R.color.homeToolbar))
//            }
//            6 -> {
//                layer.setBackgroundColor(ContextCompat.getColor(this, R.color.fav))
//                toolbar.setBackgroundColor(resources.getColor(R.color.fav))
//                userName.setTextColor(resources.getColor(R.color.violetIcon))
//                statusEt.setTextColor(resources.getColor(R.color.violetIcon))
//                val tintColor = ContextCompat.getColor(this, R.color.violetIcon)
//                val tintList = ColorStateList.valueOf(tintColor)
//                binding.call.setImageTintList(tintList)
//                binding.menu.setImageTintList(tintList)
//                imageBack.setBackgroundResource(R.drawable.back_arrow)
//                this.window.navigationBarColor = resources.getColor(R.color.fav)
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val window: Window = this.window
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
//                }
//                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                sendBtn.setBackgroundResource(R.drawable.btn_img)
//                messageBox.setBackgroundResource(R.drawable.edittext_white)
//
//            }
//
//            7 -> {
//                layer.setBackgroundResource(R.drawable.theme8)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val window: Window = this.window
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.theme7)
//                }
//            }
//            8 -> {
//                layer.setBackgroundResource(R.drawable.theme9)
//                imageBack.setBackgroundResource(R.drawable.back_arrow)
//                userName.setTextColor(resources.getColor(R.color.homeToolbar))
//                statusEt.setTextColor(resources.getColor(R.color.homeToolbar))
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val window: Window = this.window
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.theme8)
//                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                }
//            }
//            9 -> {
//                layer.setBackgroundResource(R.drawable.theme10)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val window: Window = this.window
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.light_dark)
//                }
//            }
//
//            else -> {
//                layer.setBackgroundColor(ContextCompat.getColor(this, R.color.fav))
//                toolbar.setBackgroundColor(resources.getColor(R.color.fav))
//                userName.setTextColor(resources.getColor(R.color.violetIcon))
//                statusEt.setTextColor(resources.getColor(R.color.violetIcon))
//                val tintColor = ContextCompat.getColor(this, R.color.violetIcon)
//                val tintList = ColorStateList.valueOf(tintColor)
//                binding.call.setImageTintList(tintList)
//                binding.menu.setImageTintList(tintList)
//                imageBack.setBackgroundResource(R.drawable.back_arrow)
//
//
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    val window: Window = this.window
//                    window.statusBarColor = ContextCompat.getColor(this, R.color.fav)
//                }
//                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                sendBtn.setBackgroundResource(R.drawable.btn_img)
//                messageBox.setBackgroundResource(R.drawable.edittext_white)
//                this.window.navigationBarColor = resources.getColor(R.color.fav)
//            }
//        }
//    }

    private fun setReceiverName(receiverId: String, receiverTextView: TextView) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("user").child(receiverId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val receiverUser = snapshot.getValue(User::class.java)

                if (receiverUser?.name == null) {
                    receiverTextView.text = receiverUser?.userName
                } else {
                    receiverTextView.text = receiverUser.name
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setReceiverProfile(receiverId: String, profileImgReceiver: CircleImageView) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("user").child(receiverId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recipientUser = dataSnapshot.getValue(User::class.java)

                // Set the recipient's profile image
                if (recipientUser?.profileImageUrl != null) {
                    Glide.with(applicationContext).load(recipientUser.profileImageUrl)
                        .into(profileImgReceiver)
                } else {
                    // set a default profile image if the recipient doesn't have one
                    Glide.with(applicationContext).load(R.drawable.person).into(profileImgReceiver)
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

    private fun setStatus(receiverId: String) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("user").child(receiverId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val receiverUser = snapshot.getValue(User::class.java)

                if (receiverUser?.status == true) statusEt.text = "Online"
                else statusEt.text = "Offline"
            }
            override fun onCancelled(error: DatabaseError) {
                // handle error
            }
        })
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
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
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
        senderRoom: String?,
        receiverRoom: String?,
        receiverUid: String?,
    ) {
        val senderId = FirebaseAuth.getInstance().currentUser?.uid
        val mDbRef = FirebaseDatabase.getInstance().reference
        val storageRef: StorageReference =
            FirebaseStorage.getInstance().reference.child("imagesMessage/${UUID.randomUUID()}")

        val uploadTask = storageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            storageRef.downloadUrl.addOnSuccessListener { uri ->

                val timeStamp = ServerValue.TIMESTAMP
                val senderRef = mDbRef.child("chats").child(senderRoom!!).child("message").push()
                val messageId = senderRef.key
                val messageObject =
                    Message(
                        messageId,
                        "SENT IMAGE",
                        senderId,
                        receiverUid,
                        timeStamp,
                        uri.toString(),
                        "IMAGE"
                    )
                getToken("Sent Image")

                mDbRef.child("chats").child(senderRoom!!).child("message").push()
                    .setValue(messageObject).addOnCompleteListener { senderTask ->
                        if (senderTask.isSuccessful) {
                            mDbRef.child("chats").child(receiverRoom!!).child("message").push()
                                .setValue(messageObject)
                        }
                    }
            }
        }
            .addOnFailureListener { exception ->
                // Handle failure
            }
    }
}
