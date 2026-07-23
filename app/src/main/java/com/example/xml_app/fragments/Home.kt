package com.example.xml_app.fragments

import android.content.Context
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
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
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
import com.example.xml_app.activities.NotificationActivity
import com.example.xml_app.activities.ProductDetailActivity
import com.example.xml_app.adapters.CategoryRecyclerViewAdapter
import com.example.xml_app.adapters.HeroViewPagerAdapter
import com.example.xml_app.adapters.ProductsAdapter
import com.example.xml_app.data.productDataStore
import com.example.xml_app.databinding.FragmentHomeBinding
import com.example.xml_app.models.Category
import com.example.xml_app.models.Hero
import com.example.xml_app.models.ProductState
import com.example.xml_app.utils.HorizontalItemDecoration
import com.example.xml_app.utils.SpacingItemDecoration
import com.example.xml_app.viewModel.HomeViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.Serializable


const val TAG = "HOME"


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

    fun productStateFlow(): Flow<Map<Int, ProductState>> =
        requireContext().productDataStore.data.map { products ->
            products.products
        }

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val userName = "Pranish" + ","
    private lateinit var featuredProductsAdapter: ProductsAdapter

    private lateinit var hotDealsAdapter: ProductsAdapter


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
        applyEdgeToEdgeInsets()
        setUpToolbarAndMenu()
        setupHeroPage()
        setupSearchBox()
        setupCategories()
        setupFeaturedProducts()
        setupHotDealsProducts()

        viewLifecycleOwner.lifecycleScope.launch {
            Log.d("Home", "Saved States = ${getAllProductState()}")
        }
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


        viewModel.featuredProducts.observe(viewLifecycleOwner) { products ->
            featuredProductsAdapter.products = products
        }

        viewModel.getFeaturedProduct()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productStateFlow().collect { states ->
                    featuredProductsAdapter.productStates = states
                    featuredProductsAdapter.notifyDataSetChanged()
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

        viewModel.hotDealsProducts.observe(viewLifecycleOwner) { products ->
            hotDealsAdapter.products = products
        }

        viewModel.getHotDealsProducts()

        viewLifecycleOwner.lifecycleScope.launch {

            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productStateFlow().collect { states ->
                    hotDealsAdapter.productStates = states
                    hotDealsAdapter.notifyDataSetChanged()
                }
            }
        }

        binding.rvHotDealsLayout.featuredProducts.tvHeaderTitle.text = "Hot Deals of the Day"
        binding.rvHotDealsLayout.featuredProducts.ibHeaderButton.setOnClickListener {
            Toast.makeText(requireContext(), "Hot Deals Clicked", Toast.LENGTH_SHORT).show()
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
                    requireContext().productDataStore.updateData { current ->
                        val updatedProducts = current.products.toMutableMap()
                        val currentState = updatedProducts[p.id] ?: ProductState()
                        updatedProducts[p.id] =
                            currentState.copy(isFavourite = !currentState.isFavourite)
                        current.copy(products = updatedProducts)
                    }
                }
            },
            onCartIncrement = { p ->
                viewLifecycleOwner.lifecycleScope.launch {
                    requireContext().productDataStore.updateData { current ->
                        val updatedProducts = current.products.toMutableMap()
                        val currentState = updatedProducts[p.id] ?: ProductState()
                        updatedProducts[p.id] =
                            currentState.copy(cartCount = currentState.cartCount + 1)
                        current.copy(products = updatedProducts)
                    }
                }
            },
            onCartDecrement = { p ->
                viewLifecycleOwner.lifecycleScope.launch {
                    requireContext().productDataStore.updateData { current ->
                        val updatedProducts = current.products.toMutableMap()
                        val currentState = updatedProducts[p.id] ?: ProductState()
                        updatedProducts[p.id] =
                            currentState.copy(cartCount = currentState.cartCount - 1)
                        current.copy(products = updatedProducts)
                    }
                }

            }
        )

        recyclerView.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            itemDecoration?.let {
                addItemDecoration(it)
            }
        }

        return adapter
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

    private suspend fun getAllProductState(): Map<Int, ProductState> {
        return requireContext().productDataStore.data.first().products
    }


//    private suspend fun getProductState(productId: Int): ProductState {
//        val productStates = requireContext().productDataStore.data.first()
//        return productStates.products[productId] ?: ProductState()
//    }
//
//    private suspend fun updateProductState(productId: Int, state: ProductState) {
//        requireContext().productDataStore.updateData { current ->
//            val updatedProducts = current.products.toMutableMap()
//            updatedProducts[productId] = state
//
//            current.copy(products = updatedProducts)
//        }
//    }


}