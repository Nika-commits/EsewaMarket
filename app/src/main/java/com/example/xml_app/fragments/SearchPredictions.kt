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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xml_app.adapters.search.SearchSuggestionsAdapter
import com.example.xml_app.databinding.FragmentSearchSuggestionsBinding
import com.example.xml_app.viewModel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchPredictions : Fragment() {
    private var _binding: FragmentSearchSuggestionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels(
        ownerProducer = { requireParentFragment().requireParentFragment() }
    )
    private lateinit var searchSuggestionsAdapter: SearchSuggestionsAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchSuggestionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeSuggestions()
    }

    private fun setupRecyclerView() {
        searchSuggestionsAdapter = SearchSuggestionsAdapter { suggestion ->
            Log.d("Search", "Clicked: $suggestion")
        }
        binding.rvSearchSuggestions.adapter = searchSuggestionsAdapter
        binding.rvSearchSuggestions.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeSuggestions() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.suggestions.collectLatest { suggestions ->
                    Log.d("Search", "Value is being collected: ${suggestions.size}")
                    searchSuggestionsAdapter.submitList(suggestions)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}