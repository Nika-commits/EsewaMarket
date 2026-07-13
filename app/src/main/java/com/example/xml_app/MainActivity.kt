package com.example.xml_app

import android.content.Intent
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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

        val heros = mutableListOf<Hero>(
            Hero("Sale", R.drawable.hero1),
            Hero("Sale 2", R.drawable.hero2),
            Hero("Sale 3", R.drawable.hero3)

        )

        val categories = mutableListOf(
            Category(1, R.drawable.ic_shop_clothing, "Fashion"),
            Category(2, R.drawable.ic_shop_computer, "Electronic Device"),
            Category(3, R.drawable.ic_shop_mobile, "Mobile"),
            Category(4, R.drawable.ic_shop_grocery, "Grocery"),
            Category(5, R.drawable.ic_shop_computer, "Fashions")
        )

        val adapter = HeroViewPagerAdapter(heros)
        val heroViewPager = findViewById<ViewPager2>(R.id.heroViewPager)
        heroViewPager.adapter = adapter

        val heroIndicator = findViewById<TabLayout>(R.id.heroIndicator)
        TabLayoutMediator(heroIndicator, heroViewPager){tab, _ ->
            tab.setCustomView(R.layout.item_indicator)
        }.attach()

        val tvUsername: TextView = findViewById(R.id.tvUsername)
        tvUsername.text = userName

        val categoryAdapter = CategoryRecyclerViewAdapter(categories)
        val categoryViewAdapter = findViewById<RecyclerView>(R.id.rvCategoryOptions)

        categoryViewAdapter.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryViewAdapter.adapter = categoryAdapter

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
            R.id.miNotification -> {
                val intent = Intent(this, NotificationActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }
}