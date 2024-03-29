package com.example.pracbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import com.squareup.picasso.Picasso
import java.net.URL
import kotlin.random.Random
import aws.sdk.kotlin.services.lambda.model.InvokeResponse
import aws.sdk.kotlin.services.lambda.model.ListFunctionsRequest
import aws.sdk.kotlin.services.lambda.model.LogType
import aws.sdk.kotlin.services.lambda.model.Runtime
import aws.sdk.kotlin.services.lambda.model.UpdateFunctionCodeRequest
import aws.sdk.kotlin.services.lambda.model.UpdateFunctionConfigurationRequest
import aws.sdk.kotlin.services.lambda.waiters.waitUntilFunctionActive
import aws.sdk.kotlin.services.lambda.waiters.waitUntilFunctionUpdated
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import kotlin.system.exitProcess
//import software.amazon.awssdk.regions.Region
//import software.amazon.awssdk.services.lambda.LambdaClient
//import software.amazon.awssdk.services.lambda.model.InvokeRequest
//import software.amazon.awssdk.services.lambda.model.InvokeResponse
import java.nio.charset.Charset
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection

fun getRecIndex(): Int {
    // Function body
    // Gets a random integer from 0-9999
    return Random.nextInt(0, 100)
}


class Main : AppCompatActivity() {

    val functionName = "BookRec"

    // Initilizes Firebase authentication extension to auth variable
    val auth = FirebaseAuth.getInstance()
    // Initilizes Firebase realtime database extension to database variable
    var database = Firebase.database.reference

    // When activity is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "onCreate() in MainActivity is called");
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

//        val lambdaButton = findViewById<Button>(R.id.btn_Lambda)
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

        // If "GET BOOK" button is clicked
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
                                baseContext,
                                "Added book to viewed",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }.addOnFailureListener{
                            Toast.makeText(
                                baseContext,
                                "Unable to add book to viewed",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }

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
                                    baseContext,
                                    "Added book to viewed",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }.addOnFailureListener{
                                Toast.makeText(
                                    baseContext,
                                    "Unable to add book to viewed",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }

                            // Sets text view to title of book
                            bookTextView.setText(it.child("actualTitle").value.toString())

                            val url = it.child("img").value.toString()
                            Picasso.with(this@Main).load(url).into(bookCoverImage)
                        }.addOnFailureListener {
                            Toast.makeText(
                                baseContext,
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
                    Picasso.with(this).load(url).into(bookCoverImage)

                }.addOnFailureListener {
                    Toast.makeText(
                        baseContext,
                        "Error getting data",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
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

            database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Liked").get().addOnSuccessListener{
                println("hello")
                for (x in it.children){
                    println(x)
                }
                getBookButton.performClick();
            }.addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "Error getting liked data",
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
                getBookButton.performClick();
            }.addOnFailureListener{
                Toast.makeText(
                    baseContext,
                    "Unable to add book to disliked",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }

//        lambdaButton.setOnClickListener {
//            GlobalScope.launch(Dispatchers.IO) {
//                var abc = listOf(
//            "Harry Potter and the Deathly Hallows",
//            "Fifty Shades of Grey",
//            "The Golden Compass",
//            "Steve Jobs"
//        )
//                abc = abc.toMutableList()
//                callApiRecommend(abc)
//            }
//        }
        getBookButton.performClick();
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