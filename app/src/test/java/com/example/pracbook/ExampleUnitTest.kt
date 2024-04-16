package com.example.pracbook

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mockito

class ExampleUnitTest {

    @Before
    fun setUp() {
        val mockFirebaseWrapper = Mockito.mock(FirebaseWrapper::class.java)
        Mockito.`when`(mockFirebaseWrapper.auth()).thenReturn(Mockito.mock(FirebaseAuth::class.java))
    }

//    @Test
//    fun addition_isCorrect() {
//        assertEquals(4, 2 + 2)
//    }

//    @Test
//    fun checkEmptyEmail() {
//        val mockAuth = Mockito.mock(FirebaseAuth::class.java)
//        val mockFirebaseWrapper = Mockito.mock(FirebaseWrapper::class.java)
//        Mockito.`when`(mockFirebaseWrapper.auth()).thenReturn(mockAuth)
//
//        var email = ""
//        var password = ""
//
//        // Mock unsuccessful login attempt
//        Mockito.`when`(mockAuth.signInWithEmailAndPassword(email, password))
//            .thenReturn(Tasks.forException(Exception("Login failed")))
//
//        val result = LoginUTL.validateLogin(email, password)
//        assertFalse(result)
//    }

    @Test
    fun checkValidCredentials() {
        val mockAuth = Mockito.mock(FirebaseAuth::class.java)
        val mockFirebaseWrapper = Mockito.mock(FirebaseWrapper::class.java)
        Mockito.`when`(mockFirebaseWrapper.auth()).thenReturn(mockAuth)

        var email = "abcd@gmail.com"
        var password = "Password"

        // Mock successful login attempt
        Mockito.`when`(mockAuth.signInWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(null))

        val result = LoginUTL.validateLogin(email, password)
        assertTrue(result)
    }

    @Test
    fun checkRegisterCredentials() {
        val mockAuth = Mockito.mock(FirebaseAuth::class.java)
        val mockFirebaseWrapper = Mockito.mock(FirebaseWrapper::class.java)
        Mockito.`when`(mockFirebaseWrapper.auth()).thenReturn(mockAuth)

        var email = "example@gmail.com"
        var password = "Password"

        // Mock successful login attempt
        Mockito.`when`(mockAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(null))

        val result = LoginUTL.validateResgister(email, password)
        assertTrue(result)
    }

    @Test
    fun checkRegisterCredentials2() {
        val mockAuth = Mockito.mock(FirebaseAuth::class.java)
        val mockFirebaseWrapper = Mockito.mock(FirebaseWrapper::class.java)
        Mockito.`when`(mockFirebaseWrapper.auth()).thenReturn(mockAuth)

        var email = ""
        var password = ""

        // Mock successful login attempt
        Mockito.`when`(mockAuth.createUserWithEmailAndPassword(email, password))
            .thenReturn(Tasks.forResult(null))

        val result = LoginUTL.validateResgister(email, password)
        assertTrue(result)
    }
}
