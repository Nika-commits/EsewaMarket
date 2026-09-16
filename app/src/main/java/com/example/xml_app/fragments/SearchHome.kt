package com.example.xml_app.fragments

import android.os.Bundle
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
import com.example.xml_app.adapters.CartAdapter
import com.example.xml_app.adapters.search.SearchHistoryAdapter
import com.example.xml_app.databinding.FragmentSearchHomeBinding
import com.example.xml_app.navigation.SearchRoute
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
    private lateinit var mostPopularSearchAdapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchHistoryRecyclerView()
        setupClearAll()
    }

    private fun setupSearchHistoryRecyclerView() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.user.collectLatest { user ->
                        if (user == null) {
                            binding.tvLoginMessage.visibility = View.VISIBLE
                        } else {
                            binding.tvLoginMessage.visibility = View.GONE
                        }
                    }

                    launch {
                        viewModel.searchHistories.collectLatest { searchHistoryAdapter.searchHistory = it }
                    }
                }
            }
        }
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
    }

    private fun setupClearAll() {
        binding.btnClearAll.setOnClickListener {
            viewModel.clearSearchHistory()
        }
    }

    private fun setupMostPopularSearch() {
        mostPopularSearchAdapter = CartAdapter(
            onProductClick = {},
            onCartIncrement = {},
            onCartDecrement = { _, _ -> }
        )

        val spacing = resources.getDimensionPixelSize(R.dimen.spacing_medium)

        binding.rvPopularSearch.apply {
            adapter = mostPopularSearchAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(HorizontalItemDecoration(spacing))
        }
    }
}