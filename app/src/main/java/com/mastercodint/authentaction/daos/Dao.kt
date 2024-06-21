package com.mastercodint.authentaction.daos

import android.net.Uri
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.mastercodint.authentaction.models.Posts
import com.mastercodint.authentaction.models.User
import com.mastercodint.authentaction.postCreat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.w3c.dom.Document
import java.util.UUID

class Dao {
    val db = Firebase.firestore
    val userCollection = db.collection("users")
    val postsCollectionRef = db.collection("posts")
    fun addUser(user: User?) {
        GlobalScope.launch(Dispatchers.IO) {
            user?.let {
                userCollection.document(user.uid).set(it)
            }
        }
    }

    fun uplodeImage(uri: Uri, FolderName: String, callback: (String?) -> Unit) {
        var imgUrl: String? = null
        FirebaseStorage.getInstance().getReference(FolderName).child((UUID.randomUUID().toString()))
            .putFile(uri).addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {
                    imgUrl = it.toString()
                    callback(imgUrl)
                }
            }
    }

    fun addPost(posts: Posts?, callback: (String?) -> Unit) {
        val newPostRef = postsCollectionRef.document()
        val postId = newPostRef.id
        GlobalScope.launch(Dispatchers.IO) {
            posts?.let {
                newPostRef.set(posts).addOnSuccessListener {
                    callback(postId)
                }
            }
        }
    }

}

