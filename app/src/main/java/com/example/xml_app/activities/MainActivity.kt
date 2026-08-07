package com.example.xml_app.activities

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.fragment
import com.example.xml_app.R
import com.example.xml_app.databinding.ActivityMainBinding
import com.example.xml_app.databinding.ItemNavigationBinding
import com.example.xml_app.fragments.Cart
import com.example.xml_app.fragments.Favourite
import com.example.xml_app.fragments.Home
import com.example.xml_app.fragments.More
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.viewModel.MainViewModel
import kotlinx.coroutines.launch

private val TAG = "Home"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    //    private val homeFragment = Home()
//    private val cartFragment = Cart()
//    private val favouriteFragment = Favourite()
//    private val moreFragment = More()
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        if (savedInstanceState == null) {
//            replaceFragment(homeFragment, false)
            setSelectedTab(binding.tabHome)
        }

        val app = application as? CustomApplicationContext
        app?.auth

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as NavHostFragment

        navController = navHostFragment.navController
        navController.graph = navController.createGraph(
            startDestination = com.example.xml_app.navigation.Home
        ) {
            fragment<Home, com.example.xml_app.navigation.Home> { label = "Home" }
            fragment<Cart, com.example.xml_app.navigation.Cart> { label = "Cart" }
            fragment<Favourite, com.example.xml_app.navigation.Favourite> { label = "Favourite" }
            fragment<More, com.example.xml_app.navigation.More> { label = "More" }
        }

        setupBottomNavigation()
        setSelectedTab(binding.tabHome)

    }

    override fun onStart() {
        super.onStart()
    }

    private fun setupBottomNavigation() {
        binding.tabHome.ivNavIcon.setImageResource(R.drawable.ic_market)
        binding.tabHome.tvNavText.text = "Home"

        binding.tabCart.ivNavIcon.setImageResource(R.drawable.ic_cart)
        binding.tabCart.tvNavText.text = "Cart"

        binding.tabFavourites.ivNavIcon.setImageResource(R.drawable.ic_fav)
        binding.tabFavourites.tvNavText.text = "Favourites"

        binding.tabMore.ivNavIcon.setImageResource(R.drawable.ic_more)
        binding.tabMore.tvNavText.text = "More"


        binding.tabHome.root.setOnClickListener {
            navController.navigate(route = com.example.xml_app.navigation.Home)
            setSelectedTab(binding.tabHome)
        }

        binding.tabCart.root.setOnClickListener {
            navController.navigate(route = com.example.xml_app.navigation.Cart)
            setSelectedTab(binding.tabCart)
        }

        binding.tabFavourites.root.setOnClickListener {
            navController.navigate(route = com.example.xml_app.navigation.Favourite)
            setSelectedTab(binding.tabFavourites)
        }

        binding.tabMore.root.setOnClickListener {
            navController.navigate(route = com.example.xml_app.navigation.More)
            setSelectedTab(binding.tabMore)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.cartCount.collect { count ->
                        if (count > 0) {
                            binding.tabCart.viewBadge.visibility = View.VISIBLE
                            binding.tabCart.viewBadge.text = count.toString()
                            Log.d("Home", count.toString())
                        } else {
                            binding.tabCart.viewBadge.visibility = View.GONE
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.favouriteCount.collect { count ->
                        if (count > 0) {
                            binding.tabFavourites.viewBadge.visibility = View.VISIBLE
                            binding.tabFavourites.viewBadge.text = count.toString()
                        } else {
                            binding.tabFavourites.viewBadge.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

//    private fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
//
//        navController.navigate(R.id.tabHome)
//        val options = navOptions {
//            popUpToId(navController.graph.startDestinationId) {
//                saveState = true
//            }
//            launchedFromUid
//        }
//        supportFragmentManager.beginTransaction().apply {
//            replace(R.id.fragmentContainer, fragment)
//
//            if (addToBackStack) {
//                addToBackStack(null)
//            }
//            commit()
//        }
//    }

    private fun setSelectedTab(selected: ItemNavigationBinding) {
        val tabs = listOf(
            binding.tabHome,
            binding.tabCart,
            binding.tabFavourites,
            binding.tabMore
        )

        tabs.forEach {
            it.tvNavText.visibility = View.GONE
            it.ivNavIcon.setColorFilter(getColor(R.color.textDark))
            it.tvNavText.setTextColor(getColor(R.color.textDark))
            it.rootLayout.setBackgroundColor(getColor(android.R.color.transparent))

            it.ivNavIcon.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(200)
                .start()
        }

        selected.tvNavText.visibility = View.VISIBLE
        selected.ivNavIcon.setColorFilter(getColor(R.color.primaryGreen))
        selected.tvNavText.setTextColor(getColor(R.color.primaryGreen))
        selected.rootLayout.setBackgroundResource(R.drawable.bg_selected_tab)

        selected.ivNavIcon.animate()
            .setDuration(200)
            .start()

        selected.tvNavText.animate()
            .setDuration(200)
            .start()

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
