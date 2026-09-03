package com.example.xml_app.fragments

import android.content.Intent
import android.graphics.Rect
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
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.activities.AuthActivity
import com.example.xml_app.activities.NotificationActivity
import com.example.xml_app.activities.ProductDetailActivity
import com.example.xml_app.adapters.PopularChipsAdapter
import com.example.xml_app.adapters.ProductsAdapter
import com.example.xml_app.adapters.RecommendedProductsAdapter
import com.example.xml_app.adapters.home.HomeBannerAdapter
import com.example.xml_app.adapters.home.HomeCategoriesAdapter
import com.example.xml_app.adapters.home.HomeFeaturedAdapter
import com.example.xml_app.adapters.home.HomeHeadAdapter
import com.example.xml_app.adapters.home.HomeHotDealsAdapter
import com.example.xml_app.adapters.home.HomeMostPopularAdapter
import com.example.xml_app.adapters.home.HomeRecommendedHeaderAdapter
import com.example.xml_app.adapters.home.HomeRecommendedLoadingAdapter
import com.example.xml_app.databinding.FragmentHomeConcatBinding
import com.example.xml_app.models.Category
import com.example.xml_app.models.Hero
import com.example.xml_app.navigation.ApiRoute
import com.example.xml_app.ui.modals.DeleteCartBottomSheet
import com.example.xml_app.utils.CustomSnackBar
import com.example.xml_app.utils.HomeConcatAdapterSpacing
import com.example.xml_app.viewModel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
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
    private lateinit var homeBannerAdapter: HomeBannerAdapter
    private lateinit var homeMostPopularAdapter: HomeMostPopularAdapter
    private lateinit var mostPopularChipsAdapter: PopularChipsAdapter
    private lateinit var recommendedAdapter: RecommendedProductsAdapter
    private lateinit var recommendedHeaderAdapter: HomeRecommendedHeaderAdapter
    private lateinit var recommendedLoadingAdapter: HomeRecommendedLoadingAdapter

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

