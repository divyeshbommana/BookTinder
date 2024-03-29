package com.example.pracbook

import android.content.Intent
//import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.auth
//import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.squareup.picasso.Picasso
//import java.net.URL
import kotlin.random.Random
//import android.widget.RelativeLayout
import com.example.pracbook.OnSwipeTouchListener

class Main : AppCompatActivity() {

    // Initilizes Firebase authentication extension to auth variable
    val auth = FirebaseAuth.getInstance()
    // Initilizes Firebase realtime database extension to database variable
    var database = Firebase.database.reference

    // used for swip machanism.
    // https://www.tutorialspoint.com/how-to-handle-swipe-gestures-in-kotlin
//    private lateinit var layout: RelativeLayout

    // When activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "onCreate() in MainActivity is called")
        // Shows the main activity page
        setContentView(R.layout.activity_main)

        // Gets all elements in main activity page
//        val button = findViewById<Button>(R.id.logout)
//        val textView = findViewById<TextView>(R.id.user_details)
        val user = auth.getCurrentUser()

//        val getBookButton = findViewById<Button>(R.id.btn_getbook)
        val bookTextView = findViewById<TextView>(R.id.book)
        val bookCoverImage = findViewById<ImageView>(R.id.book_cover)

//        val likeBookButton = findViewById<Button>(R.id.btn_likebook)
//        val dislikeBookButton = findViewById<Button>(R.id.btn_dislikebook)
        val profileButton = findViewById<Button>(R.id.btn_profile)

        //we set teh first book as the starting book.
        val randomVal = Random.nextInt(0,10000)
        database.child("Books").child("0").child(randomVal.toString()).get().addOnSuccessListener {
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

        // code for swipe mechanism. Class onSwipeTouchListener is used for this.
        // Which every book is displyed, we see that and can user the features of swipeUp, swipeLeft, and swipeRight
        bookCoverImage.setOnTouchListener(object : OnSwipeTouchListener(this@Main) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                //Dislike Book code
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

                    //displays a new books after you swipe.
                    val randomIndex = Random.nextInt(0,10000)
                    // Gets data inside index
                    database.child("Books").child("0").child(randomIndex.toString()).get().addOnSuccessListener {
                        // Sets text view to title of book
                        bookTextView.setText(it.child("actualTitle").value.toString())
                        // Gets book URL and loads it into image view
                        val url = it.child("img").value.toString()
                        Picasso.with(this@Main).load(url).into(bookCoverImage)
                    }


                }.addOnFailureListener{
                    Toast.makeText(
                        baseContext,
                        "Unable to add book to disliked",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

            // Liked Book code
            override fun onSwipeRight() {
                super.onSwipeRight()
                val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Liked")
                // Pushes liked book's title into "Liked" subsection in database
                data.push().setValue(bookTextView.getText().toString()).addOnSuccessListener {
                    Toast.makeText(
                        baseContext,
                        "Added book to liked",
                        Toast.LENGTH_SHORT,
                    ).show()

                    //dispalys a new books
                    val randomIndex = Random.nextInt(0,10000)
                    // Gets data inside index
                    database.child("Books").child("0").child(randomIndex.toString()).get().addOnSuccessListener {
                        // Sets text view to title of book
                        bookTextView.setText(it.child("actualTitle").value.toString())
                        // Gets book URL and loads it into image view
                        val url = it.child("img").value.toString()
                        Picasso.with(this@Main).load(url).into(bookCoverImage)
                    }
                }.addOnFailureListener{
                    Toast.makeText(
                        baseContext,
                        "Unable to add book to liked",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

            // getBook method
            override fun onSwipeUp() {
                super.onSwipeUp()
                // Gets a random integer from 0-9999
                val randomIndex = Random.nextInt(0,10000)
                // Gets data inside index
                database.child("Books").child("0").child(randomIndex.toString()).get().addOnSuccessListener {
                    // Sets text view to title of book
                    bookTextView.setText(it.child("actualTitle").value.toString())
                    // Gets book URL and loads it into image view
                    val url = it.child("img").value.toString()
                    Picasso.with(this@Main).load(url).into(bookCoverImage)
                }.addOnFailureListener {
                    Toast.makeText(
                        baseContext,
                        "Error getting data",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }

//            override fun onSingleTapUp() {
////                TODO
//                super.onSingleTapUp()
//            }
        })


        // Bottom of screen to display the user's email
        // Check if user is null, if null takes user back to login page
        // Else sets text view to user's email
//        if(user == null){
//            val intent = Intent(getApplicationContext(), Login::class.java)
//            startActivity(intent)
//            finish()
//        }else{
//            textView.setText(user.getEmail())
//        }

        profileButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

//        // When "LOGOUT" button is clicked, takes user back to login activity page
//        button.setOnClickListener {
//            FirebaseAuth.getInstance().signOut()
//            val intent = Intent(getApplicationContext(), Login::class.java)
//            startActivity(intent)
//            finish()
//        }

//        // If "GET BOOK" button is clicked
//        getBookButton.setOnClickListener {
//            // Gets a random integer from 0-9999
//            val randomIndex = Random.nextInt(0,10000)
//            // Gets data inside index
//            database.child("Books").child("0").child(randomIndex.toString()).get().addOnSuccessListener {
//                // Sets text view to title of book
//                bookTextView.setText(it.child("actualTitle").value.toString())
//                // Gets book URL and loads it into image view
//                val url = it.child("img").value.toString()
//                Picasso.with(this).load(url).into(bookCoverImage)
//            }.addOnFailureListener {
//                Toast.makeText(
//                    baseContext,
//                    "Error getting data",
//                    Toast.LENGTH_SHORT,
//                ).show()
//            }
//        }

//        // If "LIKE" button is clicked
//        likeBookButton.setOnClickListener {
//
//            // Gets the data of the user in UserPreferences based on user's UID
//            // If new user, creates a new element in data base
//            val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Liked")
//
//            // Pushes liked book's title into "Liked" subsection in database
//            data.push().setValue(bookTextView.getText().toString()).addOnSuccessListener {
//                Toast.makeText(
//                    baseContext,
//                    "Added book to liked",
//                    Toast.LENGTH_SHORT,
//                ).show()
//            }.addOnFailureListener{
//                Toast.makeText(
//                    baseContext,
//                    "Unable to add book to liked",
//                    Toast.LENGTH_SHORT,
//                ).show()
//            }
//        }

//        // If "DISLIKE" button is clicked
//        dislikeBookButton.setOnClickListener {
//
//            // Gets the data of the user in UserPreferences based on user's UID
//            // If new user, creates a new element in data base
//            val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Disliked")
//
//            // Pushes disliked book's title into "Disliked" subsection in database
//            data.push().setValue(bookTextView.getText().toString()).addOnSuccessListener {
//                Toast.makeText(
//                    baseContext,
//                    "Added book to disliked",
//                    Toast.LENGTH_SHORT,
//                ).show()
//            }.addOnFailureListener{
//                Toast.makeText(
//                    baseContext,
//                    "Unable to add book to disliked",
//                    Toast.LENGTH_SHORT,
//                ).show()
//            }
//        }


    }
    // When activity is resumed
    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "onResume() in MainActivity is called")
    }

    // When activity is paused
    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "onPause() in MainActivity is called")
    }

    // When activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "onDestroy() in MainActivity is called")
    }
}