package com.joviansapps.ganymede.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joviansapps.ganymede.core.domain.model.CalculationResult
import com.joviansapps.ganymede.core.domain.model.CalculationType
import com.joviansapps.ganymede.core.domain.repository.CalculationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * État UI pour l'historique des calculs
 */
data class CalculationHistoryUiState(
    val calculations: List<CalculationResult> = emptyList(),
    val favorites: List<CalculationResult> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<CalculationResult> = emptyList(),
    val selectedType: CalculationType? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel pour gérer l'historique des calculs
 */
@HiltViewModel
class CalculationHistoryViewModel @Inject constructor(
    private val calculationRepository: CalculationRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedType = MutableStateFlow<CalculationType?>(null)
    private val _error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<CalculationHistoryUiState> = combine(
        calculationRepository.getAllCalculations(),
        calculationRepository.getFavoriteCalculations(),
        _searchQuery,
        _selectedType,
        _error
    ) { calculations, favorites, searchQuery, selectedType, error ->
        val filteredCalculations = when {
            selectedType != null -> calculations.filter { it.type == selectedType }
            searchQuery.isNotEmpty() -> calculations.filter { calculation ->
                calculation.input.contains(searchQuery, ignoreCase = true) ||
                calculation.result.contains(searchQuery, ignoreCase = true)
            }
            else -> calculations
        }

        CalculationHistoryUiState(
            calculations = filteredCalculations,
            favorites = favorites,
            searchQuery = searchQuery,
            selectedType = selectedType,
            error = error,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = CalculationHistoryUiState(isLoading = true)
    )

    /**
     * Sauvegarde un nouveau calcul dans l'historique
     */
    fun saveCalculation(
        type: CalculationType,
        input: String,
        result: String
    ) {
        viewModelScope.launch {
            try {
                val calculation = CalculationResult(
                    id = "",
                    type = type,
                    input = input,
                    result = result
                )
                calculationRepository.saveCalculation(calculation)
            } catch (e: Exception) {
                _error.value = "Erreur lors de la sauvegarde: ${e.message}"
            }
        }
    }

    /**
     * Bascule le statut favori d'un calcul
     */
    fun toggleFavorite(calculationId: String) {
        viewModelScope.launch {
            try {
                calculationRepository.toggleFavorite(calculationId)
            } catch (e: Exception) {
                _error.value = "Erreur lors de la modification: ${e.message}"
            }
        }
    }

    /**
     * Supprime un calcul de l'historique
     */
    fun deleteCalculation(calculationId: String) {
        viewModelScope.launch {
            try {
                calculationRepository.deleteCalculation(calculationId)
            } catch (e: Exception) {
                _error.value = "Erreur lors de la suppression: ${e.message}"
            }
        }
    }

    /**
     * Effectue une recherche dans l'historique
     */
    fun searchCalculations(query: String) {
        _searchQuery.value = query
    }

    /**
     * Filtre par type de calcul
     */
    fun filterByType(type: CalculationType?) {
        _selectedType.value = type
    }

    /**
     * Efface tout l'historique
     */
    fun clearHistory() {
        viewModelScope.launch {
            try {
                calculationRepository.clearHistory()
            } catch (e: Exception) {
                _error.value = "Erreur lors de l'effacement: ${e.message}"
            }
        }
    }

    /**
     * Nettoie l'historique ancien
     */
    fun cleanOldHistory(daysToKeep: Int = 30) {
        viewModelScope.launch {
            try {
                calculationRepository.cleanOldHistory(daysToKeep)
            } catch (e: Exception) {
                _error.value = "Erreur lors du nettoyage: ${e.message}"
            }
        }
    }

    /**
     * Efface les erreurs
     */
    fun clearError() {
        _error.value = null
    }
}
