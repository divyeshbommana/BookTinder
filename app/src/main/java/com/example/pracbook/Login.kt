package com.example.pracbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class Login : AppCompatActivity() {

    // Initilizes Firebase authentication extension to auth variable
    val auth = Firebase.auth

    // When app starts
    public override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "onStart() in LoginActivity is called");
        // Gets current user using auth, if current user is not equal to null (signed in)
        // Then it takes user to main activity page
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(getApplicationContext(), Main::class.java)
            startActivity(intent)
            finish()
        }
    }


    // When user is not logged in
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Lifecycle", "oncreate() in LoginActivity is called");
        // Shows the login activity page
        setContentView(R.layout.activity_login)

        // Gets all elements in login activity page
        val editTextEmail = findViewById<EditText>(R.id.email)
        val editTextPassword = findViewById<EditText>(R.id.password)
        val buttonLogin = findViewById<Button>(R.id.btn_login)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val textView = findViewById<TextView>(R.id.registerNow)

        // For "Register" text on login page
        textView.setOnClickListener {

            // If text view is clicked, starts register activity
            val intent = Intent(getApplicationContext(), Register::class.java)
            startActivity(intent)
            finish()
        }

        // When "LOGIN" button is clicked
        buttonLogin.setOnClickListener {

            // Sets the progressBar (loading) to become visible
            progressBar.setVisibility(View.VISIBLE)

            // Gets values inside text fields
            val email = editTextEmail.text.toString();
            val password = editTextPassword.text.toString();

            // If either email or password is empty, prompts user to enter field and returns
            if(TextUtils.isEmpty(email)){
                Toast.makeText(this@Login, "Enter email", Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }
            if(TextUtils.isEmpty(password)) {
                Toast.makeText(this@Login, "Enter password", Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            // Authenticates email and password with auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.setVisibility(View.GONE)

                    // Checks if login is successful or not
                    if (task.isSuccessful) {
                        Toast.makeText(
                            baseContext,
                            "Login successful.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        // If successful, starts main activity
                        val intent = Intent(getApplicationContext(), Main::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Login failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }

        }

    }
}