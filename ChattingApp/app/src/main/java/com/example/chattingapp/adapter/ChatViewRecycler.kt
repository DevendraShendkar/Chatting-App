package com.example.chattingapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.ChatActivity
import com.example.chattingapp.R
import com.example.chattingapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class ChatViewRecycler(var context:Context, var list: ArrayList<User>):RecyclerView.Adapter<ChatViewRecycler.ViewHolder>() {

    open class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        var name:TextView = itemView.findViewById(R.id.name)
        var profileImage:CircleImageView = itemView.findViewById(R.id.circleImageView)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewRecycler.ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.online_item,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewRecycler.ViewHolder, position: Int) {
        var list = list[position]

        holder.itemView.setOnClickListener {
            val i = Intent(context, ChatActivity::class.java)
            i.putExtra("uid",list.uid)
            context.startActivity(i)
        }
        holder.name.text = list.name
        Glide.with(context).load(list.profileImageUrl).into(holder.profileImage)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}