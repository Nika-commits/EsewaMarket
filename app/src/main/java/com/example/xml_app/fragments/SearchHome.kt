package com.example.xml_app.fragments

import android.content.Intent
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xml_app.R
import com.example.xml_app.activities.AuthActivity
import com.example.xml_app.activities.ProductDetailActivity
import com.example.xml_app.adapters.search.SearchHistoryAdapter
import com.example.xml_app.adapters.search.SearchMostPopularProductAdapter
import com.example.xml_app.databinding.FragmentSearchHomeBinding
import com.example.xml_app.navigation.SearchRoute
import com.example.xml_app.ui.state.SearchHistoryUiState
import com.example.xml_app.ui.state.SearchMostPopularProductsUiState
import com.example.xml_app.utils.HorizontalItemDecoration
import com.example.xml_app.utils.SearchHistoryItemDecoration
import com.example.xml_app.viewModel.SearchViewModel
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchHome : Fragment() {
    private var _binding: FragmentSearchHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels(
        ownerProducer = { requireParentFragment().requireParentFragment() }
    )
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var mostPopularSearchAdapter: SearchMostPopularProductAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchHistoryRecyclerView()
        setupClearAll()
        setupMostPopularSearch()
    }

    private fun setupSearchHistoryRecyclerView() {
        searchHistoryAdapter = SearchHistoryAdapter(
            onSearchHistoryClick = {
                viewModel.onChange(it)
                findNavController().navigate(SearchRoute.Results) {
                    popUpTo<SearchRoute.Results> {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            },
            onDeleteClick = {
                viewModel.deleteSearchHistory(
                    it
                )
            }
        )

        binding.rvSearchHistory.apply {
            adapter = searchHistoryAdapter
            layoutManager = FlexboxLayoutManager(
                requireContext(),
                com.google.android.flexbox.FlexDirection.ROW,
                FlexWrap.WRAP
            )
            addItemDecoration(
                SearchHistoryItemDecoration()
            )
        }

        binding.btnLogin.setOnClickListener {
            Log.d("Search", "Login button Clicked")
            Intent(requireContext(), AuthActivity::class.java).apply {
                putExtra(AuthActivity.DESTINATION, AuthActivity.LOGIN)
            }.also { startActivity(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchHistoryUiState.collectLatest { state ->
                    when (state) {
                        is SearchHistoryUiState.Unauth -> {
                            binding.llLoginMessage.visibility = View.VISIBLE
                            binding.rvSearchHistory.visibility = View.GONE
                            binding.btnClearAll.visibility = View.GONE
                        }

                        is SearchHistoryUiState.Loading,
                        SearchHistoryUiState.Error
                            -> {
                            binding.llLoginMessage.visibility = View.GONE
                            binding.rvSearchHistory.visibility = View.GONE
                            binding.btnClearAll.visibility = View.GONE
                        }

                        is SearchHistoryUiState.Success -> {
                            binding.llLoginMessage.visibility = View.GONE
                            binding.rvSearchHistory.visibility = View.VISIBLE
                            binding.btnClearAll.visibility = View.VISIBLE
                            searchHistoryAdapter.searchHistory = state.histories
                        }
                    }
                }
            }
        }
    }

    private fun setupClearAll() {
        binding.btnClearAll.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    private fun setupMostPopularSearch() {
        viewModel.getMostPopularProducts()
        mostPopularSearchAdapter = SearchMostPopularProductAdapter {
            ProductDetailActivity.startActivity(requireContext(), it)
        }

        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_low)

        binding.rvPopularSearch.apply {
            adapter = mostPopularSearchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(HorizontalItemDecoration(spacing))
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchMostPopularProductUiState.collectLatest { state ->
                    when (state) {
                        is SearchMostPopularProductsUiState.Loading -> {

                        }

                        is SearchMostPopularProductsUiState.Error -> {

                        }

                        is SearchMostPopularProductsUiState.Success -> {
                            mostPopularSearchAdapter.products = state.products
                        }
                    }
                }
            }
        }
    }
}