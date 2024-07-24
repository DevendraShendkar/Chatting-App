package com.example.chattingapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.R
import com.example.chattingapp.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class GroupAdapter(private val context: Context, private val friendsList: ArrayList<User>, val groupId:String) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {
    private var selectedBoolean:Boolean = false
    private var groupBoolean:Boolean = false
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.friends_item_recyclear, parent, false)
        return GroupViewHolder(view)
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val currentUser = friendsList[position]
        holder.name.text = currentUser.name

        val databaseGroup = FirebaseDatabase.getInstance().reference.child("Groups").child("GroupMembers")

        holder.itemView.setOnClickListener {

            selectedBoolean = true
            val uid = currentUser.uid
            groupBoolean = true
            databaseGroup.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(groupBoolean){
                        if(snapshot.child(groupId!!).hasChild(uid!!)){
                            databaseGroup.child(groupId).child(uid).removeValue()
                            holder.layer.setBackgroundColor(ContextCompat.getColor(context,
                                R.color.white
                            ))
                            holder.name.setTextColor(R.color.favBlack)
                            groupBoolean = false

                        }else{
                            databaseGroup.child(groupId).child(uid).setValue(true)
                            holder.layer.setBackgroundColor(ContextCompat.getColor(context,
                                R.color.fav1
                            ))
                            holder.name.setTextColor(R.color.fav)
                            groupBoolean = false
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        }

        Glide.with(context).load(currentUser.profileImageUrl).into(holder.profileImg)

    }

    class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImg: CircleImageView = itemView.findViewById(R.id.circleImageView)
        val name: TextView = itemView.findViewById(R.id.etName)
        val layer: ConstraintLayout = itemView.findViewById(R.id.layer)
    }
}
