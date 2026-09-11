package com.example.xml_app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.activities.AuthActivity
import com.example.xml_app.activities.ProductDetailActivity
import com.example.xml_app.adapters.ProductsAdapter
import com.example.xml_app.adapters.home.HomeRecommendedLoadingAdapter
import com.example.xml_app.databinding.FragmentSearchResultsBinding
import com.example.xml_app.navigation.ApiRoute
import com.example.xml_app.ui.modals.DeleteCartBottomSheet
import com.example.xml_app.utils.CustomSnackBar
import com.example.xml_app.utils.SpacingItemDecoration
import com.example.xml_app.viewModel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchResults : Fragment() {
    private var _binding: FragmentSearchResultsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels(
        ownerProducer = { requireParentFragment().requireParentFragment() }
    )
    private lateinit var productsAdapter: ProductsAdapter
    private lateinit var loadingAdapter: HomeRecommendedLoadingAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initialize()
        setupRecyclerView()
        setupDropDownFilter()
    }

    fun setupRecyclerView() {

        productsAdapter = ProductsAdapter(
            onProductClick = {
                ProductDetailActivity.startActivity(
                    requireContext(),
                    productId = it.id
                )
            },
            onFavouriteClick = { p, isFavourite ->
                if (!viewModel.isLoggedIn()) {
                    showLoginSnackbar("Log in to add to favourites")
                    return@ProductsAdapter
                }
                viewModel.toggleFavourite(p.id)
                if (isFavourite) return@ProductsAdapter
                CustomSnackBar.show(
                    context = requireContext(),
                    view = binding.root,
                    text = "$(1) item added to favourites",
                    action = {
                        requireParentFragment()
                            .requireParentFragment()
                            .findNavController()
                            .navigate(ApiRoute.Favourite) {
                                popUpTo<ApiRoute.Search> {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                    },

                    actionText = "GOTO FAVOURITES"
                )
            },
            onCartIncrement = { p, count ->
                if (!viewModel.isLoggedIn()) {
                    showLoginSnackbar("Log in to add to cart")
                    return@ProductsAdapter
                }
                if (count != null) {
                    CustomSnackBar.show(
                        context = requireContext(),
                        view = binding.root,
                        text = "(1) item added to cart",
                        actionText = "GOTO CART",
                        action = {
                            requireParentFragment()
                                .requireParentFragment()
                                .findNavController()
                                .navigate(ApiRoute.Cart) {
                                    popUpTo<ApiRoute.Search> {
                                        inclusive = true
                                    }
                                    launchSingleTop = true
                                }
                        }
                    )
                }
                viewModel.incrementCart(p.id)
            },
            onCartDecrement = { p, count ->
                if (count == 1) {
                    DeleteCartBottomSheet(
                        onDelete = {
                            viewModel.decrementCart(p.id)
                        }
                    ).show(
                        childFragmentManager,
                        "DeleteCartBottomSheet"
                    )
                } else {
                    viewModel.decrementCart(p.id)
                }
            }
        )
        loadingAdapter = HomeRecommendedLoadingAdapter()

        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        val layoutManager = GridLayoutManager(requireContext(), 2)

        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position < productsAdapter.itemCount) {
                    1
                } else {
                    2
                }
            }
        }
        binding.rvProductsGrid.apply {
            adapter = ConcatAdapter(
                productsAdapter,
                loadingAdapter
            )
            this.layoutManager = layoutManager

            addItemDecoration(
                SpacingItemDecoration(2, spacing)
            )
            itemAnimator = null
        }

        binding.rvProductsGrid.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy <= 0) return

                    val lastCompletelyVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                    val totalItemCount = layoutManager.itemCount
                    val isAtBottom = lastCompletelyVisibleItem == totalItemCount - 1
                    if (isAtBottom) {
                        viewModel.loadNextPage()
                    }
                }
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collectLatest { product ->
                    productsAdapter.products = product
                }
            }
        }


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoadingProducts.collectLatest { loadingAdapter.setLoading(it) }
            }
        }

        viewModel.getSearchedProducts()
    }

    private fun showLoginSnackbar(text: String) {
        CustomSnackBar.show(
            context = requireContext(),
            view = binding.root,
            text = text,
            actionText = "LOGIN",
            action = {
                Intent(requireContext(), AuthActivity::class.java).apply {
                    putExtra(AuthActivity.DESTINATION, AuthActivity.LOGIN)
                }.also { startActivity(it) }
            }
        )
    }

    private fun setupDropDownFilter() {
        val button = binding.llDropdownFilters
        button.setOnClickListener { v: View ->
            val popup = PopupMenu(requireContext(), v)
            popup.menuInflater.inflate(R.menu.menu_product_filters, popup.menu)
            popup.setOnMenuItemClickListener { menuItem: MenuItem ->
                when (menuItem.itemId) {
                    R.id.bestSellers -> {
                        binding.tvFilters.text = menuItem.title
                        true
                    }

                    R.id.priceHighToLow -> {
                        binding.tvFilters.text = menuItem.title
                        true
                    }

                    R.id.priceLowToHigh -> {
                        binding.tvFilters.text = menuItem.title
                        true
                    }

                    else -> {
                        binding.tvFilters.text = menuItem.title
                        false
                    }
                }
            }

            popup.setOnDismissListener {

            }

            popup.show()
        }
    }
}
