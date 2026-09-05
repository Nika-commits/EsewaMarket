package com.example.xml_app.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.xml_app.databinding.FragmentSearchBinding
import com.example.xml_app.viewModel.SearchViewModel

class Search : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etSearch.requestFocus()
        binding.etSearch.post {
            val imm = requireContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.etSearch, InputMethodManager.SHOW_IMPLICIT)
        }

        setupSearchBox()
    }

    fun setupSearchBox() {
        val searchBox = binding.etSearch
        viewModel.onChange(searchBox.text.toString())
        binding.tvSearch.setOnClickListener {

        }
        binding.layoutSearchBox.setStartIconOnClickListener {
//            findNavController().navigate(ApiRoute.Home) {
//                launchSingleTop = true
//                restoreState = true
//            }
            findNavController().popBackStack()
        }
    }
}