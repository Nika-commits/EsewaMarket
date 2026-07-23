package com.example.xml_app.activities

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.xml_app.R
import com.example.xml_app.adapters.ProductCarouselAdapter
import com.example.xml_app.data.productDataStore
import com.example.xml_app.databinding.ActivityProductDetailBinding
import com.example.xml_app.models.ProductState
import com.example.xml_app.viewModel.ProductDetailsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private val viewModel: ProductDetailsViewModel by viewModels()

    private lateinit var binding: ActivityProductDetailBinding

    private lateinit var carouselAdapter: ProductCarouselAdapter

    private fun productStateFlow(): Flow<Map<Int, ProductState>> =
        this.productDataStore.data.map { products ->
            products.products
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        carouselAdapter = ProductCarouselAdapter {}
        binding.vpProductViewPager.adapter = carouselAdapter
        val productId = intent.getIntExtra("id", 0)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productStateFlow().collect { states ->
                    val state = states[productId] ?: ProductState()

                    if (state.isFavourite) {
                        binding.ibFavourite.setImageResource(R.drawable.ic_filled_favourite)
                        binding.ibFavourite.imageTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    this@ProductDetailActivity,
                                    R.color.surface
                                )
                            )
                    } else {
                        binding.ibFavourite.setImageResource(R.drawable.ic_fav)
                        binding.ibFavourite.imageTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    this@ProductDetailActivity,
                                    R.color.surface
                                )
                            )
                    }
                }
            }
        }
        viewModel.product.observe(this) { product ->
            carouselAdapter.imageUrls = product.imageUrls
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "Rs. " + product.price.toFloat().toString()
            binding.tvProductStatus.text = product.status
            binding.tvProductDescription.text =
                HtmlCompat.fromHtml(product.description, HtmlCompat.FROM_HTML_MODE_LEGACY)

            binding.addToCartContainer.tvAddToCartProductName.text =
                product.name + " - " + product.brand
            binding.addToCartContainer.tvAddToCartPrice.text = product.price.toFloat().toString()

        }
        viewModel.getProduct(productId)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ibFavourite.setOnClickListener {
            lifecycleScope.launch {
                applicationContext.productDataStore.updateData { current ->
                    val updatedProducts = current.products.toMutableMap()
                    val currentState = updatedProducts[productId] ?: ProductState()

                    updatedProducts[productId] =
                        currentState.copy(isFavourite = !currentState.isFavourite)
                    current.copy(products = updatedProducts)
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                productStateFlow().collect { state ->
                    val currentState = state[productId] ?: ProductState()

                    if (currentState.cartCount > 0) {
                        binding.addToCartContainer.btnAddToCart.visibility = View.GONE
                        binding.addToCartContainer.llCartCountStepper.visibility = View.VISIBLE
                        binding.addToCartContainer.tvCartCount.text =
                            currentState.cartCount.toString()
                    } else {
                        binding.addToCartContainer.llCartCountStepper.visibility = View.GONE
                        binding.addToCartContainer.btnAddToCart.visibility = View.VISIBLE
                    }

                }
            }
        }

        binding.addToCartContainer.btnAddToCart.setOnClickListener {
            lifecycleScope.launch {
                applicationContext.productDataStore.updateData { current ->
                    val updatedProducts = current.products.toMutableMap()
                    val currentState = updatedProducts[productId] ?: ProductState()

                    updatedProducts[productId] =
                        currentState.copy(cartCount = currentState.cartCount + 1)
                    current.copy(products = updatedProducts)
                }
            }
        }

        binding.addToCartContainer.ibCartIncrement.setOnClickListener {
            lifecycleScope.launch {
                applicationContext.productDataStore.updateData { current ->
                    val updatedProducts = current.products.toMutableMap()
                    val currentState = updatedProducts[productId] ?: ProductState()

                    updatedProducts[productId] =
                        currentState.copy(cartCount = currentState.cartCount + 1)
                    current.copy(updatedProducts)
                }
            }
        }

        binding.addToCartContainer.ibCartDecrement.setOnClickListener {
            lifecycleScope.launch {
                applicationContext.productDataStore.updateData { current ->
                    val updatedProducts = current.products.toMutableMap()
                    val currentState = updatedProducts[productId] ?: ProductState()

                    updatedProducts[productId] =
                        currentState.copy(cartCount = currentState.cartCount - 1)
                    current.copy(updatedProducts)
                }
            }
        }
    }
}