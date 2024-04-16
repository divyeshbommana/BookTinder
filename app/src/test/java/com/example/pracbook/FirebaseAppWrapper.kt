package com.example.pracbook

import com.google.firebase.FirebaseApp

open class FirebaseAppWrapper {
    open fun getInstance(): FirebaseApp {
        return FirebaseApp.getInstance()
    }
}