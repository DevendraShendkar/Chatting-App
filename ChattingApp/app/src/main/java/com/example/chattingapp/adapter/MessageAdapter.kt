package com.example.chattingapp.adapter

import Message
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.util.Log
import com.example.chattingapp.R
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.chattingapp.model.User
import com.google.android.material.internal.ViewUtils.dpToPx
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private val context: Context,
    private val messageList: ArrayList<Message>,
    private val receiptId: String,
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var layout: View
    private var replyClickListener: ReplyClickListener? = null

    companion object {
        private const val ITEM_RECEIVE = 1
        private const val ITEM_SENT_IMAGE =11
        private const val ITEM_SENT = 2
        private const val ITEM_RECEIVE_IMAGE = 22
        private const val ITEM_SENT_REPLY = 12
        private const val ITEM_RECEIVE_REPLY = 13
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_SENT -> {
                val view: View = inflater.inflate(R.layout.sentchat, parent, false)
                SentViewHolder(view)
            }
            ITEM_SENT_IMAGE -> {
                val view: View = inflater.inflate(R.layout.sent_image, parent, false)
                SentImageViewHolder(view)
            }
            ITEM_RECEIVE -> {
                val view: View = inflater.inflate(R.layout.recivedchat, parent, false)
                ReceiveViewHolder(view)
            }
            ITEM_RECEIVE_IMAGE -> {
                val view: View = inflater.inflate(R.layout.receive_image, parent, false)
                ReceiveImageViewHolder(view)
            }
            ITEM_SENT_REPLY ->{
                val view: View = inflater.inflate(R.layout.reply_sent_message, parent, false)
                ReplySentViewHolder(view)
            }
            ITEM_RECEIVE_REPLY ->{
                val view: View = inflater.inflate(R.layout.reply_receive_message, parent, false)
                ReplyReceiveViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        messageList[position].let { currentMessage ->
            when (holder) {
                is SentViewHolder -> {
                        setCurrentUserProfile(holder.sentMessageProfile)

                    holder.sentMessage.text = currentMessage.message

                    val timeStampInMillis = currentMessage.timestamp as Long
                    val date = Date(timeStampInMillis)
                    val format = SimpleDateFormat("HH:mm a")
                    val format1 = SimpleDateFormat("dd/MM/yyyy ")

                    val formattedDate1 = format1.format(date)
                    holder.timeDate.text = formattedDate1
                    // Inside your RecyclerView.Adapter's onBindViewHolder method

                    val sharedPref = context.getSharedPreferences("THEME", Context.MODE_PRIVATE)
                    val themeId = sharedPref.getInt("Theme", -1)

                    when (themeId) {
                            0 -> {
                                holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                            }
                            1 -> {
                                holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                            }
                            2 -> {
                                holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                            }
                            3 -> {
                                holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                            }
                            4 -> {
                                holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                            }
                            5 -> {
                                holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                            }
                            7 -> {
                                holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                            }
                            8 -> {
                                holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                            }
                            9 -> {
                                holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                            }
                        }
                    val formattedDate = format.format(date)
                    holder.timeEt.text = formattedDate

                    holder.itemView.setOnLongClickListener {
                        showOptionsDialog(currentMessage, position)
                        true
                    }
                }
                is ReceiveViewHolder -> {
                    setReceiverUserProfile(holder.recipientProfileImageView, receiptId)
                    holder.receiveMessage.text = currentMessage.message
                    val timeStampInMillis = currentMessage.timestamp as Long
                    val date = Date(timeStampInMillis)
                    val format = SimpleDateFormat("hh:mm a")
                    val format1 = SimpleDateFormat("dd/MM/yyyy ")

                    currentMessage.timestamp

                    val formattedDate1 = format1.format(date)
                    holder.timeDate.text = formattedDate1


                    val formattedDate = format.format(date)
                    holder.timeEtR.text = formattedDate
                    val sharedPref = context.getSharedPreferences("THEME", Context.MODE_PRIVATE)
                    val themeId = sharedPref.getInt("Theme", -1)
                    when (themeId) {
                        0 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        1 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        2 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        3 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        4 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        5 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        7 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        8 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        9 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                    }

                    holder.itemView.setOnLongClickListener {
                        showOptionsDialog(currentMessage, position)
                        true
                    }
                }
                is SentImageViewHolder ->{
                    setCurrentUserProfile(holder.sentMessageProfile)
                    val timeStampInMillis = currentMessage.timestamp!! as Long
                    val date = Date(timeStampInMillis)
                    val format = SimpleDateFormat("MMM dd, yyyy hh:mm a")
                    val formmattedDate = format.format(date)
                    holder.timeEt.text = formmattedDate
                    setImageMessage(holder.imageSent, currentMessage.image)
                    holder.imageSent.setOnLongClickListener {
                        showOptionsDialog(currentMessage, position)
                        true
                    }
                    val sharedPref = context.getSharedPreferences("THEME", Context.MODE_PRIVATE)
                    val themeId = sharedPref.getInt("Theme", -1)
                    when (themeId) {
                        0 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        1 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        2 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        3 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        4 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        5 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        7 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )
                        }

                        8 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        9 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }
                    }

                    holder.itemView.setOnLongClickListener {
                        showOptionsDialog(currentMessage, position)
                        true
                    }
                }
                is ReceiveImageViewHolder ->{
                    setReceiverMessage(holder.receiveMessageProfile, receiptId)
                    val timeStampInMillis = currentMessage.timestamp!! as Long
                    val date = Date(timeStampInMillis)
                    val format = SimpleDateFormat("MMM dd, yyyy hh:mm a")
                    val formmattedDate = format.format(date)
                    holder.timeEt.text = formmattedDate
                    setImageMessage(holder.imageReceive, currentMessage.image)
                    holder.imageReceive.setOnLongClickListener {
                        showOptionsDialog(currentMessage, position)
                        true
                    }
                    val sharedPref = context.getSharedPreferences("THEME", Context.MODE_PRIVATE)
                    val themeId = sharedPref.getInt("Theme", -1)
                    when (themeId) {
                        0 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        1 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        2 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        3 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        4 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        5 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        7 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.white
                                )
                            )
                        }

                        8 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        9 -> {
                            holder.timeEt.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }
                    }
                    holder.itemView.setOnLongClickListener {
                        showOptionsDialog(currentMessage, position)
                        true
                    }
                }
                is ReplySentViewHolder ->{

                    setCurrentUserProfile(holder.sentMessageProfile)

                    holder.sentMessage.text = currentMessage.message

                    val timeStampInMillis = currentMessage.timestamp as Long
                    val date = Date(timeStampInMillis)
                    val format = SimpleDateFormat("HH:mm a")
                    val format1 = SimpleDateFormat("dd/MM/yyyy ")


                    holder.replyMessage.setOnClickListener {
                        val clickedPosition = currentMessage.repliedPosition
                        recyclerView.scrollToPosition(clickedPosition?.toInt()!!)
                        Log.d("positionhere",clickedPosition)

                    }

                    val formattedDate1 = format1.format(date)
                    holder.timeDate.text = formattedDate1
                    // Inside your RecyclerView.Adapter's onBindViewHolder method

                    setReplayMessage(holder,currentMessage)

                    val sharedPref = context.getSharedPreferences("THEME", Context.MODE_PRIVATE)
                    val themeId = sharedPref.getInt("Theme", -1)

                    when (themeId) {
                        0 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        1 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        2 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        3 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        4 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        5 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        7 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                            holder.replyText.setTextColor(ContextCompat.getColor(context, R.color.white))
                        }
                        8 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.favBlack))
                            holder.replyText.setTextColor(ContextCompat.getColor(context, R.color.favBlack))

                        }
                        9 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                    }
                    val formattedDate = format.format(date)
                    holder.timeEt.text = formattedDate

                    holder.itemView.setOnLongClickListener {
                        showOptionsDialog(currentMessage, position)
                        true
                    }
                }
                is ReplyReceiveViewHolder ->{
                    setReceiverUserProfile(holder.receiverMessageProfile, receiptId)
                    holder.receiveMessage.text = currentMessage.message
                    val timeStampInMillis = currentMessage.timestamp as Long
                    val date = Date(timeStampInMillis)
                    val format = SimpleDateFormat("hh:mm a")
                    val format1 = SimpleDateFormat("dd/MM/yyyy ")

                    currentMessage.timestamp

                    val formattedDate1 = format1.format(date)
                    holder.timeDate.text = formattedDate1

                    holder.replyMessage.setOnClickListener {
                        val clickedPosition = currentMessage.repliedPosition?.toInt()
                        recyclerView.scrollToPosition(clickedPosition!!)
                        Log.d("position is here", clickedPosition.toString())
                    }

                    setReplayReceivedMessage(holder,currentMessage)

                    val formattedDate = format.format(date)
                    holder.timeEt.text = formattedDate
                    val sharedPref = context.getSharedPreferences("THEME", Context.MODE_PRIVATE)
                    val themeId = sharedPref.getInt("Theme", -1)
                    when (themeId) {
                        0 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        1 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        2 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        3 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        4 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        5 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                        7 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                            holder.replyText.setTextColor(ContextCompat.getColor(context, R.color.white))

                        }
                        8 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.favBlack))
                            holder.replyText.setTextColor(ContextCompat.getColor(context, R.color.favBlack))

                        }
                        9 -> {
                            holder.timeDate.setTextColor(ContextCompat.getColor(context, R.color.black))
                        }
                    }

                    holder.itemView.setOnLongClickListener {
                        showOptionsDialog(currentMessage, position)
                        true
                    }

                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if (currentMessage.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            if (currentMessage.type == "textReplay") {
                ITEM_SENT_REPLY
            }else if (currentMessage.type == "text") {
                ITEM_SENT
            }else
                ITEM_SENT_IMAGE

        }else {
            if (currentMessage.type == "textReplay") {
                ITEM_RECEIVE_REPLY
            } else if(currentMessage.type == "text") {
                ITEM_RECEIVE
                Log.d("currentreplayyy66y",currentMessage.type.toString())
            }else{
                ITEM_RECEIVE_IMAGE
            }
        }
    }
    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val sentMessageProfile = itemView.findViewById<CircleImageView>(R.id.current_userProfile)
        val timeEt = itemView.findViewById<TextView>(R.id.timeEt)
        val timeDate = itemView.findViewById<TextView>(R.id.timeDate)
        val layout = itemView.findViewById<RelativeLayout>(R.id.layout100)
        val layer = itemView.findViewById<ConstraintLayout>(R.id.layer)
    }
    inner class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_received_message)
        val recipientProfileImageView = itemView.findViewById<CircleImageView>(R.id.receiver_userProfile)
        val timeEtR = itemView.findViewById<TextView>(R.id.timeEtR)
        val timeDate = itemView.findViewById<TextView>(R.id.timeDate)
        val layout = itemView.findViewById<RelativeLayout>(R.id.layout100)
        val layer = itemView.findViewById<ConstraintLayout>(R.id.layer)

    }
    inner class SentImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageSent = itemView.findViewById<ImageView>(R.id.img_sent_message)
        val timeEt = itemView.findViewById<TextView>(R.id.timeEtImgS)
        val sentMessageProfile = itemView.findViewById<CircleImageView>(R.id.current_userProfileImgS)
        val layer = itemView.findViewById<ConstraintLayout>(R.id.layer)


    }
    inner class ReceiveImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val imageReceive: ImageView = itemView.findViewById(R.id.img_received_message)
        val timeEt: TextView = itemView.findViewById(R.id.timeEtImgR)
        val receiveMessageProfile: CircleImageView = itemView.findViewById(R.id.receiver_userProfile)
        val layer: ConstraintLayout = itemView.findViewById(R.id.layer)

    }
    inner class ReplySentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage: TextView = itemView.findViewById(R.id.txt_sent_message)
        val sentMessageProfile: CircleImageView = itemView.findViewById(R.id.current_userProfile)
        val timeEt: TextView = itemView.findViewById(R.id.timeEt)
        val timeDate: TextView = itemView.findViewById(R.id.timeDate)
        val layout: RelativeLayout = itemView.findViewById(R.id.layout100)
        val layer: ConstraintLayout = itemView.findViewById(R.id.layer)
        var replyMessage :TextView = itemView.findViewById(R.id.reply)
        var replyText :TextView = itemView.findViewById(R.id.replayWritten)
    }
    inner class ReplyReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage: TextView = itemView.findViewById(R.id.txt_sent_message)
        val receiverMessageProfile: CircleImageView = itemView.findViewById(R.id.current_userProfile)
        val timeEt: TextView = itemView.findViewById(R.id.timeEt)
        val timeDate: TextView = itemView.findViewById(R.id.timeDate)
        val layout: RelativeLayout = itemView.findViewById(R.id.layout100)
        val layer: ConstraintLayout = itemView.findViewById(R.id.layer)
        var replyMessage :TextView = itemView.findViewById(R.id.reply)
        var replyText :TextView = itemView.findViewById(R.id.replayWritten)

    }

    private fun setReceiverMessage(recipientProfileImageView: CircleImageView, recipientId: String){
        val databaseRef = FirebaseDatabase.getInstance().reference.child("user").child(recipientId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recipientUser = dataSnapshot.getValue(User::class.java)

                if (recipientUser?.profileImageUrl != null) {
                    Glide.with(context).load(recipientUser.profileImageUrl).into(recipientProfileImageView)
                } else {
                    Glide.with(context).load(R.drawable.person).into(recipientProfileImageView)

                }
            }
            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }
    private fun showOptionsDialog(currentMessage: Message, position: Int) {
        val builder = AlertDialog.Builder(context)
        val copyOption = if (currentMessage.type == "text") "Copy Text" else "Copy Image"
        if (currentMessage.senderId == FirebaseAuth.getInstance().currentUser?.uid ) {
            builder.setItems(arrayOf("Delete", copyOption)) { _, which ->
                when (which) {
                    0 -> showDeleteDialog(currentMessage, position, true)
                    1 -> copyMessage(currentMessage)

                }
            }
        } else {
            builder.setItems(arrayOf("Delete",copyOption)) { _, which ->
                when (which) {
                    0 -> showDeleteDialog(currentMessage, position, false)
                    1 -> copyMessage(currentMessage)
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
    private fun copyMessage(currentMessage: Message) {
        when (currentMessage.type) {
            "text" -> {
                val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", currentMessage.message)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            else -> {

                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("Image URL", currentMessage.image)
                clipboard.setPrimaryClip(clipData)
                Toast.makeText(context, "Image copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setCurrentUserProfile(sentMessageProfile: CircleImageView) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val databaseRef = FirebaseDatabase.getInstance().reference.child("user").child(uid)
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the User object for the recipient
                val recipientUser = dataSnapshot.getValue(User::class.java)

                // Set the recipient's profile image
                if (recipientUser?.profileImageUrl != null) {
                    Glide.with(context).load(recipientUser.profileImageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(sentMessageProfile)
                } else {
                    // set a default profile image if the recipient doesn't have one
                    Glide.with(context).load(R.drawable.person).into(sentMessageProfile)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun showDeleteDialog(currentMessage: Message, position: Int, deleteForEveryone: Boolean) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to delete this message?")
            .setPositiveButton("Delete") { dialog, which ->
                if (currentMessage.type == "text") {
                    deleteMessage(currentMessage, position, deleteForEveryone)
                }else deleteImage(currentMessage,position)
            }
            .setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
    }
    private fun deleteImage(currentMessage: Message, position: Int) {
        val databaseRef = FirebaseDatabase.getInstance().reference

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        val receiverUid = receiptId
        val senderRoom = receiverUid + senderUid
        val receiverRoom = senderUid + receiverUid

        val query = databaseRef.child("chats")
            .child(senderRoom)
            .child("message")
            .orderByChild("image")
            .equalTo(currentMessage.image)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }

                if (senderUid == currentMessage.senderId) {
                    messageList.removeAt(position)
                    notifyItemRemoved(position)

                    // Delete message from receiver's chat room
                    val receiverQuery = databaseRef.child("chats")
                        .child(receiverRoom)
                        .child("message")
                        .orderByChild("image")
                        .equalTo(currentMessage.image)

                    receiverQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            for (snapshot in dataSnapshot.children) {
                                snapshot.ref.removeValue()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // handle the error
                        }
                    })

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
    private fun deleteMessage(message: Message, position: Int, deleteForEveryone: Boolean) {
        val databaseRef = FirebaseDatabase.getInstance().reference

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        val receiverUid = receiptId
        val senderRoom = receiverUid + senderUid
        val receiverRoom = senderUid + receiverUid

        val query = databaseRef.child("chats")
            .child(senderRoom)
            .child("message")
            .orderByChild("message")
            .equalTo(message.message)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }

                if (senderUid == message.senderId) {
                    messageList.removeAt(position)
                    notifyItemRemoved(position)

                    if (deleteForEveryone) {
                        // Delete message from receiver's chat room
                        val receiverQuery = databaseRef.child("chats")
                            .child(receiverRoom)
                            .child("message")
                            .orderByChild("message")
                            .equalTo(message.message)

                        receiverQuery.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                for (snapshot in dataSnapshot.children) {
                                    snapshot.ref.removeValue()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // handle the error
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // handle the error
            }
        })


}
    private fun setReceiverUserProfile(recipientProfileImageView: CircleImageView, recipientId: String) {
            val databaseRef = FirebaseDatabase.getInstance().reference.child("user").child(recipientId)

            databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get the User object for the recipient
                    val recipientUser = dataSnapshot.getValue(User::class.java)


                    // Set the recipient's profile image
                    if (recipientUser?.profileImageUrl != null) {
                        Glide.with(context).load(recipientUser.profileImageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(recipientProfileImageView)
                    } else {
                        // set a default profile image if the recipient doesn't have one
                        Glide.with(context).load(R.drawable.person).into(recipientProfileImageView)

                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // handle the error
                }
            })
        }
    private fun setImageMessage(imageSent: ImageView,imageUrl: String?) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        val receiverUid = receiptId
        val senderRoom = receiverUid + senderUid

//        imageSent.setOnClickListener(View.OnClickListener {
//            val intent = Intent(context, ImageActivity::class.java)
//            intent.putExtra("image_url", imageUrl)
//            context.startActivity(intent)
//        })

        databaseRef.child("chats").child(senderRoom).child("message").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val message = snapshot.getValue(Message::class.java)
                if (imageUrl != null) {
                    Glide.with(context).load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageSent)
                    Log.d("MessageImageNew", message?.image.toString())
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun setReplayReceivedMessage(holder: MessageAdapter.ReplyReceiveViewHolder, currentMessage: Message) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        val receiverUid = receiptId
        val senderRoom = receiverUid + senderUid

        if (currentMessage.replayId != null) {
            databaseRef.child("chats").child(senderRoom).child("message").child(currentMessage.replayId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val message = snapshot.getValue(Message::class.java)
                        holder.replyMessage.text = message?.message
                        Log.d("check noww", currentMessage.replayId.toString())
                        val uid= message?.senderId
                        FirebaseDatabase.getInstance().reference.child("user").child(uid!!)
                            .addListenerForSingleValueEvent(object :ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val data = snapshot.getValue(User::class.java)
                                    if(message.senderId == FirebaseAuth.getInstance().uid){
                                        holder.replyText.text = "Replied to Yourself"
                                    }else
                                        holder.replyText.text = "Replied to " +data?.name
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }
    private fun setReplayMessage(holder: MessageAdapter.ReplySentViewHolder, currentMessage: Message) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        val receiverUid = receiptId
        val senderRoom = receiverUid + senderUid

        if (currentMessage.replayId != null) {
            databaseRef.child("chats").child(senderRoom).child("message").child(currentMessage.replayId!!)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val message = snapshot.getValue(Message::class.java)
                        holder.replyMessage.text = message?.message
                        val uid= message?.senderId
                        FirebaseDatabase.getInstance().reference.child("user").child(uid!!)
                            .addListenerForSingleValueEvent(object :ValueEventListener{
                                @SuppressLint("SuspiciousIndentation")
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val data = snapshot.getValue(User::class.java)
                                    if(message.senderId == FirebaseAuth.getInstance().uid){
                                        holder.replyText.text = "Replied to Yourself"
                                    }else
                                        holder.replyText.text = "Replied to " +data?.name
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                            })
                        Log.d("check noww", currentMessage.replayId.toString())
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }

    interface ReplyClickListener {
        fun onReplyClick()
    }
    fun setReplyClickListener(listener: ReplyClickListener) {
        replyClickListener = listener
    }
}
