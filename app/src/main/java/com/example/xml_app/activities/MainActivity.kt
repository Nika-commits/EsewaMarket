package com.example.xml_app.activities

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.xml_app.R
import com.example.xml_app.databinding.ActivityMainBinding
import com.example.xml_app.databinding.ItemNavigationBinding
import com.example.xml_app.fragments.Cart
import com.example.xml_app.fragments.Favourite
import com.example.xml_app.fragments.Home
import com.example.xml_app.fragments.More

class MainActivity : AppCompatActivity() {
    private var userName = "Pranish" + ","
    private lateinit var binding: ActivityMainBinding
    private val homeFragment = Home()
    private val cartFragment = Cart()
    private val favouriteFragment = Favourite()
    private val moreFragment = More()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        binding.tabHome.ivNavIcon.setImageResource(R.drawable.ic_market)
        binding.tabHome.tvNavText.text = "Home"

        binding.tabCart.ivNavIcon.setImageResource(R.drawable.ic_cart)
        binding.tabCart.tvNavText.text = "Cart"

        binding.tabFavourites.ivNavIcon.setImageResource(R.drawable.ic_fav)
        binding.tabFavourites.tvNavText.text = "Favourites"

        binding.tabMore.ivNavIcon.setImageResource(R.drawable.ic_more)
        binding.tabMore.tvNavText.text = "More"

        if (savedInstanceState == null) {
            replaceFragment(homeFragment)
            setSelectedTab(binding.tabHome)
        }
        binding.tabHome.root.setOnClickListener {
            replaceFragment(homeFragment)
            setSelectedTab(binding.tabHome)
        }

        binding.tabCart.root.setOnClickListener {
            replaceFragment(cartFragment)
            setSelectedTab(binding.tabCart)
        }

        binding.tabFavourites.root.setOnClickListener {
            replaceFragment(favouriteFragment)
            setSelectedTab(binding.tabFavourites)
        }

        binding.tabMore.root.setOnClickListener {
            replaceFragment(moreFragment)
            setSelectedTab(binding.tabMore)
        }


        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, 0)
            insets
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainer, fragment)
            addToBackStack(null)
            commit()
        }
    }


    private fun setSelectedTab(selected: ItemNavigationBinding) {

        val tabs = listOf(
            binding.tabHome,
            binding.tabCart,
            binding.tabFavourites,
            binding.tabMore
        )

        tabs.forEach {
            it.tvNavText.visibility = View.GONE
//            it.tvNavText.animate()
//                .alpha(0f)
//                .setDuration(150)
//                .withEndAction {
//                    it.tvNavText.visibility = View.GONE
//                }
//                .start()
            it.ivNavIcon.setColorFilter(getColor(R.color.textDark))
            it.tvNavText.setTextColor(getColor(R.color.textDark))
            it.rootLayout.setBackgroundColor(getColor(android.R.color.transparent))

            it.ivNavIcon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start()
        }

//        selected.tvNavText.apply {
//            alpha = 0f
//            visibility = View.VISIBLE
//
//            animate()
//                .alpha(1f)
//                .setDuration(200)
//                .start()
//        }
        selected.tvNavText.visibility = View.VISIBLE
        selected.ivNavIcon.setColorFilter(getColor(R.color.primaryGreen))
        selected.tvNavText.setTextColor(getColor(R.color.primaryGreen))
        selected.rootLayout.setBackgroundResource(R.drawable.bg_selected_tab)

        selected.ivNavIcon.animate()
            .scaleX(1.15f)
            .scaleY(1.15f)
            .setDuration(200)
            .start()
    }

}