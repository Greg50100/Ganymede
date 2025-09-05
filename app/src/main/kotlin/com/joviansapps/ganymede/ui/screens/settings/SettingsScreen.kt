package com.joviansapps.ganymede.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.viewmodel.SettingsViewModel
import com.joviansapps.ganymede.viewmodel.ThemeMode

@Composable
fun SettingsScreen(vm: SettingsViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(stringResource(R.string.theme_label), style = MaterialTheme.typography.titleMedium)
        ThemeRow(stringResource(R.string.light_label), ThemeMode.LIGHT, state.themeMode) { vm.setTheme(it) }
        ThemeRow(stringResource(R.string.dark_label),  ThemeMode.DARK,  state.themeMode) { vm.setTheme(it) }
        ThemeRow(stringResource(R.string.auto_label),  ThemeMode.AUTO,  state.themeMode) { vm.setTheme(it) }

        Text(stringResource(R.string.color_label), style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            val colors = listOf(0xFF6750A4, 0xFF006E1C, 0xFFB3261E, 0xFF1F6FEB)
            colors.forEach { colorLong ->
                val c = Color(colorLong)
                Box(
                    Modifier
                        .size(36.dp)
                        .background(c, CircleShape)
                        .border(
                            width = if (state.primaryColor == colorLong) 3.dp else 1.dp,
                            color = if (state.primaryColor == colorLong) Color.Black else Color.Gray,
                            shape = CircleShape
                        )
                        .clickable { vm.setPrimaryColor(colorLong) }
                )
            }
        }

        Text(stringResource(R.string.language_label), style = MaterialTheme.typography.titleMedium)
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(state.language.uppercase())
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("fr", "en", "es", "de").forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(lang.uppercase()) },
                        onClick = {
                            vm.setLanguage(lang)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeRow(
    label: String,
    mode: ThemeMode,
    current: ThemeMode,
    onSelect: (ThemeMode) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(mode) }
            .padding(vertical = 8.dp)
    ) {
        RadioButton(selected = current == mode, onClick = { onSelect(mode) })
        Spacer(Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}