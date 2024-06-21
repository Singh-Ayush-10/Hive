package com.mastercodint.authentaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.mastercodint.authentaction.daos.Dao
import com.mastercodint.authentaction.databinding.ActivitySignUpBinding
import com.mastercodint.authentaction.models.User

class SignUp : AppCompatActivity() {
    lateinit var binding : ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    val dao = Dao()
    var image: String = ""
    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            dao.uplodeImage(uri, "profile") {
                if (it != null) {
                    image = it
                    binding.userImg.setImageURI(uri)
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_sign_up)

        binding.addProfile.setOnClickListener {
            openImagePicker()
        }
        auth=Firebase.auth
        binding.buttonSignUp.setOnClickListener {
            createUser()
        }
    }

    private fun openImagePicker() {
        launcher.launch("image/*")
    }

    private fun createUser() {
        val email=binding.editTextEmail.text.toString()
        val password=binding.editTextPassword.text.toString()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAGY", "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                     // If sign in fails, display a message to the user.
                    Log.w("TAGY", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
    }
    private fun updateUI(fireBaseUser: FirebaseUser?) {
        if(fireBaseUser!=null){
            val user = User(fireBaseUser.uid,binding.editTextUsername.text.toString(),image)
            val usersDao= Dao()
            usersDao.addUser(user)
        }
        val intent= Intent(this,Jurnallist::class.java)
        startActivity(intent)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun reload() {

    }
}