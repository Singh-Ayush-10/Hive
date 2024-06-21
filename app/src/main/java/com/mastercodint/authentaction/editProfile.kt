package com.mastercodint.authentaction

import ImageAdapter
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mastercodint.authentaction.daos.Dao
import com.mastercodint.authentaction.databinding.ActivityEditProfileBinding
import com.mastercodint.authentaction.models.Posts
import com.mastercodint.authentaction.models.User
class postinfo(val postId: String, val imageUrl: String)
class editProfile : AppCompatActivity() {
    lateinit var binding: ActivityEditProfileBinding
    lateinit var auth: FirebaseAuth
    lateinit var userName:String
    lateinit var userImage:String
    var postID = mutableListOf<String>()
    var postsImg = mutableListOf<postinfo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle("Profile")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
         userName = intent.getStringExtra("USER_NAME").toString()
         userImage = intent.getStringExtra("USER_IMG").toString()
        binding.UserName.text=userName.toString()
        Glide.with(binding.userImg).load(userImage).into(binding.userImg)
        auth=Firebase.auth

        PutPostID()


        binding.userImg.setOnClickListener {
            launcher.launch("image/*")
        }
        binding.buttonSignUp.setOnClickListener {
            edit_profile()
        }

    }
    fun PutPostID() {
        FirebaseFirestore.getInstance().collection("users").document(auth.currentUser!!.uid)
            .get().addOnSuccessListener {snapShot->
                if(snapShot.exists()){
                    val userData = snapShot.toObject(User::class.java)
                    userData?.let {
                        for (postId in userData.arrayList){
                            postID.add(postId)
                        }
                    }
                }
                displayPostImg()
        }
    }
    private fun displayPostImg() {

        val recyclerView: RecyclerView = findViewById(R.id.recyclearView)
        val layoutManager = GridLayoutManager(this, 3) // Assuming you want 3 columns
        recyclerView.layoutManager = layoutManager
        for ( postid in postID){
            FirebaseFirestore.getInstance().collection("posts").document(postid).get().addOnSuccessListener {snapShot->
                if(snapShot.exists()){
                    val posts=snapShot.toObject(Posts::class.java)
                    val postinfo=postinfo(postid,posts!!.image.toString())
                    postsImg.add(postinfo)
                }
                val adapter = ImageAdapter(this,postsImg) // Assuming postImagesList contains the list of image URLs
                recyclerView.adapter = adapter
            }
        }
    }
    private fun edit_profile() {
        val docref=FirebaseFirestore.getInstance().collection("users").document(auth.currentUser!!.uid)
        val updates = hashMapOf<String, Any>(
            "imageUrl" to userImage.toString()
        )
        docref.set(updates, SetOptions.merge())
        val docRef =
            FirebaseFirestore.getInstance().collection("users").document(auth.currentUser!!.uid)

        docRef.get().addOnSuccessListener { userSnapshot ->
            if (userSnapshot.exists()) {
                val userData = userSnapshot.toObject(User::class.java)
                userData?.let { userData ->
                    for (postId in userData.arrayList) {
                        val postRef =
                            FirebaseFirestore.getInstance().collection("posts").document(postId)
                        postRef.get().addOnSuccessListener { postSnapshot ->
                            if (postSnapshot.exists()) {
                                val post = postSnapshot.toObject(Posts::class.java)
                                post?.let { post ->
                                    post.userImageView = userImage.toString()
                                    // Update the post document
                                    postRef.set(post)
                                }
                            }
                        }
                    }
                }
            }
        }
        val intent=Intent(this,Jurnallist::class.java)
        startActivity(intent)
        finish()
    }
    val dao = Dao()
    val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            dao.uplodeImage(uri, "profile") {
                if (it != null) {
                    userImage = it
                    binding.userImg.setImageURI(uri)
                }
            }
        }
    }
}