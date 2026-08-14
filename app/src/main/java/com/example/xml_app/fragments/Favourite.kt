package com.example.xml_app.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.visible
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.example.xml_app.models.Product
import com.example.xml_app.ui.composeUi.EmptyFavourites
import com.example.xml_app.utils.CustomSnackbar
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.custom.ActionIcon
import com.example.xml_app.utils.custom.SwipableItemsWithActions
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

        viewModel.initializeUser()

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
        val bottomNavigation = requireActivity().findViewById<LinearLayout>(R.id.bottomNavigation)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MaterialTheme {
                        FavouriteScreen(viewModel, requireContext(), binding.root, bottomNavigation)
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
fun FavouriteList(
    product: Product,
    isChecked: Boolean = false,
    onClick: (Int) -> Unit,
    onAddToCart: (Int) -> Unit,
    onRemoveFromCart: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onClick(product.id)
                }
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
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
                    .padding(vertical = 8.dp, horizontal = 8.dp),
            ) {
                Text(
                    text = product.name,
                    fontSize = 16.sp,
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Medium,
                    color = colorResource(R.color.textDark400)
                )
                Text(
                    text = product.brand,
                    fontSize = 10.sp,
                    fontFamily = SourceSansPro,
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
                        fontFamily = SourceSansPro,
                        fontWeight = FontWeight.Normal,
                        color = colorResource(R.color.textDark400)

                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = product.price.toFloat().toString(),
                        fontSize = 20.sp,
                        fontFamily = SourceSansPro,
                        fontWeight = FontWeight.Medium,
                        color = colorResource(R.color.textDark400)
                    )
                }

            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = {}
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_overflow_option),
                        contentDescription = null
                    )
                }

                FilledIconButton(
                    onClick = {
                        onAddToCart(product.id)
                    },
                    modifier = Modifier
                        .visible(isChecked)
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = colorResource(R.color.primaryGreen),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_add_cart),
                        contentDescription = null
                    )
                }
            }
        }

        Image(
            painter = painterResource(R.drawable.ic_tick_green),
            contentDescription = null,
            modifier = Modifier
                .visible(isChecked)
                .align(Alignment.TopStart)
//                .offset(x = (-12).dp, y = (-12).dp)
                .size(24.dp)
                .clickable {}
        )

    }
}

@Composable
fun FavouriteScreen(
    viewModel: FavouriteViewModel,
    context: Context,
    rootView: View,
    anchor: View
) {
    val products by viewModel.favouriteProducts.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.offWhiteBackground))
            .padding(16.dp)
    ) {

        Spacer(modifier = Modifier.size(8.dp))

        when {
            products.isEmpty() -> {
                Text(
                    text = "Items (0)",
                    color = colorResource(R.color.textDark300),
                    fontFamily = SourceSansPro,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    letterSpacing = 0.25.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                EmptyFavourites()
            }

            else -> {
                val selectedSet = remember { mutableStateSetOf<Int>() }
                val parentState = when {
                    selectedSet.isEmpty() -> ToggleableState.Off
                    selectedSet.size == products.size -> ToggleableState.On
                    else -> ToggleableState.Indeterminate
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    TriStateCheckbox(
                        state = parentState,
                        onClick = {
                            if (parentState != ToggleableState.On) {
                                selectedSet.clear()
                                selectedSet.addAll(products.map { it.product.id })
                            } else {
                                selectedSet.clear()
                            }
                        },
                        modifier = Modifier
                            .size(18.dp),
                        colors = CheckboxDefaults.colors(
                            checkedColor = colorResource(R.color.primaryGreen),
                            checkmarkColor = colorResource(R.color.surface),
                            uncheckedColor = colorResource(R.color.textDark),
                        )

                    )
                    Text(
                        text = "Items ${products.size}",
                        color = colorResource(R.color.textDark300),
                        fontFamily = SourceSansPro,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        letterSpacing = 0.25.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

//                    Text(
//                        text = "ADD TO CART",
//                        color = colorResource(R.color.primaryGreen),
//                        fontFamily = SourceSansPro,
//                        fontWeight = FontWeight.Medium,
//                        fontSize = 14.sp,
//                        letterSpacing = 1.sp,
//                        textAlign = TextAlign.End
//
//                    )

                    Text(
                        text = "DELETE ALL",
                        color = colorResource(R.color.textDark300),
                        fontFamily = SourceSansPro,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.clickable {
                            val count = selectedSet.size
                            val favouriteIdsCopy = selectedSet.toSet()
                            selectedSet.forEach {
                                viewModel.toggleFavourite(it)
                            }
                            CustomSnackbar.show(
                                view = rootView,
                                context = context,
                                text = "($count) items has been deleted",
                                actionText = "UNDO",
                                anchorView = anchor,
                                action = {
                                    favouriteIdsCopy.forEach {
                                        viewModel.toggleFavourite(it)
                                    }
                                }
                            )
                        }
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    itemsIndexed(
                        items = products,
                        key = { _, product -> product.product.id }
                    ) { _, product ->
                        SwipableItemsWithActions(
                            isRevealed = product.isOptionsRevealed,
                            onExpanded = {
                                viewModel.setOptionsRevealed(product.product.id, true)
                            },
                            onCollapsed = {
                                viewModel.setOptionsRevealed(product.product.id, false)
                            },
                            actions = {
                                ActionIcon(
                                    onClick = {
                                        viewModel.toggleFavourite(product.product.id)
                                        viewModel.setOptionsRevealed(product.product.id, false)
                                        CustomSnackbar.show(
                                            view = rootView,
                                            context = context,
                                            text = "(1) item has been deleted",
                                            actionText = "UNDO",
                                            anchorView = anchor,
                                            action = {
                                                viewModel.toggleFavourite(product.product.id)
                                            }
                                        )
                                    },
                                    icon = painterResource(R.drawable.ic_trash),
                                    modifier = Modifier.fillMaxHeight()
                                )
                            }
                        ) {
                            FavouriteList(
                                product = product.product,
                                isChecked = selectedSet.contains(product.product.id),
                                onClick = { id ->
                                    if (selectedSet.contains(id)) {
                                        selectedSet.remove(id)
                                    } else {
                                        selectedSet.add(id)
                                    }
                                },
                                onAddToCart = { id ->
                                    viewModel.addToCart(id)
                                    CustomSnackbar.show(
                                        view = rootView,
                                        context = context,
                                        text = "Added To Cart Successfully",
                                        anchorView = anchor,
                                        action = {

                                        }
                                    )
                                },
                                onRemoveFromCart = { id ->
                                    viewModel.removeFromCart(id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
