package com.mastercodint.authentaction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore
import com.mastercodint.authentaction.daos.Dao
import com.mastercodint.authentaction.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.buttonCreateAccount.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
        binding.buttonLogin.setOnClickListener {
            LoginWithEmailpassword(
                binding.editEmail.text.toString().trim(),
                binding.editPassword.text.toString().trim()
            )
        }

        auth = Firebase.auth
    }

    private fun LoginWithEmailpassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){
                    //Sign in sucess
                    val user = auth.currentUser
                    goToJurnalList()
                } else {
                    Toast.makeText(this, "Authentaction Failed", Toast.LENGTH_LONG).show()
                }
            }
    }
    override fun onStart() {
        super.onStart()
        val currentuser = auth.currentUser
        if (currentuser != null) {
            goToJurnalList()
        }
    }

    private fun goToJurnalList() {
        var intent = Intent(this, Jurnallist::class.java)
        startActivity(intent)
    }
}