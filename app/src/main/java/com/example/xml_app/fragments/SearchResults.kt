package com.example.xml_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.xml_app.R
import com.example.xml_app.adapters.ProductsAdapter
import com.example.xml_app.databinding.FragmentSearchResultsBinding
import com.example.xml_app.utils.SpacingItemDecoration
import com.example.xml_app.viewModel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchResults : Fragment() {
    private var _binding: FragmentSearchResultsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var productsAdapter: ProductsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initializeUserCartAndFavourites()

        setupRecyclerView()
        setupDropDownFilter()
    }

    fun setupRecyclerView() {
        productsAdapter = ProductsAdapter(
            onProductClick = {},
            onFavouriteClick = {},
            onCartIncrement = { count, pro ->
            },
            onCartDecrement = { count, pro ->
            }
        )
        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_medium)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvProductsGrid.apply {
            adapter = productsAdapter
            this.layoutManager = layoutManager

            addItemDecoration(
                SpacingItemDecoration(2, spacing)
            )
        }

        binding.rvProductsGrid.addOnScrollListener(
            object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy <= 0) return

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    val isNearBottom = visibleItemCount + firstVisibleItemPosition >= totalItemCount - 2

                    if (isNearBottom) {
                        viewModel.loadNextPage()
                    }
                }
            }
        )

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collectLatest { product ->
                    Log.d("Search", "Search Results: ${product.size}")
                    productsAdapter.products = product
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isLoadingProducts.collectLatest { binding.loader.isVisible = it }
            }
        }

        viewModel.getSearchedProducts()
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
