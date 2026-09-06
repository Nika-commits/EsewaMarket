package com.example.xml_app.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
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
        binding.rvProductsGrid.apply {
            adapter = productsAdapter
            layoutManager = GridLayoutManager(
                requireContext(),
                2
            )

            addItemDecoration(
                SpacingItemDecoration(2, spacing)
            )
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.products.collectLatest { product ->
                    Log.d("Search", "Search Results: ${product.size}")
                    productsAdapter.products = product
                }
            }
        }
        viewModel.getSearchedProducts()
    }
}
