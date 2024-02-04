package com.example.pracbook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

public class Register : AppCompatActivity() {

    val auth = Firebase.auth
    var database = Firebase.database.reference

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(getApplicationContext(), Main::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val editTextEmail = findViewById<EditText>(R.id.email)
        val editTextPassword = findViewById<EditText>(R.id.password)
        val buttonReg = findViewById<Button>(R.id.btn_register)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val textView = findViewById<TextView>(R.id.loginNow)

        textView.setOnClickListener {
            val intent = Intent(getApplicationContext(), Login::class.java)
            startActivity(intent)
            finish()
        }

        //Diff
        buttonReg.setOnClickListener{
            progressBar.setVisibility(View.VISIBLE)
            val email = editTextEmail.text.toString();
            val password = editTextPassword.text.toString();

            if(TextUtils.isEmpty(email)){
                Toast.makeText(this@Register, "Enter email", Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }
            if(TextUtils.isEmpty(password)) {
                Toast.makeText(this@Register, "Enter password", Toast.LENGTH_SHORT).show();
                return@setOnClickListener;
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.setVisibility(View.GONE)
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        val user = auth.currentUser
                        Toast.makeText(
                            baseContext,
                            "Account Created",
                            Toast.LENGTH_SHORT,
                        ).show()
                        val intent = Intent(getApplicationContext(), Main::class.java)
                        startActivity(intent)
                        finish()
//                        var userData = HashMap<String, Array<String.Companion>>()
//                        userData.put("Liked", (arrayOf (String)))
//                        userData.put("Disliked", (arrayOf (String)))
//                        database = database.child("UserPreferences").child(auth.currentUser?.uid.toString())
//                        database.push().setValue(userData)
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }


        }

    }
}