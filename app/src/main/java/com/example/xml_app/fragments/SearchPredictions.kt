package com.example.xml_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.xml_app.databinding.FragmentSearchSuggestionsBinding
import com.example.xml_app.viewModel.SearchViewModel

class SearchPredictions : Fragment() {
    private var _binding: FragmentSearchSuggestionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchSuggestionsBinding.inflate(inflater, container, false)
        return binding.root
    }
}