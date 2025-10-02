package com.joviansapps.ganymede.ui.screens.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.joviansapps.ganymede.core.domain.model.CalculationType
import com.joviansapps.ganymede.core.ui.components.CalculationCard
import com.joviansapps.ganymede.core.ui.components.ErrorMessage
import com.joviansapps.ganymede.core.ui.components.LoadingIndicator
import com.joviansapps.ganymede.viewmodel.CalculationHistoryViewModel

/**
 * Écran d'historique des calculs refactorisé
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: CalculationHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Barre de recherche et filtres
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchCalculations(it)
                },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Rechercher dans l'historique...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Rechercher")
                },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = {
                            searchQuery = ""
                            viewModel.searchCalculations("")
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Effacer")
                        }
                    }
                } else null
            )

            IconButton(
                onClick = { showFilters = !showFilters }
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filtres")
            }
        }

        // Filtres par type
        AnimatedVisibility(visible = showFilters) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        onClick = { viewModel.filterByType(null) },
                        label = { Text("Tous") },
                        selected = uiState.selectedType == null
                    )
                }

                items(CalculationType.values()) { type ->
                    FilterChip(
                        onClick = { viewModel.filterByType(type) },
                        label = { Text(type.name) },
                        selected = uiState.selectedType == type
                    )
                }
            }
        }

        // Actions rapides
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.cleanOldHistory() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Nettoyer l'ancien")
            }

            Button(
                onClick = { viewModel.clearHistory() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Tout effacer")
            }
        }

        // Contenu principal
        when {
            uiState.isLoading -> {
                LoadingIndicator(
                    message = "Chargement de l'historique..."
                )
            }

            uiState.error != null -> {
                // Extraction de la valeur de l'erreur pour éviter le problème de smart cast
                val errorMessage = uiState.error ?: "Erreur inconnue"
                ErrorMessage(
                    error = errorMessage,
                    onRetry = { viewModel.clearError() }
                )
            }

            uiState.calculations.isEmpty() -> {
                EmptyHistoryMessage()
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = uiState.calculations,
                        key = { it.id }
                    ) { calculation ->
                        CalculationCard(
                            input = calculation.input,
                            result = calculation.result,
                            timestamp = calculation.timestamp,
                            isFavorite = calculation.isFavorite,
                            onFavoriteClick = { viewModel.toggleFavorite(calculation.id) },
                            onDeleteClick = { viewModel.deleteCalculation(calculation.id) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Message affiché quand l'historique est vide
 */
@Composable
private fun EmptyHistoryMessage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Aucun calcul dans l'historique",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Commencez à utiliser la calculatrice pour voir vos calculs ici",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
