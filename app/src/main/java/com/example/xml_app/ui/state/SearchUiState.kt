package com.example.xml_app.ui.state

sealed interface SearchUiState {
    data object NoResults : SearchUiState
    data object Success : SearchUiState
    data object Error : SearchUiState
    data object InitialLoading : SearchUiState
    data object LoadingMoreProducts : SearchUiState
}