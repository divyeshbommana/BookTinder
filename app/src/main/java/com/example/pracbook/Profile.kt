package com.example.pracbook

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase // Correct import for FirebaseDatabase

class Profile : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val button = view.findViewById<Button>(R.id.logout)

        // Initilizes Firebase authentication extension to auth variable
        val auth = FirebaseAuth.getInstance()
        // Correctly initializes Firebase realtime database reference
        val database = FirebaseDatabase.getInstance().reference

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

        return view
    }
}
