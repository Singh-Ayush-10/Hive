package com.mastercodint.authentaction

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

import com.mastercodint.authentaction.daos.Dao
import com.mastercodint.authentaction.databinding.ActivityPostCreatBinding
import com.mastercodint.authentaction.models.Posts
import com.mastercodint.authentaction.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class postCreat : AppCompatActivity() {
    private lateinit var binding: ActivityPostCreatBinding
    private var image: String = ""
    private var currentUser = Firebase.auth
    private var db = FirebaseFirestore.getInstance()
    private val dao=Dao()
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            dao.uplodeImage(uri,"profile"){
                if(it!=null){
                    image=it
                    binding.imageViewPostCreate.setImageURI(uri)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setTitle("Post")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_post_creat)


        binding.selectImageButton.setOnClickListener {
            openImagePicker()
        }
        binding.SaveData.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                saveDataFireBase()
            }
        }
    }
    private fun saveDataFireBase() {
        currentUser.uid.let {userId->
            db.collection("users").document(userId!!).get()
                .addOnSuccessListener { userSnapshot ->
                    if (userSnapshot.exists()) {
                        val user = userSnapshot.toObject(User::class.java)
                        user?.let {userData->
                            val posts = Posts(
                                userSnapshot.getString("displayName")!!,
                                currentUser.uid!!,
                                binding.editText.text.toString(),
                                image,
                                userSnapshot.getString("imageUrl")!!,
                            )
                            dao.addPost(posts){postId->
                                userData.arrayList.add(postId!!)
                                db.collection("users").document(userId!!).set(userData)
                            }
                        }
                    }
                }.addOnFailureListener {}
            val intent = Intent(this, Jurnallist::class.java)
            startActivity(intent)
        }
    }
    private fun openImagePicker() {
        launcher.launch("image/*")
    }
}