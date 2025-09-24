package com.joviansapps.ganymede.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joviansapps.ganymede.data.Searchable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<Searchable> = emptyList()
)

class SearchViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var allSearchableItems: List<Searchable> = emptyList()

    fun setSearchableItems(items: List<Searchable>) {
        allSearchableItems = items
    }

    fun onQueryChange(query: String) {
        _uiState.update { it.copy(query = query) }
        viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.update { it.copy(results = emptyList()) }
            } else {
                val filteredResults = allSearchableItems.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.keywords.any { keyword -> keyword.contains(query, ignoreCase = true) }
                }
                _uiState.update { it.copy(results = filteredResults) }
            }
        }
    }
}