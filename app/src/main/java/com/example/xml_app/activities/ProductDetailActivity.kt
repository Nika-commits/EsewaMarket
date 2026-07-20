package com.example.xml_app.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.xml_app.adapters.ProductCarouselAdapter
import com.example.xml_app.databinding.ActivityProductDetailBinding
import com.example.xml_app.viewModel.ProductDetailsViewModel

class ProductDetailActivity : AppCompatActivity() {

    private val viewModel: ProductDetailsViewModel by viewModels()

    private lateinit var binding: ActivityProductDetailBinding

    private lateinit var carouselAdapter: ProductCarouselAdapter

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

        viewModel.product.observe(this) { product ->
            carouselAdapter.imageUrls = product.imageUrls
            binding.tvProductName.text = product.name
            binding.tvProductPrice.text = "Rs. " + product.price.toFloat().toString()
            binding.tvProductStatus.text = product.status

            binding.addToCartContainer.tvAddToCartProductName.text =
                product.name + " - " + product.brand
            binding.addToCartContainer.tvAddToCartPrice.text = product.price.toFloat().toString()

        }
        viewModel.getProduct(productId)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}