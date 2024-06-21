package com.mastercodint.authentaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.mastercodint.authentaction.databinding.ActivityPostDetailsBinding
import com.mastercodint.authentaction.models.Posts

class PostDetails : AppCompatActivity() {
    lateinit var binding: ActivityPostDetailsBinding
    lateinit var auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle("post")
        auth=Firebase.auth
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_details)
        val postId = intent.getStringExtra("postId")
        FirebaseFirestore.getInstance().collection("posts").document(postId!!).get()
            .addOnSuccessListener { snapShot ->
                if (snapShot.exists()) {
                    val post = snapShot.toObject(Posts::class.java)
                    binding.UserName.text = post!!.userName.toString()
                    binding.noOfLikes.text = post!!.arrayList.size.toString()
                    binding.caption.text = post!!.text.toString()
                    Glide.with(binding.UserImg).load(post.userImageView).circleCrop().into(binding.UserImg)
                    Glide.with(binding.postImg).load(post.image).into(binding.postImg)
                    if(post.arrayList.size>0){
                        binding.heartBlank.setImageResource(R.drawable.baseline_liked_24)
                    }
                }

            }

        binding.deletePost.setOnClickListener {
            deletePost(postId)
        }
    }

    private fun deletePost(postId: String) {
        FirebaseFirestore.getInstance().collection("posts").document(postId).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val curr_post_user_id = doc.getString("uid")
                    if (curr_post_user_id == auth.currentUser?.uid) {
                        // User owns the post, proceed with deletion
                        FirebaseFirestore.getInstance().collection("posts")
                            .document(postId).delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    this,
                                    "Post deleted successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    } else {
                        Toast.makeText(this, "Not Your Post", Toast.LENGTH_SHORT).show()
                    }

                }
            }
        val intent= Intent(this,Jurnallist::class.java)
        startActivity(intent)
        finish()
    }
}