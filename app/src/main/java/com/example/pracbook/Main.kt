package com.example.pracbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlin.random.Random

class Main : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
//    private lateinit var database: DatabaseReference
    var database = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.logout)
        val textView = findViewById<TextView>(R.id.user_details)
        val user = auth.getCurrentUser();

        val getBookButton = findViewById<Button>(R.id.btn_getbook)
        val bookTextView = findViewById<TextView>(R.id.book)

        if(user == null){
            val intent = Intent(getApplicationContext(), Login::class.java)
            startActivity(intent)
            finish()
        }else{
            textView.setText(user.getEmail())
        }

        button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(getApplicationContext(), Login::class.java)
            startActivity(intent)
            finish()
        }

        getBookButton.setOnClickListener {
            var randomIndex = Random.nextInt(0,10000)
            database.child("Books").child("0").child(randomIndex.toString()).get().addOnSuccessListener {
                bookTextView.setText(it.child("actualTitle").value.toString())
            }.addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "Error getting data",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}