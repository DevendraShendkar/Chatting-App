package com.example.chattingapp.adapter

import Message
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.ProfileActivity
import com.example.chattingapp.R
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Date

class AddFamilyAdapter(val context : Context, var list: ArrayList<User>):RecyclerView.Adapter<AddFamilyAdapter.FamilyViewHolder>() {
    private var selectedBoolean:Boolean = false
    private var groupBoolean:Boolean = false
    class FamilyViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val name: TextView =itemView.findViewById(R.id.etName)
        val profileImg: CircleImageView = itemView.findViewById(R.id.circleImageView)
        val latestMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
        val lastTime: TextView = itemView.findViewById(R.id.lastTime)
        val cardView: CardView = itemView.findViewById(R.id.cardView)
        val messageCount: TextView = itemView.findViewById(R.id.messageCount)
        val messageTick: ImageView = itemView.findViewById(R.id.messageTick)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FamilyViewHolder {

        val view: View = LayoutInflater.from(context).inflate(R.layout.user_recycler,parent,false)
        return FamilyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: FamilyViewHolder, position: Int) {
        val currentUser = list[position]
        val uid = currentUser.uid.toString()

        val database1 = FirebaseDatabase.getInstance().reference.child("messageCount")
        database1.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val currentCount = snapshot.child(uid).child(FirebaseAuth.getInstance().uid!!).getValue(Int::class.java) ?: 0
                if (!(currentCount == 0)) {
                    holder.messageCount.text = currentCount.toString()
                    holder.messageCount.isVisible = true
                }else holder.messageCount.isVisible = false
                Log.d("currentcountttt",currentCount.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


//        holder.cardView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.item_animation1))

        val database = FirebaseDatabase.getInstance().reference.child("user").child(uid)
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)

                if (currentUser.profileImageUrl == null) {

                } else {
                    Glide.with(context).load(currentUser.profileImageUrl).into(holder.profileImg)
                }

                val databaseRef = FirebaseDatabase.getInstance().reference
                val roomId = senderUid + uid
                val messageRef = databaseRef.child("chats").child(roomId).child("message")
                val query = messageRef.orderByChild("timestamp").limitToLast(1)

                query.addValueEventListener(object : ValueEventListener {
                    @SuppressLint("SuspiciousIndentation")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var isMessageForCurrentUser = false
                        var messageCount = 0 // Initialize the message count to 0
                        for (data in snapshot.children) {
                            val message = data.getValue(Message::class.java)
                            if (message != null ) {
                                holder.latestMessage.text = message.message
                                val timeStampInMillis = message.timestamp as Long
                                val date = Date(timeStampInMillis)
                                val format = SimpleDateFormat("HH:mm a")
                                val formattedDate = format.format(date)
                                holder.lastTime.text = formattedDate
                                val timeStamp = ServerValue.TIMESTAMP
                                isMessageForCurrentUser = true
                            }

                        }
                        if (!isMessageForCurrentUser) {
                            holder.latestMessage.text = ""
                            holder.lastTime.text = ""
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("TAG", "Failed to read value.", error.toException())
                    }
                })


            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(ContentValues.TAG, "onCancelled", error.toException())
            }
        })




        if (currentUser.name == null) {
            holder.name.text = currentUser.userName
        } else {
            holder.name.text = currentUser.name
        }

        holder.profileImg.setOnClickListener {
            val i = Intent(context, ProfileActivity::class.java)
            i.putExtra("name", currentUser.userName)
            i.putExtra("uid", currentUser.uid)
            context.startActivity(i)
        }
        
        setFamily(holder,currentUser)

        val databaseGroup = FirebaseDatabase.getInstance().reference.child("Family").child("FamilyMembers")
        holder.itemView.setOnClickListener {
            selectedBoolean = true
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            groupBoolean = true
            databaseGroup.addValueEventListener(object : ValueEventListener {
                @SuppressLint("ResourceAsColor")
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(groupBoolean){
                        if(snapshot.child(uid!!).hasChild(currentUser.uid!!)){
                            databaseGroup.child(uid).child(currentUser.uid!!).removeValue()
//                            holder.layer.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
//                            holder.name.setTextColor(R.color.favBlack)
                            holder.messageTick.isVisible = false
                            groupBoolean = false

                        }else{
                            databaseGroup.child(uid).child(currentUser.uid!!).setValue(true)
//                            holder.layer.setBackgroundColor(ContextCompat.getColor(context, R.color.fav1))
//                            holder.name.setTextColor(R.color.fav)
                            holder.messageTick.isVisible = true
                            groupBoolean = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
        }

    }

    private fun setFamily(holder: FamilyViewHolder, currentUser: User) {
        val database = FirebaseDatabase.getInstance().reference.child("Family").child("FamilyMembers")

        val uid = FirebaseAuth.getInstance().currentUser?.uid!!
        database.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.child(uid).hasChild(currentUser.uid!!)){
                    val likeCountFirebase = snapshot.child(uid).childrenCount
                    holder.messageTick.isVisible = true
                }else{
                    val likeCountFirebase = snapshot.child(uid).childrenCount
                    holder.messageTick.isVisible = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}