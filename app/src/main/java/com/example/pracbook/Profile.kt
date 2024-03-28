package com.example.pracbook

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Profile : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser

    private lateinit var emailTextView: TextView
    private lateinit var newPasswordEditText: EditText
//    private lateinit var newEmailEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!

        emailTextView = findViewById(R.id.email)
        newPasswordEditText = findViewById(R.id.newPassword)
//        newEmailEditText = findViewById(R.id.newEmail)

        val updateEmailButton = findViewById<Button>(R.id.updateEmail)
        val updatePasswordButton = findViewById<Button>(R.id.updatePassword)
        val deleteAccountButton = findViewById<Button>(R.id.deleteAccount)
        val logoutButton = findViewById<Button>(R.id.logout)

        emailTextView.text = currentUser.email

        updateEmailButton.setOnClickListener {
//            val newEmail = emailTextView.text.toString()
            val newEmail = currentUser.email.toString()
            if (newEmail.isNotEmpty()) {
                currentUser.updateEmail(newEmail)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to update email", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter a new email", Toast.LENGTH_SHORT).show()
            }
        }

//        updateEmailButton.setOnClickListener {
//            val newEmail = currentUser.email.toString()
//            if (newEmail.isNotEmpty()) {
//                currentUser.updateEmail(newEmail)
//                    .addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            Toast.makeText(this, "Email updated successfully", Toast.LENGTH_SHORT).show()
//                        } else {
//                            Toast.makeText(this, "Failed to update email", Toast.LENGTH_SHORT).show()
//                        }
//                    }
//            } else {
//                Toast.makeText(this, "Please enter a new email", Toast.LENGTH_SHORT).show()
//            }
//        }



        updatePasswordButton.setOnClickListener {
            val newPassword = newPasswordEditText.text.toString()

            if (newPassword.isNotEmpty()) {
                currentUser.updatePassword(newPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter a new password", Toast.LENGTH_SHORT).show()
            }
        }


        deleteAccountButton.setOnClickListener {
            // You can add a confirmation dialog before deleting the account
            currentUser.delete()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, Login::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to delete account", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }
    }
}

