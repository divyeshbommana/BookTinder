package com.example.pracbook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pracbook.ItemsViewModel
import com.example.pracbook.R

class BookActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booklib)

        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
        // ArrayList of class ItemsViewModel
        val data = ArrayList<ItemsViewModel>()

        // List of book names
        val bookNames = listOf(
            "Alice's Adventures in Wonderland: Lewis Carroll",
            "The Adventures of Huckleberry Finn: Mark Twain",
            "The Adventures of Tom Sawyer: Mark Twain",
            "Treasure Island: Robert Louis Stevenson",
            "Pride and Prejudice: Jane Austen",
            "Wuthering Heights: Emily Brontë",
            "Jane Eyre: Charlotte Brontë",
            "Moby Dick: Herman Melville",
            "The Scarlet Letter: Nathaniel Hawthorne",
            "Gulliver's Travels: Jonathan Swift",
            "The Pilgrim's Progress: John Bunyan",
            "A Christmas Carol: Charles Dickens",
            "David Copperfield: Charles Dickens",
            "A Tale of Two Cities: Charles Dickens",
            "Little Women: Louisa May Alcott",
            "Great Expectations: Charles Dickens"
        )

        for (i in 0 until bookNames.size) {
            // The first parameter is the image and the second is the title.
            data.add(ItemsViewModel(R.drawable.ic_launcher_background, bookNames[i]))
        }

        // This will pass the ArrayList to our Adapter
        val adapter = CustomAdapter(data)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
    }
}

//package com.example.pracbook
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.database.FirebaseDatabase
//
//class BookActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_booklib)
//
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
//        recyclerView.layoutManager = LinearLayoutManager(this)
//
//        val auth = FirebaseAuth.getInstance()
//        val database = FirebaseDatabase.getInstance().reference
//        val userId = auth.currentUser?.uid
//
//        if (userId != null) {
//            val likedBooksRef = database.child("UserPreferences").child(userId).child("Liked")
//            likedBooksRef.get().addOnSuccessListener { snapshot ->
//                val data = mutableListOf<ItemsViewModel>()
//
//                for (bookSnapshot in snapshot.children) {
//                    val title = bookSnapshot.child("title").value as? String ?: ""
//                    val imageUrl = bookSnapshot.child("image").value as? String ?: ""
//                    data.add(ItemsViewModel(imageUrl, title))
//                }
//
//                val adapter = CustomAdapter(data)
//                recyclerView.adapter = adapter
//            }.addOnFailureListener { exception ->
//                // Handle failure
//            }
//        }
//    }
//}
//
