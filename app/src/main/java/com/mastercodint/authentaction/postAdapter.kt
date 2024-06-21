package com.mastercodint.authentaction


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


import com.mastercodint.authentaction.models.Posts
import com.mastercodint.authentaction.models.User


class PostAdapter(options: FirestoreRecyclerOptions<Posts>,val listner:IpostAdapter) :
    FirestoreRecyclerAdapter<Posts, PostAdapter.PostViewHolder>(options) {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postText: TextView = itemView.findViewById(R.id.textViewd)
        val imgView: ImageView = itemView.findViewById(R.id.imageViewd)
        val delet_btn:ImageView=itemView.findViewById(R.id.delete_post)
        val like_btn:ImageView=itemView.findViewById(R.id.heart_blank)
        val likes:TextView=itemView.findViewById(R.id.no_of_likes)
        val username: TextView =itemView.findViewById(R.id.User_name)
        val userImage: ImageView =itemView.findViewById(R.id.User_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val viewHolder = PostViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.design, parent,false))
        viewHolder.delet_btn.setOnClickListener{
            listner.delet_post(snapshots.getSnapshot(viewHolder.absoluteAdapterPosition).id)
        }
        viewHolder.like_btn.setOnClickListener {
            listner.no_of_likes(snapshots.getSnapshot(viewHolder.absoluteAdapterPosition).id)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Posts) {
        holder.postText.text = model.text
        holder.likes.text=model.arrayList.size.toString()
        holder.username.text=model.userName.toString()
        Glide.with(holder.imgView.context).load(model.image).into(holder.imgView)
        Glide.with(holder.userImage.context).load(model.userImageView).circleCrop().into(holder.userImage)

        val auth=Firebase.auth
        val currentUserId=auth.currentUser!!.uid
        val isLiked = model.arrayList.contains(currentUserId)
        if (isLiked){
            holder.like_btn.setImageDrawable(ContextCompat.getDrawable(holder.like_btn.context,R.drawable.baseline_liked_24))
        }else{
            holder.like_btn.setImageDrawable(ContextCompat.getDrawable(holder.like_btn.context,R.drawable.baseline_favorite_border_24))
        }
    }

}
interface IpostAdapter{
    fun delet_post(postId:String,)
    fun no_of_likes(postId: String)
}