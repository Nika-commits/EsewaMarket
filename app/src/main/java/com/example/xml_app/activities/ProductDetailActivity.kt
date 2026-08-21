package com.example.xml_app.activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xml_app.R
import com.example.xml_app.adapters.ColorSelectorAdapter
import com.example.xml_app.adapters.ProductCarouselAdapter
import com.example.xml_app.databinding.ActivityProductDetailBinding
import com.example.xml_app.models.ProductUiModel
import com.example.xml_app.ui.SizeSelectorButton
import com.example.xml_app.utils.CustomSnackbar
import com.example.xml_app.utils.HorizontalItemDecoration
import com.example.xml_app.viewModel.ProductDetailsViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {
    companion object {
        const val ID = "PRODUCT_ID"
        fun startActivity(
            context: Context,
            productId: Int
        ) {
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(ID, productId)
            }
            context.startActivity(intent)
        }
    }

    private val viewModel: ProductDetailsViewModel by viewModels()
    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var carouselAdapter: ProductCarouselAdapter
    private lateinit var colorAdapter: ColorSelectorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupWindowInsets()
        setupCarousel()
        setupColorSelector()
        setupListeners()
        observeViewModel()

        val productId = intent.getIntExtra(ID, -1)
        if (productId == -1) {
            finish()
            return
        }

        viewModel.getProduct(productId)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }

    private fun setupCarousel() {
        carouselAdapter = ProductCarouselAdapter(
            onImageClick = {
                Toast.makeText(
                    this,
                    "Image Clicked",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.vpProductViewPager.adapter = carouselAdapter

        TabLayoutMediator(
            binding.imageIndicator,
            binding.vpProductViewPager
        ) { tab, _ ->
            tab.setCustomView(R.layout.item_indicator)
        }.attach()
    }

    private fun setupColorSelector() {
        colorAdapter = ColorSelectorAdapter(
            onColorChange = { color ->
                viewModel.selectColor(color)
            }
        )

        binding.rvColorSelector.apply {
            layoutManager = LinearLayoutManager(
                this@ProductDetailActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            adapter = colorAdapter

            addItemDecoration(
                HorizontalItemDecoration(24)
            )
        }
    }

    private fun setupListeners() {
        val favMessage = "You must be logged in to add to favourites"
        val cartMesssage = "You must be logged in to use Cart"

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.ibFavourite.setOnClickListener {
            if (viewModel.isLoggedIn()) {
                viewModel.toggleFavourite()
            } else {
                showLoginSnackbar(favMessage)
            }
        }

        binding.addToCartContainer.btnAddToCart.setOnClickListener {
            if (viewModel.isLoggedIn()) {
                viewModel.cartIncrement()
            } else {
                showLoginSnackbar(cartMesssage)
            }
        }

        binding.addToCartContainer.ibCartIncrement.setOnClickListener {
            if (viewModel.isLoggedIn()) {
                viewModel.cartIncrement()
            } else {
                showLoginSnackbar(cartMesssage)
            }

        }

        binding.addToCartContainer.ibCartDecrement.setOnClickListener {
            if (viewModel.isLoggedIn()) {
                viewModel.decrementCart()
            } else {
                showLoginSnackbar(cartMesssage)
            }
        }
    }

    private fun showLoginSnackbar(message: String) {
        CustomSnackbar.show(
            view = binding.root,
            context = this,
            text = message,
            anchorView = binding.addToCartContainer.root,
            actionText = "Login",
            action = {
                val intent = Intent(this, AuthActivity::class.java).apply {
                    putExtra(AuthActivity.DESTINATION, AuthActivity.LOGIN)
                }
                startActivity(intent)
            }
        )

    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isProductLoading.collect { loading ->
                        binding.loading.isVisible = loading
                        binding.productDetailScrollView.isVisible = !loading
                        binding.addToCartContainer.root.isVisible = !loading
                    }
                }

                launch {
                    viewModel.product.collect { productUiModel ->
                        productUiModel ?: return@collect
                        renderProduct(productUiModel)
                    }
                }

                launch {
                    viewModel.selectedColor.collect { color ->
                        colorAdapter.selectedColor = color
                    }
                }
            }
        }
    }

    private fun renderProduct(
        productUiModel: ProductUiModel
    ) {
        val product = productUiModel.product

        carouselAdapter.imageUrls = product.imageUrls

        binding.tvProductName.text = product.name

        binding.tvProductPrice.text =
            "Rs. ${product.price.toFloat()}"

        binding.tvProductStatus.text =
            product.status

        binding.tvProductDescription.text =
            HtmlCompat.fromHtml(
                product.description,
                HtmlCompat.FROM_HTML_MODE_LEGACY
            )

        binding.addToCartContainer.tvAddToCartProductName.text =
            "${product.name} - ${product.brand}"

        binding.addToCartContainer.tvAddToCartPrice.text =
            product.price.toFloat().toString()

        renderFavourite(productUiModel.isFavourite)

        renderCart(productUiModel.cartCount)

        setupSizes(product.sizes)

        colorAdapter.colors = product.colors
    }

    private fun renderFavourite(
        isFavourite: Boolean
    ) {
        val icon = if (isFavourite) {
            R.drawable.ic_filled_favourite
        } else {
            R.drawable.ic_fav
        }

        binding.ibFavourite.setImageResource(icon)

        binding.ibFavourite.imageTintList =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    this,
                    R.color.surface
                )
            )
    }

    private fun renderCart(
        cartCount: Int
    ) {
        if (cartCount > 0) {

            binding.addToCartContainer.btnAddToCart.visibility =
                View.GONE

            binding.addToCartContainer.llCartCountStepper.visibility =
                View.VISIBLE

            binding.addToCartContainer.tvCartCount.text =
                cartCount.toString()

        } else {

            binding.addToCartContainer.llCartCountStepper.visibility =
                View.GONE

            binding.addToCartContainer.btnAddToCart.visibility =
                View.VISIBLE
        }
    }

    private fun setupSizes(
        sizes: List<String>?
    ) {
        binding.sizeToggleGroup.removeAllViews()

        sizes?.forEach { size ->

            val button = SizeSelectorButton(
                context = this,
                size = size
            ).apply {
                id = View.generateViewId()
            }

            binding.sizeToggleGroup.addView(button)
        }

        binding.sizeToggleGroup.clearOnButtonCheckedListeners()

        binding.sizeToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val button =
                group.findViewById<SizeSelectorButton>(checkedId)
                    ?: return@addOnButtonCheckedListener

            if (isChecked) {
                button.applySelectedStyle()
            } else {
                button.applyDefaultStyle()
            }
        }
    }
}