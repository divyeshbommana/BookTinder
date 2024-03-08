package com.example.pracbook

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.squareup.picasso.Picasso
import java.net.URL
import kotlin.random.Random

class Profile : AppCompatActivity() {

    // Initilizes Firebase authentication extension to auth variable
    val auth = FirebaseAuth.getInstance()
    // Initilizes Firebase realtime database extension to database variable
    var database = Firebase.database.reference

    // When activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Shows the main activity page
        setContentView(R.layout.activity_profile)

        // Gets all elements in main activity page
        // remove the userName or add ability to add userName in register page.
        val button = findViewById<Button>(R.id.logout)
        val textView = findViewById<TextView>(R.id.email)
        val user = auth.getCurrentUser();

        // Bottom of screen to display the user's email
        // Check if user is null, if null takes user back to login page
        // Else sets text view to user's email
        if(user == null){
            val intent = Intent(getApplicationContext(), Login::class.java)
            startActivity(intent)
            finish()
        }else{
            textView.setText(user.getEmail())
        }

        // When "LOGOUT" button is clicked, takes user back to login activity page
        button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(getApplicationContext(), Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}