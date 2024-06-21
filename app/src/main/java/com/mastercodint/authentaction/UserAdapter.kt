package com.mastercodint.authentaction

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.mastercodint.authentaction.models.Posts
import com.mastercodint.authentaction.models.User

class UserAdapter(options: FirestoreRecyclerOptions<User>) :
    FirestoreRecyclerAdapter<User, UserAdapter.UserViewHolder>(options) {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val username: TextView =itemView.findViewById(R.id.User_name)
        val userImage: ImageView =itemView.findViewById(R.id.User_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.design, parent,false))
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {

        holder.username.text=model.displayName
        Glide.with(holder.userImage.context).load(model.imageUrl).circleCrop().into(holder.userImage)
    }


}