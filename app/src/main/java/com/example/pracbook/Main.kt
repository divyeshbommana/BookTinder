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

class Main : AppCompatActivity() {

    // Initilizes Firebase authentication extension to auth variable
    val auth = FirebaseAuth.getInstance()
    // Initilizes Firebase realtime database extension to database variable
    var database = Firebase.database.reference

    // When activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Shows the main activity page
        setContentView(R.layout.activity_main)

        // Gets all elements in main activity page
        val button = findViewById<Button>(R.id.logout)
        val textView = findViewById<TextView>(R.id.user_details)
        val user = auth.getCurrentUser();

        val getBookButton = findViewById<Button>(R.id.btn_getbook)
        val bookTextView = findViewById<TextView>(R.id.book)
        val bookCoverImage = findViewById<ImageView>(R.id.book_cover)

        val likeBookButton = findViewById<Button>(R.id.btn_likebook)
        val dislikeBookButton = findViewById<Button>(R.id.btn_dislikebook)
        val profileView = findViewById<Button>(R.id.btn_profile)


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

        // For "profile" button on login page
        profileView.setOnClickListener {
            // If text view is clicked, starts register activity
            val intent = Intent(getApplicationContext(), Profile::class.java)
            startActivity(intent)
            finish()
        }

        // When "LOGOUT" button is clicked, takes user back to login activity page
        button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(getApplicationContext(), Login::class.java)
            startActivity(intent)
            finish()
        }

        // If "GET BOOK" button is clicked
        getBookButton.setOnClickListener {

            // Gets a random integer from 0-9999
            var randomIndex = Random.nextInt(0,10000)

            // Gets data inside index
            database.child("Books").child("0").child(randomIndex.toString()).get().addOnSuccessListener {

                // Sets text view to title of book
                bookTextView.setText(it.child("actualTitle").value.toString())

                // Gets book URL and loads it into image view
                val url = it.child("img").value.toString()
                Picasso.with(this).load(url).into(bookCoverImage)

            }.addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "Error getting data",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        // If "LIKE" button is clicked
        likeBookButton.setOnClickListener {

            // Gets the data of the user in UserPreferences based on user's UID
            // If new user, creates a new element in data base
            val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Liked")

            // Pushes liked book's title into "Liked" subsection in database
            data.push().setValue(bookTextView.getText().toString()).addOnSuccessListener {
                Toast.makeText(
                    baseContext,
                    "Added book to liked",
                    Toast.LENGTH_SHORT,
                ).show()
            }.addOnFailureListener{
                Toast.makeText(
                    baseContext,
                    "Unable to add book to liked",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        // If "DISLIKE" button is clicked
        dislikeBookButton.setOnClickListener {

            // Gets the data of the user in UserPreferences based on user's UID
            // If new user, creates a new element in data base
            val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Disliked")

            // Pushes disliked book's title into "Disliked" subsection in database
            data.push().setValue(bookTextView.getText().toString()).addOnSuccessListener {
                Toast.makeText(
                    baseContext,
                    "Added book to disliked",
                    Toast.LENGTH_SHORT,
                ).show()
            }.addOnFailureListener{
                Toast.makeText(
                    baseContext,
                    "Unable to add book to disliked",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }
}