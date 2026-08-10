package com.example.xml_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.xml_app.R
import com.example.xml_app.databinding.FragmentFavouriteBinding

class Favourite : Fragment() {
    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavouriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyEdgeToEdgeInsets()
        setupToolbar()
        setupFavourites()
    }

    fun applyEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun setupToolbar() {
        val toolbar = binding.favouriteToolbar.toolbar
        toolbar.title = "Favourites"

        (requireContext() as AppCompatActivity).apply {
            setSupportActionBar(toolbar)

            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
            }
        }

        toolbar.navigationIcon?.setTint(
            ContextCompat.getColor(requireContext(), R.color.lightGrey)
        )

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    fun setupFavourites() {
        val composeView = binding.composeFavourite
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    Text("Hello")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}