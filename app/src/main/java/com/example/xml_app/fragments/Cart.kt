package com.example.xml_app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xml_app.R
import com.example.xml_app.activities.ProductDetailActivity
import com.example.xml_app.adapters.CartAdapter
import com.example.xml_app.databinding.FragmentCartBinding
import com.example.xml_app.viewModel.CartViewModel
import kotlinx.coroutines.launch


class Cart : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyEdgeToEdgeInsets()
        setupToolbar()
        setupRecyclerView()
        observerCartData()
    }

    fun applyEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }

    fun setupToolbar() {
        val toolbar = binding.cartToolbar.toolbar
        toolbar.title = "My Cart"
        (requireContext() as AppCompatActivity).apply {
            setSupportActionBar(toolbar)

            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
            }
        }

        toolbar.navigationIcon?.setTint(
            ContextCompat.getColor(requireContext(), R.color.black)
        )

        toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onProductClick = { id ->
                Intent(requireContext(), ProductDetailActivity::class.java).also {
                    it.putExtra("id", id)
                    startActivity(it)
                }
            },

            onCartIncrement = { id ->
                if (id != null) viewModel.cartIncrement(id)
            },

            onCartDecrement = { id ->
                if (id != null) viewModel.cartDecrement(id)
            }
        )

        binding.rvCartProducts.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null

        }
    }

    private fun observerCartData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.productsInCart.collect { products ->
                        cartAdapter.products = products

                        if (products.isEmpty()) {
                            binding.emptyCart.root.visibility = View.VISIBLE
                            binding.rvCartProducts.visibility = View.GONE
                        } else {
                            binding.emptyCart.root.visibility = View.GONE
                            binding.rvCartProducts.visibility = View.VISIBLE
                        }
                    }
                }

                launch {
                    viewModel.user.collect { user ->
                        if (user == null) {
                            binding.emptyCart.root.visibility = View.VISIBLE
                            binding.rvCartProducts.visibility = View.GONE
                        }
                    }
                }

                launch {
                    viewModel.totalPrice.collect {
                        binding.tvTotalPrice.text = it.toString()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}