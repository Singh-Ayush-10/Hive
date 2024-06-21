package com.mastercodint.authentaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Adapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObject
import com.mastercodint.authentaction.databinding.ActivityJurnallistBinding
import com.mastercodint.authentaction.models.Posts
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class Jurnallist : AppCompatActivity(), IpostAdapter {
    lateinit var binding: ActivityJurnallistBinding
    lateinit var auth: FirebaseAuth
    lateinit var adapter: PostAdapter
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle("Hive")

        binding = DataBindingUtil.setContentView(this, R.layout.activity_jurnallist)
        auth = Firebase.auth
        binding.recyclerView.layoutManager = LinearLayoutManager(this)



        val query: Query = db.collection("posts")
        val options = FirestoreRecyclerOptions.Builder<Posts>()
            .setQuery(query, Posts::class.java)
            .setLifecycleOwner(this)
            .build()
        adapter = PostAdapter(options, this)
        binding.recyclerView.adapter = adapter

        binding.creatPost.setOnClickListener {
            val intent = Intent(this, postCreat::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logOutbutton -> {
                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.editProfile -> {
                getUserdata()
            }
        }
        return true
    }

    private fun getUserdata() {
        FirebaseFirestore.getInstance().collection("users")
            .document(auth.currentUser!!.uid)
            .get().addOnSuccessListener { profile->
                if(profile.exists()){
                    val currUserName=profile.getString("displayName")
                    val userImg=profile.getString("imageUrl")
                    val intent=Intent(this,editProfile::class.java).apply {
                        putExtra("USER_NAME",currUserName)
                        putExtra("USER_IMG",userImg)
                    }
                    startActivity(intent)
                    finish()
                }
            }
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }


    override fun delet_post(postId: String) {
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
    }

    override fun no_of_likes(postId: String) {
        val postRef = FirebaseFirestore.getInstance().collection("posts").document(postId)
        GlobalScope.launch {
            val post =
                FirebaseFirestore.getInstance().collection("posts").document(postId).get().await()
                    .toObject(Posts::class.java)
            val isliked = post!!.arrayList.contains(auth.currentUser!!.uid)
            val curruser = auth.currentUser!!.uid
            if (isliked) {
                post.arrayList.remove(curruser)
            } else {
                post.arrayList.add(curruser)
                post.uId = curruser.toString()

            }
            post?.let { updatedPost ->
                postRef.set(updatedPost)
            }
        }
    }
}