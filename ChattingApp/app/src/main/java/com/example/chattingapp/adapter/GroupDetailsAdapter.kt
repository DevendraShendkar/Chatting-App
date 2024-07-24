package com.example.chattingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.R
import com.example.chattingapp.model.User
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

class GroupDetailsAdapter(var context: Context, var list: ArrayList<User>, val groupId:String) : RecyclerView.Adapter<GroupDetailsAdapter.ViewHolder>() {
    private var selectedBoolean:Boolean = false
    private var groupBoolean:Boolean = false
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage:CircleImageView = itemView.findViewById(R.id.circleImageView)
        var name: TextView = itemView.findViewById(R.id.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.selected_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]

        holder.name.text = user.name
        Glide.with(context).load(user.profileImageUrl).into(holder.profileImage)

        val databaseGroup =
            FirebaseDatabase.getInstance().reference.child("Groups").child("GroupMembers")

//        holder.itemView.setOnClickListener {
//
//            selectedBoolean = true
//            val uid = user.uid
//            groupBoolean = true
//            databaseGroup.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (groupBoolean) {
//                        if (snapshot.child(groupId!!).hasChild(uid!!)) {
//                            databaseGroup.child(groupId).child(uid).removeValue()
//                            holder.layer.setBackgroundColor(
//                                ContextCompat.getColor(
//                                    context,
//                                    R.color.white
//                                )
//                            )
//                            holder.name.setTextColor(R.color.favBlack)
//                            groupBoolean = false
//
//                        } else {
//                            databaseGroup.child(groupId).child(uid).setValue(true)
//                            holder.layer.setBackgroundColor(
//                                ContextCompat.getColor(
//                                    context,
//                                    R.color.fav1
//                                )
//                            )
//                            holder.name.setTextColor(R.color.fav)
//                            groupBoolean = false
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
//                }
//            })
//        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}
