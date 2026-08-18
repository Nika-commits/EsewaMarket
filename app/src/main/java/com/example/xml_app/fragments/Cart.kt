package com.example.xml_app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
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
import com.example.xml_app.activities.AuthActivity
import com.example.xml_app.activities.CheckoutActivity
import com.example.xml_app.activities.ProductDetailActivity
import com.example.xml_app.adapters.CartAdapter
import com.example.xml_app.databinding.FragmentCartBinding
import com.example.xml_app.ui.modals.DeleteCartBottomSheet
import com.example.xml_app.utils.CustomSnackbar
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

        viewModel.initializeUser()
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
            onProductClick = { ProductDetailActivity.startActivity(requireContext(), it) },

            onCartIncrement = { id ->
                viewModel.cartIncrement(id)
            },

            onCartDecrement = { id, count ->
                if (count == 1) {
                    DeleteCartBottomSheet(
                        onDelete = {
                            viewModel.cartDecrement(id)
                        }
                    ).show(
                        childFragmentManager,
                        "DeleteCartBottomSheet"
                    )
                } else {
                    viewModel.cartDecrement(id)
                }
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

        binding.btnCheckout.setOnClickListener {
            val bottomNavigation =
                requireActivity().findViewById<LinearLayout>(R.id.bottomNavigation)
            if (viewModel.user.value == null) {
                CustomSnackbar.show(
                    view = binding.root,
                    context = requireContext(),
                    text = "Log in to Checkout",
                    anchorView = bottomNavigation,
                    actionText = "LOGIN",
                    action = {
                        Intent(requireContext(), AuthActivity::class.java).apply {
                            putExtra(AuthActivity.DESTINATION, AuthActivity.LOGIN)
                        }.also {
                            startActivity(it)
                        }
                    }
                )
                return@setOnClickListener
            }

            val cartSize = viewModel.productsInCart.value.size
            if (cartSize == 0) {
                CustomSnackbar.show(
                    view = binding.root,
                    context = requireContext(),
                    text = "Cannot checkout with an empty Cart.",
                    anchorView = bottomNavigation,
//                    actionText = "LOGIN",
//                    action = {
//                        Intent(requireContext(), AuthActivity::class.java).apply {
//                            putExtra(AuthActivity.DESTINATION, AuthActivity.LOGIN)
//                        }.also {
//                            startActivity(it)
//                        }
//                    }
                )
                return@setOnClickListener
            }
            Intent(requireContext(), CheckoutActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}