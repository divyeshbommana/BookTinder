package com.example.pracbook

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
object LoginUTL {
    val mockAuth = Mockito.mock(FirebaseAuth::class.java)

    fun validateLogin(
        email: String,
        password: String
    ): Boolean {
        // Mocking the behavior of signInWithEmailAndPassword to return a successful Task
        Mockito.`when`(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(null))

        // Now the isSuccessful call won't throw NullPointerException
        return mockAuth.signInWithEmailAndPassword(email, password).isSuccessful
    }

    fun validateResgister(
        email: String,
        password: String
    ): Boolean{
        Mockito.`when`(mockAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(null))

        // Now the isSuccessful call won't throw NullPointerException
        return mockAuth.createUserWithEmailAndPassword(email, password).isSuccessful
    }
}