//        applyEdgeToEdgeInsets()
        setupCategoriesRecyclerView()
        setupFeaturedProductsRecyclerview()
        setupHotDealsProductsRecyclerview()
        setupHomeBannerRecyclerview()
        setupMostPopularRecyclerView()
        setupRecommendedProducts()
        setupHomeRecyclerview()

        FirebaseAuth.getInstance()
            .currentUser?.getIdToken(false)?.addOnSuccessListener {
                Log.d("FirebaseToken", it.token ?: "No Auth Sessions")
            }
    }

    private fun applyEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collect { user ->
                    homeHeadAdapter.setUsername(
                        user?.fullName?.trim()?.substringBefore(" ") ?: "Guest"
                    )
                }
            }
        }
        concatAdapter = ConcatAdapter(
            homeHeadAdapter,
            homeCategoriesAdapter,
            homeFeaturedAdapter,
            homeHotDealsAdapter,
            homeBannerAdapter,
            homeMostPopularAdapter,
            recommendedHeaderAdapter,
            recommendedAdapter,
            recommendedLoadingAdapter
        )

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)

        gridLayoutManager.spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {
                    val (adapter, _) = concatAdapter.getWrappedAdapterAndPosition((position))
                    return if (adapter === recommendedAdapter) 1 else 2
                }
            }
        val spacing =
            resources.getDimensionPixelSize(R.dimen.spacing_medium)

        binding.rvHome.apply {
            layoutManager = gridLayoutManager
            adapter = concatAdapter
            itemAnimator = null
            addItemDecoration(HomeConcatAdapterSpacing(spacing))

            addItemDecoration(
                object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val holder = parent.getChildViewHolder(view)
                        if (holder.bindingAdapter !== recommendedAdapter) return

                        val position = holder.bindingAdapterPosition

                        if (position == RecyclerView.NO_POSITION) return

                        if (position % 2 == 0) {
                            outRect.right = spacing / 2
                        } else {
                            outRect.left = spacing / 2
                        }

                        outRect.top = spacing / 2
                        outRect.bottom = spacing / 2
                    }
                }
            )
        }
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

    private fun setupFeaturedProductsRecyclerview() {
        featuredProductsAdapter = ProductsAdapter(
            onProductClick = { goToDetails(it.id) },
            onCartIncrement = { p, count ->
                incrementCart(p.id, count)
            },
            onCartDecrement = { p, count ->
                decrementCart(p.id, count)
            },
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
            onCartIncrement = { p, count ->
                incrementCart(p.id, count)
            },
            onCartDecrement = { p, count ->
                decrementCart(p.id, count)
            },
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

    private fun setupHomeBannerRecyclerview() {
        homeBannerAdapter = HomeBannerAdapter(
            onClick = {}
        )
    }

    private fun setupMostPopularRecyclerView() {
        mostPopularChipsAdapter = PopularChipsAdapter { category ->
            Toast.makeText(requireContext(), category, Toast.LENGTH_SHORT).show()
        }

        homeMostPopularAdapter = HomeMostPopularAdapter(
            onClick = {},
            chipsAdapter = mostPopularChipsAdapter
        )
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.popularChips.collect { mostPopularChipsAdapter.item = it }
            }
        }

        viewModel.getPopularChips()
    }

    private fun setupRecommendedProducts() {
        recommendedAdapter = RecommendedProductsAdapter(
            onProductClick = { goToDetails(it.id) },
            onFavouriteClick = { toggleFavourite(it.id) },
            onCartIncrement = { p, count ->
                incrementCart(p.id, count)
            },
            onCartDecrement = { p, count ->
                decrementCart(p.id, count)
            }
        )
        recommendedHeaderAdapter = HomeRecommendedHeaderAdapter(
            onSeeAllClick = {}
        )

        recommendedLoadingAdapter = HomeRecommendedLoadingAdapter()

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
                    val isInitialLoading =
                        loadStates.refresh is LoadState.Loading

                    val isLoadingMore =
                        loadStates.append is LoadState.Loading

                    recommendedLoadingAdapter.setLoading(isInitialLoading || isLoadingMore)
//                    homeRecommendedSectionAdapter.setLoading(
//                        isInitialLoading || isLoadingMore
//                    )
                }
            }
        }
    }

    private fun showLoginSnackBar(message: String) {
        val bottomNav = requireActivity().findViewById<LinearLayout>(R.id.bottomNavigation)
        CustomSnackBar.show(
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

    private fun incrementCart(id: Int, count: Int?) {
        if (viewModel.isLoggedIn()) {
            if (count != null) {
                showAddToCartSnackBar()
            }
            viewModel.cartIncrement(id)
        } else {
            showLoginSnackBar("Log in to add to cart.")
        }
    }

    private fun decrementCart(id: Int, count: Int) {
        if (viewModel.isLoggedIn()) {
            if (count == 1) {
                DeleteCartBottomSheet(
                    onDelete = {
                        viewModel.decrementCart(id)
                    }
                ).show(
                    childFragmentManager,
                    "DeleteCartBottomSheet"
                )
            } else {
                viewModel.decrementCart(id)
            }
        } else {
            showLoginSnackBar("Log in to decrement your cart.")
        }
    }

    private fun toggleFavourite(id: Int) {
        if (viewModel.isLoggedIn()) {
            viewModel.toggleFavourite(id)
            val bottomNavigation =
                requireActivity().findViewById<LinearLayout>(R.id.bottomNavigation)
            CustomSnackBar.show(
                binding.root,
                context = requireContext(),
                anchorView = bottomNavigation,
                text = "Added to favourites",
                actionText = "GOTO FAVOURITES",
                action = {
                    findNavController().navigate(ApiRoute.Favourite) {
                        popUpTo<ApiRoute.Home> {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        } else {
            showLoginSnackBar("Log in to add to favourites.")
        }
    }

    private fun goToDetails(id: Int) {
        ProductDetailActivity.startActivity(requireContext(), id)
    }

    private fun showAddToCartSnackBar() {
        val bottomNavigation = requireActivity().findViewById<LinearLayout>(R.id.bottomNavigation)
        CustomSnackBar.show(
            binding.root,
            requireContext(),
            "Added to cart successfully",
            bottomNavigation,
            "GOTO CART",
            action = {
                findNavController().navigate(ApiRoute.Cart) {
                    popUpTo<ApiRoute.Home> {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}