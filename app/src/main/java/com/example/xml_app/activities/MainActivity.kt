package com.example.xml_app.activities

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
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
import com.example.xml_app.navigation.ApiRoute
import com.example.xml_app.viewModel.MainViewModel
import kotlinx.coroutines.launch

private val TAG = "Home"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.fragmentContainer.id) as NavHostFragment

        navController = navHostFragment.navController
        navController.graph = navController.createGraph(
            startDestination = ApiRoute.Home,
        ) {
            fragment<Home, ApiRoute.Home> { label = "Home" }
            fragment<Cart, ApiRoute.Cart> { label = "Cart" }
            fragment<Favourite, ApiRoute.Favourite> { label = "Favourite" }
            fragment<More, ApiRoute.More> { label = "More" }
        }

        setupBottomNavigation()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when {
                destination.hasRoute<ApiRoute.Home>() -> setSelectedTab(binding.tabHome)
                destination.hasRoute<ApiRoute.Cart>() -> setSelectedTab(binding.tabCart)
                destination.hasRoute<ApiRoute.Favourite>() -> setSelectedTab(binding.tabFavourites)
                destination.hasRoute<ApiRoute.More>() -> setSelectedTab(binding.tabMore)
            }
        }

        onBackPressedDispatcher.addCallback(this) {
            if (!navController.currentDestination!!.hasRoute<ApiRoute.Home>()) {
                navigateTo(ApiRoute.Home)
            } else {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }
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
            if (!navController.currentDestination!!.hasRoute<ApiRoute.Home>()) {
                navigateTo(ApiRoute.Home)
            }
        }

        binding.tabCart.root.setOnClickListener {
            if (!navController.currentDestination!!.hasRoute<ApiRoute.Cart>()) {
                navigateTo(ApiRoute.Cart)
            }
        }

        binding.tabFavourites.root.setOnClickListener {
            if (!navController.currentDestination!!.hasRoute<ApiRoute.Favourite>()) {
                navigateTo(ApiRoute.Favourite)
            }
        }

        binding.tabMore.root.setOnClickListener {
            if (!navController.currentDestination!!.hasRoute<ApiRoute.More>()) {
                navigateTo(ApiRoute.More)
            }
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

    private fun navigateTo(route: ApiRoute) {
        navController.navigate(route = route) {
            popUpTo<ApiRoute.Home> { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }


    private fun setSelectedTab(selected: ItemNavigationBinding) {
        val tabs = listOf(
            binding.tabHome,
            binding.tabCart,
            binding.tabFavourites,
            binding.tabMore
        )

        tabs.forEach { tab ->
            val isSelected = tab == selected

            tab.tvNavText.visibility = if (isSelected) View.VISIBLE else View.GONE
            tab.ivNavIcon.setColorFilter(getColor(if (isSelected) R.color.primaryGreen else R.color.textDark))
            tab.tvNavText.setTextColor(getColor(if (isSelected) R.color.primaryGreen else R.color.textDark))

            if (isSelected) {
                tab.rootLayout.setBackgroundResource(R.drawable.bg_selected_tab)
                animateSelectedTab(tab.root)
            } else {
                tab.rootLayout.setBackgroundColor(getColor(android.R.color.transparent))
                tab.root.animate().cancel()
                tab.root.scaleX = 1f
                tab.root.scaleY = 1f
                tab.root.alpha = 1f
            }
        }
    }

    private fun animateSelectedTab(view: View) {
        view.animate().cancel()
        view.scaleY = 0.95f
        view.scaleX = 0.95f
//        view.alpha = 0.75f

        view.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(220)
//            .setInterpolator(
//                android.view.animation.OvershootInterpolator(1.4f)
//            )
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