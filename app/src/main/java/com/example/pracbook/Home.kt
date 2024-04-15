package com.example.pracbook

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
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

    //Code related to flip.
    private lateinit var bookInfoCard: CardView
    private var isBookCoverVisible = true
    private lateinit var bookCoverImage: ImageView
    //pop up menu to add books.
    val spinnerItems = arrayOf("SELECT", "Reading", "Read")

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
        bookCoverImage = view.findViewById(R.id.book_cover)

        //book swipe and flip mechnaism:
        val bookTitleTextView = view.findViewById<TextView>(R.id.book_title)
        val bookDescription = view.findViewById<TextView>(R.id.book_description)

        //code for pop up menu (OLD)
//        val spinner: Spinner = view.findViewById(R.id.spinner_dropdown)

        //4/14/2024
        val spinnerLayout: LinearLayout = view.findViewById(R.id.spinner_layout)
        val spinner: Spinner = view.findViewById(R.id.spinner_dropdown)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter
        spinner.setSelection(0)



        // flip card pointer.
        bookInfoCard = view.findViewById(R.id.book_info_card)

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

//        //we set teh first book as the starting book.
//        val randomVal = Random.nextInt(0,10000)
//        database.child("Books").child("0").child(randomVal.toString()).get().addOnSuccessListener {
//            //sets the text on the card flipped tp the title of the book.
//            bookTitleTextView.setText(it.child("actualTitle").value.toString())
//            // sets the book description
//            bookDescription.setText(it.child("actualGenre").value.toString())
//            // Gets book URL and loads it into image view
//            val url = it.child("img").value.toString()
//            Picasso.with(requireActivity()).load(url).into(bookCoverImage)
//        }.addOnFailureListener {
//            Toast.makeText(
//                requireContext(),
//                "Error getting data",
//                Toast.LENGTH_SHORT,
//            ).show()
//        }

        // code for swipe mechanism. Class onSwipeTouchListener is used for this.
        // Which every book is displyed, we see that and can user the features of swipeUp, swipeLeft, and swipeRight
        bookCoverImage.setOnTouchListener(object : OnSwipeTouchListener(requireActivity()) { //this@Main to requireActivity()
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                //Dislike Book code
                // Gets the data of the user in UserPreferences based on user's UID
                // If new user, creates a new element in data base
                val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Disliked")
                // Pushes disliked book's title into "Disliked" subsection in database
                data.push().setValue(bookTitleTextView.getText().toString()).addOnSuccessListener {//chnaged this from bookTextView to bookTitleTextView
                    Toast.makeText(
                        requireContext(),
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
                        Picasso.with(requireActivity()).load(url).into(bookCoverImage)

                    }
                }.addOnFailureListener{
                    Toast.makeText(
                        requireContext(),
                        "Unable to add book to disliked",
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                //hide the section.
                spinnerLayout.visibility = View.INVISIBLE
                //resets the pop up spinner to select.
                spinner.setSelection(0)
            }

            // Liked Book code
            override fun onSwipeRight() {
                super.onSwipeRight()
                val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Liked")
                // Pushes liked book's title into "Liked" subsection in database
                data.push().setValue(bookTitleTextView.getText().toString()).addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
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
                        Picasso.with(requireActivity()).load(url).into(bookCoverImage)
                    }
                }.addOnFailureListener{
                    Toast.makeText(
                        requireContext(),
                        "Unable to add book to liked",
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                //hide the section.
                spinnerLayout.visibility = View.INVISIBLE
                //resets the pop up spinner to select.
                spinner.setSelection(0)
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
                    Picasso.with(requireActivity()).load(url).into(bookCoverImage)
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Error getting data",
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                //hide the section.
                spinnerLayout.visibility = View.INVISIBLE
                //resets the pop up spinner to select.
                spinner.setSelection(0)
            }

            override fun onClick() {
                super.onClick()
                // When user taps on the bookCoverImage, it should flip to display bookInformationCard. How do I this?
                Toast.makeText(requireActivity(), "User Tapped on this on book image", Toast.LENGTH_SHORT)
                    .show()
//                //hide the section.
//                spinnerLayout.visibility = View.INVISIBLE
                //method to flip card.
                flipViews()
            }

            //code for pop up menu
            override fun onLongClick(){
                super.onLongClick()
//                should be able to have a drop down menu.
//                spinner.visibility = View.VISIBLE
                spinnerLayout.visibility = View.VISIBLE
                true
            }


        })



        // code for book info card.
        bookInfoCard.setOnTouchListener(object : OnSwipeTouchListener(requireActivity()) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                // Handle left swipe action on bookInformationCard
                val data = database.child("UserPreferences").child(auth.currentUser?.uid.toString()).child("Disliked")
                // Pushes disliked book's title into "Disliked" subsection in database
                //bookTextView to bookTitleTextView
                data.push().setValue(bookTitleTextView.getText().toString()).addOnSuccessListener {
                    Toast.makeText(
                        requireContext(),
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
                        Picasso.with(requireActivity()).load(url).into(bookCoverImage)
                    }
                }.addOnFailureListener{
                    Toast.makeText(
                        requireContext(),
                        "Unable to add book to disliked",
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                //hide the section.
                spinnerLayout.visibility = View.INVISIBLE
                //resets the pop up spinner to select.
                spinner.setSelection(0)
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
                        requireContext(),
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
                        Picasso.with(requireActivity()).load(url).into(bookCoverImage)
                    }
                }.addOnFailureListener{
                    Toast.makeText(
                        requireContext(),
                        "Unable to add book to liked",
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                //hide the section.
                spinnerLayout.visibility = View.INVISIBLE
                //resets the pop up spinner to select.
                spinner.setSelection(0)
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
                    Picasso.with(requireActivity()).load(url).into(bookCoverImage)
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Error getting data",
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                //hide the section.
                spinnerLayout.visibility = View.INVISIBLE
                //resets the pop up spinner to select.
                spinner.setSelection(0)
                //flips once its clicked on.
                flipViews()

            }

            override fun onClick() {
                super.onClick()
                // When user taps on the bookCoverImage, it should flip to display bookInformationCard. How do I this?
//                Toast.makeText(requireActivity(), "User Tapped on this on the book info card", Toast.LENGTH_SHORT)
//                    .show()
                //method to flip card.I want this to display a new
                flipViews()
            }

            //code for pop up menu
            override fun onLongClick(){
                super.onLongClick()
//                should be able to have a drop down menu.
                spinnerLayout.visibility = View.VISIBLE
                true
            }
        })

        //pop up code.
        fun showToast(message: String) {
            Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = spinnerItems[position]
                when (selectedItem) {
                    // add
                    "Reading" -> showToast("You selected Reading")
                    "Read" -> showToast("You selected Read")
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected
            }
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


