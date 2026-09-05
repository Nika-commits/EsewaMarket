package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.ProductRepository
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SearchViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val repository = ProductRepository()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions = _suggestions.asStateFlow()

    fun onChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    init {
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery
                .debounce(300.milliseconds)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _suggestions.value = emptyList()
                        return@collectLatest
                    }

                    try {
                        val result = repository.getSearchSuggestions(query)
                        _suggestions.value = result
                        Log.d("Search", _suggestions.value.toString())
                    } catch (e: Exception) {
                        _suggestions.value = emptyList()
                        Log.e("Search", "failed to observe: ${e.message}")
                    }
                }

        }
    }
}