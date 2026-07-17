package com.example.xml_app.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xml_app.Activities.NotificationActivity
import com.example.xml_app.Adapters.CategoryRecyclerViewAdapter
import com.example.xml_app.Adapters.FeaturedProductsAdapter
import com.example.xml_app.Adapters.HeroViewPagerAdapter
import com.example.xml_app.Api.RetrofitInstance
import com.example.xml_app.Models.Category
import com.example.xml_app.Models.Hero
import com.example.xml_app.R
import com.example.xml_app.Utils.SpacingItemDecoration
import com.example.xml_app.databinding.FragmentHomeBinding
import com.google.android.material.tabs.TabLayoutMediator
import okio.IOException
import retrofit2.HttpException


const val TAG = "HOME"

class Home : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val userName = "Pranish" + ","
    private lateinit var productAdapter: FeaturedProductsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        applyEdgeToEdgeInsets()
        setUpToolbarAndMenu()
        setupHeroPage()
        setupSearchBox()
        setupCategories()
        setupFeaturedProducts()
    }


    private fun applyEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                view.paddingBottom
            )
            insets
        }
    }

    private fun setUpToolbarAndMenu() {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_bar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.miAbout -> {
                        Toast.makeText(requireContext(), "Clicked on About", Toast.LENGTH_SHORT)
                            .show()
                        true
                    }

                    R.id.miNotification -> {
                        startActivity(Intent(requireContext(), NotificationActivity::class.java))
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupHeroPage() {
        val heroes = mutableListOf(
            Hero("Sale", R.drawable.hero1),
            Hero("Sale 2", R.drawable.hero2),
            Hero("Sale 3", R.drawable.hero3)
        )

        binding.heroViewPager.adapter = HeroViewPagerAdapter(heroes)
        TabLayoutMediator(binding.heroIndicator, binding.heroViewPager) { tab, _ ->
            tab.setCustomView(R.layout.item_indicator)
        }.attach()

        binding.tvUsername.text = userName
    }

    private fun setupCategories() {
        val categories = mutableListOf(
            Category(1, R.drawable.ic_shop_clothing, "Fashion"),
            Category(2, R.drawable.ic_shop_computer, "Electronic Device"),
            Category(3, R.drawable.ic_shop_mobile, "Mobile"),
            Category(4, R.drawable.ic_shop_grocery, "Grocery"),
            Category(5, R.drawable.ic_shop_computer, "Fashions"),
            Category(6, R.drawable.ic_shop_clothing, "Women Fashion"),
            Category(7, R.drawable.ic_shop_computer, "Laptops")
        )

        val categoryRv = binding.rvCategoryOptionsLayout.rvCategoryOptions
        categoryRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryRv.adapter = CategoryRecyclerViewAdapter(categories)

        binding.rvCategoryOptionsLayout.categorySection.tvHeaderTitle.text = "Categories"
        binding.rvCategoryOptionsLayout.categorySection.ibHeaderButton.setOnClickListener {
            Toast.makeText(requireContext(), "ALl Categories", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setupFeaturedProducts() {
        setupRecyclerView()

        lifecycleScope.launchWhenCreated {
            val response = try {
                RetrofitInstance.api.getFeaturedProducts()
            } catch (e: IOException) {
                Log.e(TAG, e.message ?: "Internet issue")
                return@launchWhenCreated
            } catch (e: HttpException) {
                Log.e(TAG, e.message ?: "Http Error")
                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null) {
                productAdapter.products = response.body()!!
            } else {
                Log.e(TAG, "Http Error")
            }
        }


        binding.rvFeaturedProductsSectionLayout.featuredProducts.tvHeaderTitle.text =
            "Featured Products"
        binding.rvFeaturedProductsSectionLayout.featuredProducts.ibHeaderButton.setOnClickListener {
            Toast.makeText(requireContext(), "Featured Products Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    fun setupRecyclerView() =
        binding.rvFeaturedProductsSectionLayout.rvFeaturedProducts.apply {
            productAdapter = FeaturedProductsAdapter()
            adapter = productAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            val spacing = resources.getDimensionPixelSize(R.dimen.spacing_medium)
            addItemDecoration(
                SpacingItemDecoration(spacing)
            )
        }

    private fun setupSearchBox() {
        binding.searchBox.setEndIconOnClickListener {
            Toast.makeText(requireContext(), "Filters Clicked", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}