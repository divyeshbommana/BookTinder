package com.example.pracbook

import com.google.firebase.auth.FirebaseAuth

open class FirebaseWrapper {
    open fun auth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}