package com.example.xml_app.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.createGraph
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.fragment
import com.example.xml_app.R
import com.example.xml_app.databinding.FragmentSearchBinding
import com.example.xml_app.navigation.SearchRoute
import com.example.xml_app.viewModel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class Search : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var nestedNavController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val nestedNavHostFragment = childFragmentManager.findFragmentById(R.id.searchContainer) as NavHostFragment
        nestedNavController = nestedNavHostFragment.navController
        nestedNavController.graph = nestedNavController.createGraph(
            startDestination = SearchRoute.Suggestions,
        ) {
            fragment<SearchResults, SearchRoute.Results> { label = "Results" }
            fragment<SearchPredictions, SearchRoute.Suggestions> { label = "Suggestions" }
        }

        binding.etSearch.requestFocus()
        binding.etSearch.post {
            val imm = requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        setupSearchBox()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.suggestions.collectLatest { Log.d("Search", "Suggestions from Search: ${it.size}") }
            }
        }
    }

    fun setupSearchBox() {
        binding.etSearch.doAfterTextChanged { text ->
            viewModel.onChange(text?.toString().orEmpty())
        }

        binding.layoutSearchBox.setStartIconOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvSearch.setOnClickListener {
            nestedNavController.navigate(SearchRoute.Results) {
                launchSingleTop = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}