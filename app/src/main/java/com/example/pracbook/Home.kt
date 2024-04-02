package com.example.pracbook

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlin.random.Random


fun getRecIndex(): Int {
    // Function body
    // Gets a random integer from 0-9999
    return Random.nextInt(0, 100)
}
class Home : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initilizes Firebase authentication extension to auth variable
        val auth = FirebaseAuth.getInstance()
        // Initilizes Firebase realtime database extension to database variable
        var database = Firebase.database.reference


        val button = view.findViewById<Button>(R.id.logout)
//        val textViewUserDetails = view.findViewById<TextView>(R.id.user_details)
        val getBookButton = view.findViewById<Button>(R.id.btn_getbook)
        val likeBookButton = view.findViewById<Button>(R.id.btn_likebook)
        val dislikeBookButton = view.findViewById<Button>(R.id.btn_dislikebook)
        val bookTextView = view.findViewById<TextView>(R.id.book)
        val bookCoverImage = view.findViewById<ImageView>(R.id.book_cover)


        val user = auth.currentUser
        if (user == null) {
            val intent = Intent(requireContext(), Login::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        // When "LOGOUT" button is clicked, takes user back to login activity page
        button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent( requireContext(),Login::class.java)
            startActivity(intent)
            requireActivity().finish()
        }



        getBookButton.setOnClickListener {

            var viewedBooks = mutableListOf<String>()

            database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Viewed").get().addOnSuccessListener {
                for (x in it.children){
                    viewedBooks.add(x.value.toString())
                }
            }

            //Gets the liked data
            database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Liked").get().addOnSuccessListener {
                if(it.childrenCount < 5){
                    // Gets data inside index
                    database.child("Books").child("0").child(getRecIndex().toString()).get().addOnSuccessListener {

                        val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Viewed")

                        // Pushes viewed book's title into "Viewed" subsection in database
                        data.push().setValue(it.child("actualTitle").value.toString()).addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Added book to viewed",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }.addOnFailureListener{
                            Toast.makeText(
                                requireContext(),
                                "Unable to add book to viewed",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }

                        // Sets text view to title of book
                        bookTextView.setText(it.child("actualTitle").value.toString())

                        // Gets book URL and loads it into image view
                        val url = it.child("img").value.toString()
                        Picasso.with(requireContext()).load(url).into(bookCoverImage)

                    }.addOnFailureListener {
                        Toast.makeText(
                            requireContext(),
                            "Error getting data",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }else{
                    var likedBooks = mutableListOf<String>()
                    for (x in it.children){
                        likedBooks.add(x.value.toString())
                    }
                    println("There are more than 5 books in like and they are: $likedBooks")
                    GlobalScope.launch(Dispatchers.IO) {
                        val res = callApiRecommend(likedBooks, viewedBooks)
                        val recommendedBook = res.split("{\"data\":[[1,")[1].split("]]}")[0].replace("[","").replace("]","").split(",")[0]

                        database.child("Books").child("0").child(recommendedBook.toString()).get().addOnSuccessListener {
                            val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Viewed")

                            // Pushes viewed book's title into "Viewed" subsection in database
                            data.push().setValue(it.child("actualTitle").value.toString()).addOnSuccessListener {
                                Toast.makeText(
                                    requireContext(),
                                    "Added book to viewed",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }.addOnFailureListener{
                                Toast.makeText(
                                    requireContext(),
                                    "Unable to add book to viewed",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }

                            // Sets text view to title of book
                            bookTextView.setText(it.child("actualTitle").value.toString())

                            val url = it.child("img").value.toString()
                            Picasso.with(requireContext()).load(url).into(bookCoverImage)
                        }.addOnFailureListener {
                            Toast.makeText(
                                requireContext(),
                                "Error getting recommended book data",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }

                        println("res = $recommendedBook")
                    }
                }
            }.addOnFailureListener {
                // Gets data inside index
                database.child("Books").child("0").child(getRecIndex().toString()).get().addOnSuccessListener {

                    // Sets text view to title of book
                    bookTextView.setText(it.child("actualTitle").value.toString())

                    // Gets book URL and loads it into image view
                    val url = it.child("img").value.toString()
                    Picasso.with(requireContext()).load(url).into(bookCoverImage)

                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Error getting data",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }





        likeBookButton.setOnClickListener {

            // Gets the data of the user in UserPreferences based on user's UID
            // If new user, creates a new element in data base
            val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Liked")

            // Pushes liked book's title into "Liked" subsection in database
            data.push().setValue(bookTextView.getText().toString()).addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    "Added book to liked",
                    Toast.LENGTH_SHORT,
                ).show()
            }.addOnFailureListener{
                Toast.makeText(
                    requireContext(),
                    "Unable to add book to liked",
                    Toast.LENGTH_SHORT,
                ).show()
            }

            database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Liked").get().addOnSuccessListener{
                println("hello")
                for (x in it.children){
                    println(x)
                }
                getBookButton.performClick();
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Error getting liked data",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

        dislikeBookButton.setOnClickListener {
            // Gets the data of the user in UserPreferences based on user's UID
            // If new user, creates a new element in database
            val currentUserUid = auth.currentUser?.uid
            if (currentUserUid != null) {
                val data = database.child("UserPreferences").child(currentUserUid).child("Disliked")

                // Pushes disliked book's title into "Disliked" subsection in database
                data.push().setValue(bookTextView.text.toString()).addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
                        "Added book to disliked",
                        Toast.LENGTH_SHORT
                    ).show()
                    getBookButton.performClick()
                }.addOnFailureListener{
                    Toast.makeText(
                        requireContext(),
                        "Unable to add book to disliked",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                // Handle case where user is not authenticated
            }
        }
        getBookButton.performClick()

        return view
    }



    private fun loadFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }


    private fun callApi() {
        val num = 10
        val url = URL("https://divyeshbommana.app.modelbit.com/v1/double_number/latest")

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")

            val payload = "{\"data\": $num}"

            OutputStreamWriter(outputStream).use {
                it.write(payload)
            }

            val responseCode = responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    println("Response: $response")
                }
            } else {
                println("Error: $responseCode - ${responseMessage}")
            }
        }
    }

    private fun callApiRecommend(titles: List<String>, viewed: List<String>): StringBuffer {
//        val titles = listOf(
//            "Harry Potter and the Deathly Hallows",
//            "Fifty Shades of Grey",
//            "The Golden Compass",
//            "Steve Jobs"
//        )
        val url = URL("https://divyeshbommana.app.modelbit.com/v1/recommend/latest")

        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json")

            val payload = buildPayload(titles, viewed)
            //val payload = "{\"data\": [[1,[\"Harry Potter and the Deathly Hallows\", \"Fifty Shades of Grey\", \"The Golden Compass\"], [\"Steve Jobs\"]]]}"

            println("payload: ${payload}")

            OutputStreamWriter(outputStream).use {
                it.write(payload)
            }

            val responseCode = responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader(InputStreamReader(inputStream)).use {
                    val response = StringBuffer()
                    var inputLine = it.readLine()
                    while (inputLine != null) {
                        response.append(inputLine)
                        inputLine = it.readLine()
                    }
                    println("Response: $response")
                    return response
                }
            } else {
                println("Error: $responseCode - ${responseMessage}")
            }
        }
        return StringBuffer()
    }

    fun buildPayload(titles: List<String>, viewed: List<String>): String {
        val data = titles.joinToString("\", \"", prefix = "[\"", postfix = "\"]")
        val viewedData = viewed.joinToString("\", \"", prefix = "[\"", postfix = "\"]")
        return "{\"data\": [[1,$data, $viewedData]]}"
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


