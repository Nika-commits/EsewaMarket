package com.example.xml_app.fragments

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.component1
import androidx.core.util.component2
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
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.activities.AuthActivity
import com.example.xml_app.activities.CheckoutActivity
import com.example.xml_app.activities.ProductDetailActivity
import com.example.xml_app.adapters.CartAdapter
import com.example.xml_app.adapters.RecommendedProductsAdapter
import com.example.xml_app.adapters.cart.CartCartItemsAdapter
import com.example.xml_app.adapters.home.HomeRecommendedHeaderAdapter
import com.example.xml_app.adapters.home.HomeRecommendedLoadingAdapter
import com.example.xml_app.databinding.FragmentCartBinding
import com.example.xml_app.ui.modals.DeleteCartBottomSheet
import com.example.xml_app.utils.CustomSnackbar
import com.example.xml_app.utils.HomeConcatAdapterSpacing
import com.example.xml_app.viewModel.CartViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Cart : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartCartItemsAdapter: CartCartItemsAdapter
    private lateinit var recommendationHeaderAdapter: HomeRecommendedHeaderAdapter
    private lateinit var recommendedAdapter: RecommendedProductsAdapter
    private lateinit var recommendedLoadingAdapter: HomeRecommendedLoadingAdapter
    private lateinit var concatAdapter: ConcatAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initializeUser()
//        applyEdgeToEdgeInsets()
        setupToolbar()
        setupRecyclerView()
        observerCartData()
    }

    fun applyEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    fun setupToolbar() {
        val toolbar = binding.cartToolbar.toolbar
        toolbar.title = "My Cart"
        (requireContext() as AppCompatActivity).apply {
            setSupportActionBar(toolbar)

            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
            }
        }

        toolbar.navigationIcon?.setTint(
            ContextCompat.getColor(requireContext(), R.color.black)
        )

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onProductClick = { ProductDetailActivity.startActivity(requireContext(), it) },
            onCartIncrement = { id ->
                viewModel.cartIncrement(id)
            },
            onCartDecrement = { id, count ->
                if (count == 1) {
                    DeleteCartBottomSheet(
                        onDelete = {
                            viewModel.cartDecrement(id)
                        }
                    ).show(
                        childFragmentManager,
                        "DeleteCartBottomSheet"
                    )
                } else {
                    viewModel.cartDecrement(id)
                }
            }
        )

        cartCartItemsAdapter = CartCartItemsAdapter(
            cartAdapter = cartAdapter
        )

        recommendationHeaderAdapter = HomeRecommendedHeaderAdapter(
            onSeeAllClick = {}
        )

        recommendedAdapter = RecommendedProductsAdapter(
            onProductClick = {},
            onFavouriteClick = {},
            onCartIncrement = { product, _ ->
                viewModel.cartIncrement(product.id)
            },
            onCartDecrement = { product, count ->
                if (count == 1) {
                    DeleteCartBottomSheet(
                        onDelete = { viewModel.cartDecrement(product.id) }
                    ).show(
                        childFragmentManager,
                        "DeleteCartBottomSheet"
                    )
                } else {
                    viewModel.cartDecrement(product.id)
                }

            },
        )
        recommendedLoadingAdapter = HomeRecommendedLoadingAdapter()

        concatAdapter = ConcatAdapter(
            cartCartItemsAdapter,
            recommendationHeaderAdapter,
            recommendedAdapter,
            recommendedLoadingAdapter
        )

        val gridLayoutManager = GridLayoutManager(
            requireContext(), 2
        )

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val (adapter, _) = concatAdapter.getWrappedAdapterAndPosition(position)
                return if (adapter == recommendedAdapter) 1 else 2
            }
        }

        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_large)
        binding.rvCartContent.apply {
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

                        outRect.top = if (position == 0 || position == 1) {
                            0
                        } else {
                            spacing / 2
                        }
                    }
                }
            )
        }
    }

    private fun observerCartData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productsInCart.collect { products ->
                        cartAdapter.products = products
                        cartCartItemsAdapter.setEmpty(
                            products.isEmpty()
                        )
                    }
                }

                launch {
                    viewModel.totalPrice.collect {
                        binding.tvTotalPrice.text = it.toString()
                    }
                }

                launch {
                    viewModel.isCartLoading.collect { isLoading ->
                        binding.cartLoading.isVisible = isLoading
                        binding.rvCartContent.isVisible = !isLoading
                    }
                }
            }
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

                    recommendedLoadingAdapter.setLoading(isInitialLoading || isLoadingMore)
                }
            }
        }

        binding.btnCheckout.setOnClickListener {
            val bottomNavigation =
                requireActivity().findViewById<LinearLayout>(R.id.bottomNavigation)
            if (viewModel.user.value == null) {
                CustomSnackbar.show(
                    view = binding.root,
                    context = requireContext(),
                    text = "Log in to Checkout",
                    anchorView = bottomNavigation,
                    actionText = "LOGIN",
                    action = {
                        Intent(requireContext(), AuthActivity::class.java).apply {
                            putExtra(AuthActivity.DESTINATION, AuthActivity.LOGIN)
                        }.also {
                            startActivity(it)
                        }
                    }
                )
                return@setOnClickListener
            }

            val cartSize = viewModel.productsInCart.value.size
            if (cartSize == 0) {
                CustomSnackbar.show(
                    view = binding.root,
                    context = requireContext(),
                    text = "Cannot checkout with an empty Cart.",
                    anchorView = bottomNavigation,
//                    actionText = "LOGIN",
//                    action = {
//                        Intent(requireContext(), AuthActivity::class.java).apply {
//                            putExtra(AuthActivity.DESTINATION, AuthActivity.LOGIN)
//                        }.also {
//                            startActivity(it)
//                        }
//                    }
                )
                return@setOnClickListener
            }
            Intent(requireContext(), CheckoutActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}