package com.example.chattingapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.ChatActivity
import com.example.chattingapp.ProfileActivity
import com.example.chattingapp.R
import com.example.chattingapp.model.User
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdapter( val context: Context, val userList: List<User>): RecyclerView.Adapter<SearchAdapter.ViewHolder> (){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.user_recyclear_search,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchResult = userList[position]

        holder.searchName.text = searchResult.name
        holder.email.text = searchResult.email
        holder.cardView.startAnimation(AnimationUtils.loadAnimation(context,R.anim.item_animation))

        if (searchResult.profileImageUrl == null) {
            holder.searchProfile.setImageResource(R.drawable.person_3d)
        } else {
            Glide.with(context).load(searchResult.profileImageUrl).into(holder.searchProfile)
        }

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser?.uid

        holder.searchProfile.setOnClickListener{
            val i = Intent(context, ProfileActivity::class.java)
            i.putExtra("uid",searchResult.uid)
            context.startActivity(i)
        }


        holder.itemView.setOnClickListener {

                val i = Intent(context, ChatActivity::class.java)
                i.putExtra("uid", searchResult.uid)
                context.startActivity(i)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val searchProfile = itemView.findViewById<CircleImageView>(R.id.circleImageViewSearch)
        val searchName = itemView.findViewById<TextView>(R.id.etNameSearch)
        val cardView = itemView.findViewById<CardView>(R.id.cardView)
        val email = itemView.findViewById<TextView>(R.id.email)

    }
}



