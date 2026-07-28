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
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xml_app.R
import com.example.xml_app.adapters.CartAdapter
import com.example.xml_app.data.productDataStore
import com.example.xml_app.databinding.FragmentCartBinding
import com.example.xml_app.models.ProductState
import com.example.xml_app.viewModel.CartViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class Cart : Fragment() {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CartViewModel by viewModels()

    private lateinit var cartAdapter: CartAdapter

    fun productStateFlow(): Flow<Map<Int, ProductState>> =
        requireContext().productDataStore.data.map { products ->
            products.products
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

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
        setupCart()
        setupRecyclerView()
        observerCartData()
    }

    fun applyEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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

    fun setupCart() {
        viewLifecycleOwner.lifecycleScope.launch {
            productStateFlow().collect { products ->
                val cartIds = products.filter { (_, productState) ->
                    productState.cartCount > 0
                }.keys.toList()
                viewModel.getProductsInCart(cartIds)
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter()

        binding.rvCartProducts.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observerCartData() {
        viewModel.productsInCart.observe(viewLifecycleOwner) { products ->
            cartAdapter.products = products
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}