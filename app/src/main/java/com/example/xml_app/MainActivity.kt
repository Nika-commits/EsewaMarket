package com.example.xml_app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xml_app.Activities.NotificationActivity
import com.example.xml_app.Adapters.CategoryRecyclerViewAdapter
import com.example.xml_app.Adapters.HeroViewPagerAdapter
import com.example.xml_app.Models.Category
import com.example.xml_app.Models.Hero
import com.example.xml_app.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {
    private var userName = "Pranish" + ","

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        getSupportActionBar()?.apply {
            setDisplayShowTitleEnabled(false)
        }

        val heroes = mutableListOf<Hero>(
            Hero("Sale", R.drawable.hero1),
            Hero("Sale 2", R.drawable.hero2),
            Hero("Sale 3", R.drawable.hero3)

        )

        val categories = mutableListOf(
            Category(1, R.drawable.ic_shop_clothing, "Fashion"),
            Category(2, R.drawable.ic_shop_computer, "Electronic Device"),
            Category(3, R.drawable.ic_shop_mobile, "Mobile"),
            Category(4, R.drawable.ic_shop_grocery, "Grocery"),
            Category(5, R.drawable.ic_shop_computer, "Fashions"),
            Category(6, R.drawable.ic_shop_clothing, "Women Fashion"),
            Category(7, R.drawable.ic_shop_computer, "Laptops"),
        )

        val adapter = HeroViewPagerAdapter(heroes)
        val heroViewPager = binding.heroViewPager
        heroViewPager.adapter = adapter

        val heroIndicator = binding.heroIndicator
        TabLayoutMediator(heroIndicator, heroViewPager){tab, _ ->
            tab.setCustomView(R.layout.item_indicator)
        }.attach()

        val tvUsername = binding.tvUsername
        tvUsername.text = userName

        val categoryAdapter = CategoryRecyclerViewAdapter(categories)
        val categoryViewAdapter = binding.rvCategoryOptionsLayout.rvCategoryOptions

        categoryViewAdapter.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        categoryViewAdapter.adapter = categoryAdapter

        binding.searchBox.setEndIconOnClickListener {
            Toast.makeText(this, "Filters Clicked", Toast.LENGTH_LONG).show()
        }

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                binding.searchBox.boxStrokeWidth = 2
                binding.searchBox.boxStrokeColor = getColor(R.color.primaryGreen)
                Toast.makeText(this, "Has Focus", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Has No Focus", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rvCategoryOptionsLayout.categorySection.tvHeaderTitle.text = "Category"
        binding.rvCategoryOptionsLayout.categorySection.ibHeaderButton.setOnClickListener {
            Toast.makeText(this, "See all Categories", Toast.LENGTH_SHORT).show()
        }


        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
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

//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        currentFocus?.let { view ->
//            if(view is EditText){
//                val
//            }
//        }
//        return super.dispatchTouchEvent(ev)
//    }
}