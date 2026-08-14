package com.example.xml_app.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.activities.AuthActivity
import com.example.xml_app.activities.NotificationActivity
import com.example.xml_app.activities.ProductDetailActivity
import com.example.xml_app.adapters.PopularChipsAdapter
import com.example.xml_app.adapters.ProductsAdapter
import com.example.xml_app.adapters.RecommendedProductsAdapter
import com.example.xml_app.adapters.home.HomeCategoriesAdapter
import com.example.xml_app.adapters.home.HomeFeaturedAdapter
import com.example.xml_app.adapters.home.HomeHeadAdapter
import com.example.xml_app.adapters.home.HomeHotDealsAdapter
import com.example.xml_app.databinding.FragmentHomeConcatBinding
import com.example.xml_app.models.Category
import com.example.xml_app.models.Hero
import com.example.xml_app.utils.CustomSnackbar
import com.example.xml_app.utils.SpacingItemDecoration
import com.example.xml_app.viewModel.HomeViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Home : Fragment() {
    private var _binding: FragmentHomeConcatBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var concatAdapter: ConcatAdapter
    private lateinit var homeHeadAdapter: HomeHeadAdapter
    private lateinit var homeCategoriesAdapter: HomeCategoriesAdapter
    private lateinit var homeFeaturedAdapter: HomeFeaturedAdapter
    private lateinit var featuredProductsAdapter: ProductsAdapter
    private lateinit var homeHotDealsAdapter: HomeHotDealsAdapter
    private lateinit var hotDealsAdapter: ProductsAdapter
    private lateinit var recommendedAdapter: RecommendedProductsAdapter
    private lateinit var mostPopularChipsAdapter: PopularChipsAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeConcatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initializeUser()

        applyEdgeToEdgeInsets()
        setupHomeRecyclerview()
        setupCategoriesRecyclerView()
        setupFeaturedProductsRecyclerview()
        setupHotDealsProductsRecyclerview()
        setupMostPopularSection()
        setupRecommendedProducts()
    }

    private fun applyEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setUpToolbarAndMenu(toolbar: androidx.appcompat.widget.Toolbar) {
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar.overflowIcon?.setTint(
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

    private fun setupHomeRecyclerview() {
        val heroes = mutableListOf(
            Hero("Sale", R.drawable.hero1),
            Hero("Sale 2", R.drawable.hero2),
            Hero("Sale 3", R.drawable.hero3)
        )
        homeHeadAdapter = HomeHeadAdapter(
            heroes = heroes,
            onFilterClick = {},
            onToolbarReady = { setUpToolbarAndMenu(it) }
        )
        concatAdapter = ConcatAdapter(
            homeHeadAdapter,
            homeCategoriesAdapter,
            homeFeaturedAdapter
        )
        binding.rvHome.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = concatAdapter
        }

        val categories = listOf(
            Category(1, R.drawable.ic_shop_clothing, "Fashion"),
            Category(2, R.drawable.ic_shop_computer, "Electronic Device"),
            Category(3, R.drawable.ic_shop_mobile, "Mobile"),
            Category(4, R.drawable.ic_shop_grocery, "Grocery"),
            Category(5, R.drawable.ic_shop_computer, "Fashions"),
            Category(6, R.drawable.ic_shop_clothing, "Women Fashion"),
            Category(7, R.drawable.ic_shop_computer, "Laptops")
        )


    }

    private fun setupCategoriesRecyclerView() {
        val categories = mutableListOf(
            Category(1, R.drawable.ic_shop_clothing, "Fashion"),
            Category(2, R.drawable.ic_shop_computer, "Electronic Device"),
            Category(3, R.drawable.ic_shop_mobile, "Mobile"),
            Category(4, R.drawable.ic_shop_grocery, "Grocery"),
            Category(5, R.drawable.ic_shop_computer, "Fashions"),
            Category(6, R.drawable.ic_shop_clothing, "Women Fashion"),
            Category(7, R.drawable.ic_shop_computer, "Laptops")
        )
        homeCategoriesAdapter = HomeCategoriesAdapter(
            categories = categories,
            onCategoryClick = { c ->
                Toast.makeText(requireContext(), c.categoryName, Toast.LENGTH_SHORT).show()
            },
            onSeeAllClick = {}
        )
    }

    private fun incrementCart(id: Int) {
        if (viewModel.isLoggedIn()) {
            viewModel.cartIncrement(id)
        } else {
            showLoginSnackBar("Log in to add to cart.")
        }
    }

    private fun decrementCart(id: Int) {
        if (viewModel.isLoggedIn()) {
            viewModel.decrementCart(id)
        } else {
            showLoginSnackBar("Log in to decrement your cart.")
        }
    }

    private fun toggleFavourite(id: Int) {
        if (viewModel.isLoggedIn()) {
            viewModel.toggleFavourite(id)
        } else {
            showLoginSnackBar("Log in to add to favourites.")
        }
    }

    private fun goToDetails(id: Int) {
        ProductDetailActivity.startActivity(requireContext(), id)
    }

    private fun setupFeaturedProductsRecyclerview() {
        featuredProductsAdapter = ProductsAdapter(
            onProductClick = { goToDetails(it.id) },
            onCartIncrement = { incrementCart(it.id) },
            onCartDecrement = { decrementCart(it.id) },
            onFavouriteClick = { toggleFavourite(it.id) }
        )

        homeFeaturedAdapter = HomeFeaturedAdapter(
            productsAdapter = featuredProductsAdapter,
            onSeeAllClick = {}
        )
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.featuredProducts.collect { featuredProductsAdapter.products = it }
            }
        }

        viewModel.getFeaturedProduct()
    }

    private fun setupHotDealsProductsRecyclerview() {
        hotDealsAdapter = ProductsAdapter(
            onProductClick = { goToDetails(it.id) },
            onCartIncrement = { incrementCart(it.id) },
            onCartDecrement = { decrementCart(it.id) },
            onFavouriteClick = { toggleFavourite(it.id) }
        )

        homeHotDealsAdapter = HomeHotDealsAdapter(
            productsAdapter = hotDealsAdapter,
            onSeeAllClick = {}
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.hotDealsProducts.collect { hotDealsAdapter.products = it }
            }
        }

        viewModel.getHotDealsProducts()
    }

    private fun setupRecommendedProducts() {
        Log.d("PAGING", "setupRecommendedCalled")
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_medium)

        recommendedAdapter = RecommendedProductsAdapter(
            onProductClick = { p ->
                Intent(requireContext(), ProductDetailActivity::class.java).also {
                    it.putExtra("id", p.id)
                    startActivity(it)
                }
            },

            onFavouriteClick = { p ->
                if (viewModel.isLoggedIn()) {
                    viewModel.toggleFavourite(p.id)
                } else {
                    showLoginSnackBar("Log in to add to Favourites")
                }
            },

            onCartIncrement = { p ->
                if (viewModel.isLoggedIn()) {
                    viewModel.cartIncrement(p.id)
                } else {
                    showLoginSnackBar("Log in to add to cart")
                }
            },

            onCartDecrement = { p ->
                if (viewModel.isLoggedIn()) {
                    viewModel.decrementCart(p.id)
                } else {
                    showLoginSnackBar("Log in to add to cart")
                }
            }
        )
        binding.rvRecommendedProductsSectionLayout.rvFeaturedProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = recommendedAdapter
            itemAnimator = null
            addItemDecoration(SpacingItemDecoration(spacing))
            layoutParams = layoutParams.apply {
                height = resources.getDimensionPixelSize(R.dimen.recommended_list_height)
            }
            isNestedScrollingEnabled = true
        }

        binding.rvRecommendedProductsSectionLayout.featuredProducts.tvHeaderTitle.text =
            "Recommended Products"
        binding.rvRecommendedProductsSectionLayout.featuredProducts.ibHeaderButton.setOnClickListener {
            Toast.makeText(requireContext(), "Recommended Products", Toast.LENGTH_SHORT).show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.recommendedProducts.collectLatest { pagingData ->
                    recommendedAdapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                recommendedAdapter.loadStateFlow.collectLatest { loadStates ->
                    val isInitialLoading = loadStates.refresh is LoadState.Loading
                    val isLoadingMore = loadStates.append is LoadState.Loading

                    binding.Spinner.isVisible = isInitialLoading || isLoadingMore
                    binding.tvLoading.isVisible = isInitialLoading || isLoadingMore
                }
            }
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
                if (viewModel.isLoggedIn()) {
                    viewModel.toggleFavourite(p.id)
                } else {
                    showLoginSnackBar("Log in to add to Favourites")
                }
            },
            onCartIncrement = { p ->
                if (viewModel.isLoggedIn()) {
                    viewModel.cartIncrement(p.id)
                } else {
                    showLoginSnackBar("Log in to add to cart")
                }
            },
            onCartDecrement = { p ->
                if (viewModel.isLoggedIn()) {
                    viewModel.decrementCart(p.id)
                } else {
                    showLoginSnackBar("Log in to add to cart")
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
        viewModel.getPopularChips()
    }

    private fun showLoginSnackBar(message: String) {
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