package com.example.pracbook

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
//import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
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
import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class Main : AppCompatActivity() {

    // Initilizes Firebase authentication extension to auth variable
    val auth = FirebaseAuth.getInstance()
    // Initilizes Firebase realtime database extension to database variable
    var database = Firebase.database.reference

    //code for flip card
    private lateinit var bookInfoCard: CardView
    private var isBookCoverVisible = true
    private lateinit var bookCoverImage: ImageView

    // used for swipe mechanism.
    // https://www.tutorialspoint.com/how-to-handle-swipe-gestures-in-kotlin

    // When activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "onCreate() in MainActivity is called")
        // Shows the main activity page
        setContentView(R.layout.activity_main)

        val user = auth.getCurrentUser()
        val bookTextView = findViewById<TextView>(R.id.book)
        bookCoverImage = findViewById<ImageView>(R.id.book_cover)
        val profileButton = findViewById<Button>(R.id.btn_profile)
        val libraryButton = findViewById<Button>(R.id.btn_library)

        val bookTitleTextView = findViewById<TextView>(R.id.book_title)
        val bookDescription = findViewById<TextView>(R.id.book_description)
        // flip card pointer.
        bookInfoCard = findViewById(R.id.book_info_card)

        //we set teh first book as the starting book.
        val randomVal = Random.nextInt(0,10000)
        database.child("Books").child("0").child(randomVal.toString()).get().addOnSuccessListener {
//            // Sets text view to title of book
//            bookTextView.setText(it.child("actualTitle").value.toString())
            //sets the text on the card flipped tp the title of the book.
            bookTitleTextView.setText(it.child("actualTitle").value.toString())
            // sets the book description
            bookDescription.setText(it.child("actualGenre").value.toString())
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
                data.push().setValue(bookTitleTextView.getText().toString()).addOnSuccessListener {//chnaged this from bookTextView to bookTitleTextView
                    Toast.makeText(
                        baseContext,
                        "Added book to disliked",
                        Toast.LENGTH_SHORT,
                    ).show()

                    //displays a new books after you swipe.
                    val randomIndex = Random.nextInt(0,10000)
                    // Gets data inside index
                    database.child("Books").child("0").child(randomIndex.toString()).get().addOnSuccessListener {
//                        // Sets text view to title of book
//                        bookTextView.setText(it.child("actualTitle").value.toString())
                        //sets the text on the card flipped tp the title of the book.
                        bookTitleTextView.setText(it.child("actualTitle").value.toString())
                        // sets the book description
                        bookDescription.setText(it.child("actualGenre").value.toString())
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
                data.push().setValue(bookTitleTextView.getText().toString()).addOnSuccessListener {
                    Toast.makeText(
                        baseContext,
                        "Added book to liked",
                        Toast.LENGTH_SHORT,
                    ).show()

                    //dispalys a new books
                    val randomIndex = Random.nextInt(0,10000)
                    // Gets data inside index
                    database.child("Books").child("0").child(randomIndex.toString()).get().addOnSuccessListener {
//                        // Sets text view to title of book
//                        bookTextView.setText(it.child("actualTitle").value.toString())
                        //sets the text on the card flipped tp the title of the book.
                        bookTitleTextView.setText(it.child("actualTitle").value.toString())
                        // sets the book description
                        bookDescription.setText(it.child("actualGenre").value.toString())
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
//                    // Sets text view to title of book
//                    bookTextView.setText(it.child("actualTitle").value.toString())
                    //sets the text on the card flipped tp the title of the book.
                    bookTitleTextView.setText(it.child("actualTitle").value.toString())
                    // sets the book description
                    bookDescription.setText(it.child("actualGenre").value.toString())
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

            override fun onClick() {
                super.onClick()
                // When user taps on the bookCoverImage, it should flip to display bookInformationCard. How do I this?
                Toast.makeText(this@Main, "User Tapped on this on book image", Toast.LENGTH_SHORT)
                    .show()
                //method to flip card.
                flipViews()
            }
        })

        // code for book info card.
        bookInfoCard.setOnTouchListener(object : OnSwipeTouchListener(this@Main) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                // Handle left swipe action on bookInformationCard
                val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Disliked")
                // Pushes disliked book's title into "Disliked" subsection in database
                //bookTextView to bookTitleTextView
                data.push().setValue(bookTitleTextView.getText().toString()).addOnSuccessListener {
                    Toast.makeText(
                        baseContext,
                        "Added book to disliked",
                        Toast.LENGTH_SHORT,
                    ).show()

                    //displays a new books after you swipe.
                    val randomIndex = Random.nextInt(0,10000)
                    // Gets data inside index
                    database.child("Books").child("0").child(randomIndex.toString()).get().addOnSuccessListener {
//                        // Sets text view to title of book
//                        bookTextView.setText(it.child("actualTitle").value.toString())
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

                flipViews()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                // Handle right swipe action on bookInformationCard (if needed)

                val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Liked")
                // Pushes liked book's title into "Liked" subsection in database
                // bookTextView to bookTitleTextView
                data.push().setValue(bookTitleTextView.getText().toString()).addOnSuccessListener {
                    Toast.makeText(
                        baseContext,
                        "Added book to liked",
                        Toast.LENGTH_SHORT,
                    ).show()

                    //dispalys a new books
                    val randomIndex = Random.nextInt(0,10000)
                    // Gets data inside index
                    database.child("Books").child("0").child(randomIndex.toString()).get().addOnSuccessListener {
//                        // Sets text view to title of book
//                        bookTextView.setText(it.child("actualTitle").value.toString())
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

                flipViews()
            }

            override fun onSwipeUp() {
                super.onSwipeUp()
                // Handle up swipe action on bookInformationCard (if needed)

                val randomIndex = Random.nextInt(0,10000)
                // Gets data inside index
                database.child("Books").child("0").child(randomIndex.toString()).get().addOnSuccessListener {
//                    // Sets text view to title of book
//                    bookTextView.setText(it.child("actualTitle").value.toString())
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
                //flips once its clicked on.
                flipViews()
            }

            override fun onClick() {
                super.onClick()
                // When user taps on the bookCoverImage, it should flip to display bookInformationCard. How do I this?
                Toast.makeText(this@Main, "User Tapped on this on the book info card", Toast.LENGTH_SHORT)
                    .show()
                //method to flip card.I want this to display a new
                flipViews()
            }
        })

        profileButton.setOnClickListener {
            val intent = Intent(this, Profile::class.java)
            startActivity(intent)
        }

        libraryButton.setOnClickListener {
            val intent = Intent(this, BookActivity::class.java)
            startActivity(intent)
        }
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


    //New code to flip bookCoverImage to bookInformationCards
    private fun flipViews() {
        val fromView: View
        val toView: View

        if (isBookCoverVisible) {
            fromView = bookCoverImage
            toView = bookInfoCard
        } else {
            fromView = bookInfoCard
            toView = bookCoverImage
        }

        isBookCoverVisible = !isBookCoverVisible

        val flipOut = ObjectAnimator.ofFloat(fromView, "rotationY", 0f, 90f).apply {
            duration = 500
        }
        val flipIn = ObjectAnimator.ofFloat(toView, "rotationY", -90f, 0f).apply {
            duration = 500
        }

        flipOut.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                fromView.isVisible = false
                flipIn.start()
                toView.isVisible = true
            }
        })
        flipOut.start()
    }
}