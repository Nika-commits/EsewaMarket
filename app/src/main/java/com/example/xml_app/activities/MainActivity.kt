package com.example.xml_app.activities

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.xml_app.R
import com.example.xml_app.data.productDataStore
import com.example.xml_app.databinding.ActivityMainBinding
import com.example.xml_app.databinding.ItemNavigationBinding
import com.example.xml_app.fragments.Cart
import com.example.xml_app.fragments.Favourite
import com.example.xml_app.fragments.Home
import com.example.xml_app.fragments.More
import com.example.xml_app.models.ProductState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var userName = "Pranish" + ","
    private lateinit var binding: ActivityMainBinding
    private val homeFragment = Home()
    private val cartFragment = Cart()
    private val favouriteFragment = Favourite()
    private val moreFragment = More()


    override fun onCreate(savedInstanceState: Bundle?) {

        val productsStateFlow: Flow<Map<Int, ProductState>> =
            this.productDataStore.data.map { products ->
                products.products
            }

        val cartCount: Flow<Int> = productsStateFlow.map { p ->
            p.values.sumOf { it.cartCount }
        }

        val favouriteCount: Flow<Int> = productsStateFlow.map { p ->
            p.values.count { it.isFavourite }
        }


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

        lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                cartCount.collect { count ->
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

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                favouriteCount.collect { count ->
                    if (count > 0) {
                        binding.tabFavourites.viewBadge.visibility = View.VISIBLE
                        binding.tabFavourites.viewBadge.text = count.toString()
                    } else {
                        binding.tabFavourites.viewBadge.visibility = View.GONE
                    }
                }
            }
        }
//        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
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

//        selected.rootLayout.apply {
//            scaleX = 0.6f
//            scaleY = 0.6f
//            alpha = 0f
//            setBackgroundResource(R.drawable.bg_selected_tab)
//
//            animate()
//                .scaleX(1f)
//                .scaleY(1f)
//                .alpha(1f)
//                .setDuration(200)
//                .start()
//        }
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
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

}