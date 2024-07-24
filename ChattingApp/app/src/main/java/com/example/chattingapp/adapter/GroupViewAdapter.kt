package com.example.chattingapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.GroupChatActivity
import com.example.chattingapp.R
import com.example.chattingapp.model.Group
import de.hdodenhof.circleimageview.CircleImageView

class GroupViewAdapter(var context: Context, var list:ArrayList<Group>):RecyclerView.Adapter<GroupViewAdapter.GroupViewHolder>() {

    class GroupViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val groupImage:CircleImageView = itemView.findViewById(R.id.circleImageView)
        val groupName:TextView = itemView.findViewById(R.id.etName)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_recycler,parent,false)
        return GroupViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = list[position]
        holder.groupName.text = group.groupName.toString()

        Log.d("listishereagain",group.groupName.toString())
        Glide.with(context).load(group.groupImage).into(holder.groupImage)

        holder.itemView.setOnClickListener {
            val i = Intent(context, GroupChatActivity::class.java)
            i.putExtra("uid",group.groupId)
            context.startActivity(i)
        }
    }
}