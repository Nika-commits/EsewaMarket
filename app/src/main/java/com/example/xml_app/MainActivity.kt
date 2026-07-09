package com.example.xml_app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class MainActivity : AppCompatActivity() {

    private var userName = "Pranish" + ","

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        getSupportActionBar()?.apply {
            setDisplayShowTitleEnabled(false)
        }

        val tvUsername: TextView = findViewById(R.id.tvUsername)
        tvUsername.text = userName

        val searchBox = findViewById<TextInputLayout>(R.id.searchBox)
        searchBox.setEndIconOnClickListener {
            Toast.makeText(this, "Filters Clicked", Toast.LENGTH_LONG).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miAbout -> Toast.makeText(this, "Clicked on About", Toast.LENGTH_SHORT).show()
            R.id.miNotification -> Toast.makeText(this, "Clicked on Notification", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}