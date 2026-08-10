package com.example.xml_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.example.xml_app.R
import com.example.xml_app.databinding.FragmentFavouriteBinding
import com.example.xml_app.models.Color
import com.example.xml_app.models.Product
import com.example.xml_app.viewModel.FavouriteViewModel

class Favourite : Fragment() {
    private var _binding: FragmentFavouriteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FavouriteViewModel by viewModels()

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
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MaterialTheme {
                        FavouriteScreen(viewModel)
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

@Composable
fun FavouriteCountText(count: Int) {
    Text("Items $count")
}

@Composable
fun FavouriteList(product: Product) {
    val sourceSans = FontFamily(
        Font(R.font.source_sans_regular, FontWeight.Normal),
        Font(R.font.source_sans_medium, FontWeight.Medium),
        Font(R.font.source_sans_semibold, FontWeight.Normal),
        Font(R.font.source_sans_bold, FontWeight.Bold),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(colorResource(R.color.surface)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = product.imageUrls.firstOrNull(),
            contentDescription = product.name,
            modifier = Modifier
                .padding(16.dp)
                .size(77.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = product.name,
                fontSize = 16.sp,
                fontFamily = sourceSans,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.textDark400)
            )
            Text(
                text = product.brand,
                fontSize = 10.sp,
                fontFamily = sourceSans,
                fontWeight = FontWeight.Normal,
                color = colorResource(R.color.textDark200),
                letterSpacing = 1.2.sp
            )
            Row(
                modifier = Modifier
                    .weight(1f),
            ) {
                Text(
                    text = "Rs.",
                    fontSize = 14.sp,
                    fontFamily = sourceSans,
                    fontWeight = FontWeight.Normal,
                    color = colorResource(R.color.textDark400)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = product.price.toString(),
                    fontSize = 20.sp,
                    fontFamily = sourceSans,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.textDark400)
                )
            }

        }

    }
}
@Composable
fun FavouriteScreen(viewModel: FavouriteViewModel) {
    val products by viewModel.favouriteProducts.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        FavouriteCountText(products.size)

        Spacer(modifier = Modifier.size(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                items = products,
                key = { product -> product.id }
            ) { product ->
                FavouriteList(product = product)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun Preview() {
    val product = Product(
        id = 6,
        name = "Classic Black T-Shirt",
        price = 650,
        description = """
                  <ul>
                      <li><strong>Material:</strong> 100% Cotton</li>
                      <li><strong>Fit:</strong> Regular Fit</li>
                      <li><strong>Neck:</strong> Crew Neck</li>
                      <li><strong>Style:</strong> Everyday Casual</li>
                  </ul>

                  <p>A simple everyday t-shirt suitable for casual outfits.</p>
                  """,
        brand = "Adiddydas",
        status = "In-Stock",
        imageUrls = listOf(
            "https://gqtuuqsgkyffgcpbfltk.supabase.co/storage/v1/object/public/product-images/tshirt-blacknwhite-adiddydas/1770641109200"
        ),
        colors = listOf(
            Color("Black", "#000000"),
            Color("White", "#FFFFFF"),
            Color("Grey", "#808080")
        ),
        sizes = listOf(
            "M",
            "L",
            "XL",
            "XXL"
        )
    )
    FavouriteList(product)
}