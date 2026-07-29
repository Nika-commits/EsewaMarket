package com.example.xml_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.xml_app.R
import com.example.xml_app.databinding.FragmentMoreBinding

private const val ARG_PARAM1 = "param1"

class More : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        applyEdgeToEdgeInsets()
    }

    private fun applyEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun setupToolbar() {
        val toolbar = binding.moreToolbar.toolbar
        toolbar.title = "More"

        (requireContext() as AppCompatActivity).apply {
            setSupportActionBar(toolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
            }
        }

        toolbar.navigationIcon?.setTint(
            ContextCompat.getColor(requireContext(), R.color.textDark)
        )

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
}