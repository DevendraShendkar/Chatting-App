package com.example.chattingapp.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.R
import com.example.chattingapp.model.Group
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class GroupChatAdapter(
    private val context: Context,
    private val messageList: ArrayList<Group>,
    private val receiptId: String,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var isAnimated: Boolean = false
    lateinit var layout: View
    private var lastFormat1DisplayTime: Long = 0
    private var lastPosition = -1
    private var dateBoolean: Boolean = true
    private var dates: String = ""

    companion object {
        private const val ITEM_RECEIVE = 1
        private const val ITEM_SENT_IMAGE = 11
        private const val ITEM_SENT = 2
        private const val ITEM_RECEIVE_IMAGE = 22
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

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
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
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        1 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        2 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        3 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        4 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        5 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        7 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        8 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        9 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
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
                    setReceiverUserProfile(holder.recipientProfileImageView, currentMessage.messageSenderId!!)
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
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        1 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        2 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        3 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        4 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        5 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        7 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        8 -> {
                            holder.timeDate.setTextColor(
                                ContextCompat.getColor(
                                    context,
                                    R.color.black
                                )
                            )
                        }

                        9 -> {
                            holder.timeDate.setTextColor(
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

                is SentImageViewHolder -> {
                    setCurrentUserProfile(holder.sentMessageProfile)
                    val timeStampInMillis = currentMessage.timestamp!! as Long
                    val date = Date(timeStampInMillis)
                    val format = SimpleDateFormat("MMM dd, yyyy hh:mm a")
                    val formmattedDate = format.format(date)
                    holder.timeEt.text = formmattedDate
                    setImageMessage(holder.imageSent, currentMessage.imageMessage)
                    holder.imageSent.setOnLongClickListener {
                        showOptionsDialog(currentMessage, position)
                        true
                    }

                    holder.itemView.setOnLongClickListener {
                        showOptionsDialog(currentMessage, position)
                        true
                    }
                }

                is ReceiveImageViewHolder -> {
                    setReceiverMessage(holder.receiveMessageProfile, currentMessage.messageSenderId!!)
                    val timeStampInMillis = currentMessage.timestamp!! as Long
                    val date = Date(timeStampInMillis)
                    val format = SimpleDateFormat("MMM dd, yyyy hh:mm a")
                    val formmattedDate = format.format(date)
                    holder.timeEt.text = formmattedDate
                    setImageMessage(holder.imageReceive, currentMessage.imageMessage)
                    holder.imageReceive.setOnLongClickListener {
                        showOptionsDialog(currentMessage, position)
                        true
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
        return if (currentMessage.messageSenderId == FirebaseAuth.getInstance().currentUser?.uid) {
            if (currentMessage.type == "text") ITEM_SENT else ITEM_SENT_IMAGE
        } else {
            if (currentMessage.type == "text") ITEM_RECEIVE else ITEM_RECEIVE_IMAGE
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
        val recipientProfileImageView =
            itemView.findViewById<CircleImageView>(R.id.receiver_userProfile)
        val timeEtR = itemView.findViewById<TextView>(R.id.timeEtR)
        val timeDate = itemView.findViewById<TextView>(R.id.timeDate)
        val layout = itemView.findViewById<RelativeLayout>(R.id.layout100)

    }

    inner class SentImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageSent = itemView.findViewById<ImageView>(R.id.img_sent_message)
        val timeEt = itemView.findViewById<TextView>(R.id.timeEtImgS)
        val sentMessageProfile =
            itemView.findViewById<CircleImageView>(R.id.current_userProfileImgS)

    }

    inner class ReceiveImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageReceive = itemView.findViewById<ImageView>(R.id.img_received_message)
        val timeEt = itemView.findViewById<TextView>(R.id.timeEtImgR)
        val receiveMessageProfile =
            itemView.findViewById<CircleImageView>(R.id.receiver_userProfile)
    }

    private fun setReceiverMessage(
        recipientProfileImageView: CircleImageView,
        recipientId: String
    ) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("user").child(recipientId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val recipientUser = dataSnapshot.getValue(User::class.java)

                if (recipientUser?.profileImageUrl != null) {
                    Glide.with(context).load(recipientUser.profileImageUrl)
                        .into(recipientProfileImageView)
                } else {
                    Glide.with(context).load(R.drawable.person).into(recipientProfileImageView)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun setImageMessage(imageSent: ImageView, imageUrl: String?) {
        val databaseRef = FirebaseDatabase.getInstance().reference
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        val receiverUid = receiptId
        val senderRoom = receiverUid + senderUid

//        imageSent.setOnClickListener(View.OnClickListener {
//            val intent = Intent(context, ImageActivity::class.java)
//            intent.putExtra("image_url", imageUrl)
//            context.startActivity(intent)
//        })



        if (imageUrl != null) {
                        Glide.with(context).load(imageUrl).into(imageSent)
                        Log.d("MessageImageNew", imageUrl.toString())
                    }


    }


    private fun showOptionsDialog(currentMessage: Group, position: Int) {
        val builder = AlertDialog.Builder(context)
        val copyOption = if (currentMessage.type == "text") "Copy Text" else "Copy Image"
        if (currentMessage.messageSenderId == FirebaseAuth.getInstance().currentUser?.uid) {
            builder.setItems(arrayOf("Delete", copyOption)) { _, which ->
                when (which) {
                    0 -> showDeleteDialog(currentMessage, position, true)
                    1 -> copyMessage(currentMessage)

                }
            }
        } else {
            builder.setItems(arrayOf("Delete", copyOption)) { _, which ->
                when (which) {
                    0 -> showDeleteDialog(currentMessage, position, false)
                    1 -> copyMessage(currentMessage)
                }
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun copyMessage(currentMessage: Group) {
        when (currentMessage.type) {
            "text" -> {
                val clipboardManager =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("text", currentMessage.message)
                clipboardManager.setPrimaryClip(clip)
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
            }

            else -> {

                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clipData = ClipData.newPlainText("Image URL", currentMessage.imageMessage)
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
                    Glide.with(context).load(recipientUser.profileImageUrl).into(sentMessageProfile)
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

    private fun showDeleteDialog(
        currentMessage: Group,
        position: Int,
        deleteForEveryone: Boolean
    ) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Are you sure you want to delete this message?")
            .setPositiveButton("Delete") { dialog, which ->
                if (currentMessage.type == "text") {
                    deleteMessage(currentMessage, position, deleteForEveryone)
                } else deleteImage(currentMessage, position)
            }
            .setNegativeButton("Cancel", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteImage(currentMessage: Group, position: Int) {
        val databaseRef = FirebaseDatabase.getInstance().reference

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        val receiverUid = receiptId
        val senderRoom = receiverUid + senderUid
        val receiverRoom = senderUid + receiverUid

        val query = databaseRef.child("chats")
            .child(senderRoom)
            .child("message")
            .orderByChild("image")
            .equalTo(currentMessage.imageMessage)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    snapshot.ref.removeValue()
                }

                if (senderUid == currentMessage.messageSenderId) {
                    messageList.removeAt(position)
                    notifyItemRemoved(position)

                    // Delete message from receiver's chat room
                    val receiverQuery = databaseRef.child("chats")
                        .child(receiverRoom)
                        .child("message")
                        .orderByChild("image")
                        .equalTo(currentMessage.imageMessage)

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

    private fun deleteMessage(message: Group, position: Int, deleteForEveryone: Boolean) {
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

                if (senderUid == message.messageSenderId) {
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

    private fun setReceiverUserProfile(
        recipientProfileImageView: CircleImageView,
        recipientId: String
    ) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("user").child(recipientId)

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get the User object for the recipient
                val recipientUser = dataSnapshot.getValue(User::class.java)


                // Set the recipient's profile image
                if (recipientUser?.profileImageUrl != null) {
                    Glide.with(context).load(recipientUser.profileImageUrl)
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
}