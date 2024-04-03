package com.example.pracbook

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Main : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(Home())
        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    loadFragment(Home())
                    return@setOnItemSelectedListener true
                }
                R.id.library -> {
                    loadFragment(Library())
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    loadFragment(Profile())
                    return@setOnItemSelectedListener true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction()
            .replace(R.id.container,fragment)
            .commit()
    }
}
