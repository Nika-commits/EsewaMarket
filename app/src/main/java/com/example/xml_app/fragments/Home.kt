package com.example.xml_app.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.activities.AuthActivity
import com.example.xml_app.activities.NotificationActivity
import com.example.xml_app.activities.ProductDetailActivity
import com.example.xml_app.adapters.CategoryRecyclerViewAdapter
import com.example.xml_app.adapters.HeroViewPagerAdapter
import com.example.xml_app.adapters.PopularChipsAdapter
import com.example.xml_app.adapters.ProductsAdapter
import com.example.xml_app.databinding.FragmentHomeBinding
import com.example.xml_app.models.Category
import com.example.xml_app.models.Hero
import com.example.xml_app.utils.CustomSnackbar
import com.example.xml_app.utils.HorizontalItemDecoration
import com.example.xml_app.utils.SpacingItemDecoration
import com.example.xml_app.viewModel.HomeViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import java.io.Serializable


data class ProductDetailActivityArgs(
    val id: Int
) : Serializable

class Home : Fragment() {
    companion object {
        const val ARGS_RESPONSE = "ARGS_RESPONSE"

        fun startActivity(
            context: Context,
            resultLauncher: ActivityResultLauncher<Intent>,
            args: ProductDetailActivityArgs
        ) {
            resultLauncher.launch(
                Intent(context, ProductDetailActivity::class.java).apply {
                    putExtra(ARGS_RESPONSE, args)
                }
            )
        }
    }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val userName = "Pranish" + ","
    private lateinit var featuredProductsAdapter: ProductsAdapter
    private lateinit var hotDealsAdapter: ProductsAdapter
    private lateinit var recommendedAdapter: ProductsAdapter
    private lateinit var mostPopularChipsAdapter: PopularChipsAdapter


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
        applyEdgeToEdgeInsets()
        setUpToolbarAndMenu()
        setupHeroPage()
        setupSearchBox()
        setupCategories()
        setupFeaturedProducts()
        setupHotDealsProducts()
        setupMostPopularSection()
        setupRecommendedProducts()
    }

    private fun applyEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setUpToolbarAndMenu() {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.toolbar.overflowIcon?.setTint(
            ContextCompat.getColor(
                requireContext(),
                R.color.textDark
            )
        )

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_bar, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.miAbout -> {
                        throw RuntimeException("Test Exception")
//                        Toast.makeText(requireContext(), "Clicked on About", Toast.LENGTH_SHORT)
//                            .show()
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
        categoryRv.adapter = CategoryRecyclerViewAdapter(categories) { category ->
            Toast.makeText(requireContext(), category.categoryName, Toast.LENGTH_SHORT).show()
        }

        binding.rvCategoryOptionsLayout.categorySection.tvHeaderTitle.text = "Categories"
        binding.rvCategoryOptionsLayout.categorySection.ibHeaderButton.setOnClickListener {
            Toast.makeText(requireContext(), "ALl Categories", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupFeaturedProducts() {
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        featuredProductsAdapter =
            setupRecyclerView(
                binding.rvFeaturedProductsSectionLayout.rvFeaturedProducts,
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false
                ),
                HorizontalItemDecoration(spacing)
            )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.featuredProducts.collect {
                        featuredProductsAdapter.products = it
                    }
                }
            }
        }

        binding.rvFeaturedProductsSectionLayout.featuredProducts.tvHeaderTitle.text =
            "Featured Products"
        binding.rvFeaturedProductsSectionLayout.featuredProducts.ibHeaderButton.setOnClickListener {
            Toast.makeText(requireContext(), "Featured Products Clicked", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupHotDealsProducts() {
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        hotDealsAdapter = setupRecyclerView(
            binding.rvHotDealsLayout.rvFeaturedProducts,
            GridLayoutManager(requireContext(), 2),
            SpacingItemDecoration(spacing)
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.hotDealsProducts.collect {
                        hotDealsAdapter.products = it
                    }
                }
            }
        }

        binding.rvHotDealsLayout.featuredProducts.tvHeaderTitle.text = "Hot Deals of the Day"
        binding.rvHotDealsLayout.featuredProducts.ibHeaderButton.setOnClickListener {
            Toast.makeText(requireContext(), "Hot Deals Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecommendedProducts() {
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        recommendedAdapter = setupRecyclerView(
            binding.rvRecommendedProductsSectionLayout.rvFeaturedProducts,
            GridLayoutManager(requireContext(), 2),
            SpacingItemDecoration(spacing)
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.recommendedProducts.collect {
                        recommendedAdapter.products = it
                    }
                }
            }
        }

        binding.rvRecommendedProductsSectionLayout.featuredProducts.tvHeaderTitle.text =
            "Recommended Products"
        binding.rvRecommendedProductsSectionLayout.featuredProducts.ibHeaderButton.setOnClickListener {
            Toast.makeText(requireContext(), "Recommended Products", Toast.LENGTH_SHORT).show()
        }
    }

    fun setupRecyclerView(
        recyclerView: RecyclerView,
        layoutManager: RecyclerView.LayoutManager,
        itemDecoration: RecyclerView.ItemDecoration? = null
    ): ProductsAdapter {

        val adapter = ProductsAdapter(
            onProductClick = { p ->
                Intent(requireContext(), ProductDetailActivity::class.java).also {
                    it.putExtra("id", p.id)
                    startActivity(it)
                }
            },
            onFavouriteClick = { p ->
                viewLifecycleOwner.lifecycleScope.launch {
                    if (viewModel.isLoggedIn()) {
                        viewModel.toggleFavourite(p.id)
                    } else {
                        showLoginSnackbar("Log in to add to Favourites")
                    }
                }
            },
            onCartIncrement = { p ->
                viewLifecycleOwner.lifecycleScope.launch {
                    if (viewModel.isLoggedIn()) {
                        viewModel.cartIncrement(p.id)
                    } else {
                        showLoginSnackbar("Log in to add to cart")
                    }
                }
            },
            onCartDecrement = { p ->
                viewLifecycleOwner.lifecycleScope.launch {
                    if (viewModel.isLoggedIn()) {
                        viewModel.decrementCart(p.id)
                    } else {
                        showLoginSnackbar("Log in to add to cart")
                    }
                }

            }
        )

        recyclerView.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            itemAnimator = null

            itemDecoration?.let {
                addItemDecoration(it)
            }
        }

        return adapter
    }

    private fun setupMostPopularSection() {
        mostPopularChipsAdapter = PopularChipsAdapter { category ->
            Toast.makeText(requireContext(), category, Toast.LENGTH_SHORT).show()
        }

        binding.rvMostPopular.apply {
            layoutManager = FlexboxLayoutManager(requireContext()).apply {
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP
                justifyContent = JustifyContent.FLEX_START
            }
            adapter = mostPopularChipsAdapter
        }
        binding.mostPopularHeader.tvHeaderTitle.text = "Most Popular"

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.popularChips.collect { mostPopularChipsAdapter.item = it }
            }
        }
    }

    private fun setupSearchBox() {
        binding.searchBox.setEndIconOnClickListener {
            Toast.makeText(requireContext(), "Filters Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoginSnackbar(message: String) {
        val bottomNav = requireActivity().findViewById<LinearLayout>(R.id.bottomNavigation)
        CustomSnackbar.show(
            view = binding.root,
            context = requireContext(),
            text = message,
            anchorView = bottomNav,
            actionText = "Login",
            action = {
                val intent = Intent(requireContext(), AuthActivity::class.java).apply {
                    putExtra(AuthActivity.DESTINATION, AuthActivity.LOGIN)
                }
                startActivity(intent)
            }
        )
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}