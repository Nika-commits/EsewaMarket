package com.example.xml_app.ui.state

import com.example.xml_app.entities.SearchHistory
import com.example.xml_app.models.Product

sealed interface SearchUiState {
    data object NoResults : SearchUiState
    data object Success : SearchUiState
    data object Error : SearchUiState
    data object InitialLoading : SearchUiState
    data object LoadingMoreProducts : SearchUiState
}

sealed interface SearchHistoryUiState {
    data object Unauth : SearchHistoryUiState
    data object Loading : SearchHistoryUiState
    data class Success(
        val histories: List<SearchHistory>
    ) : SearchHistoryUiState

    data object Error : SearchHistoryUiState
}

sealed interface SearchMostPopularProductsUiState {
    data object Loading : SearchMostPopularProductsUiState
    data object Error : SearchMostPopularProductsUiState
    data class Success(
        val products: List<Product>
    ) : SearchMostPopularProductsUiState
}