package com.joviansapps.ganymede.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.theme.seedColorScheme
import com.joviansapps.ganymede.viewmodel.SettingsViewModel
import com.joviansapps.ganymede.viewmodel.ThemeMode
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SettingsScreen(vm: SettingsViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    var customHex by remember { mutableStateOf("") }
    var hexError by remember { mutableStateOf<String?>(null) }
    val useDark = when (state.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.AUTO -> isSystemInDarkTheme()
    }
    val previewScheme = seedColorScheme(Color(state.primaryColor), useDark)
    val listState = rememberLazyListState()
    var showCrashDialog by remember { mutableStateOf(false) }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Thème
        item {
            Text(stringResource(R.string.theme_label), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            SegmentedToggleGroup(
                modes = listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.AUTO),
                selected = state.themeMode,
                onSelect = { vm.setTheme(it) },
                modifier = Modifier.fillMaxWidth(),
                height = 40.dp
            )
        }
        // Couleur hex
        item {
            OutlinedTextField(
                value = customHex,
                onValueChange = {
                    customHex = it.uppercase(); hexError = null
                },
                label = { Text("Seed HEX (ex: FF6750A4 ou 6750A4)") },
                isError = hexError != null,
                singleLine = true,
                supportingText = { hexError?.let { e -> Text(e, color = MaterialTheme.colorScheme.error) } },
                trailingIcon = {
                    TextButton(onClick = {
                        val raw = customHex.removePrefix("#")
                        val full = if (raw.length == 6) "FF$raw" else raw
                        val valid = full.length == 8 && full.all { c -> c in '0'..'9' || c in 'A'..'F' }
                        if (!valid) hexError = "HEX invalide" else try {
                            vm.setPrimaryColor(full.toLong(16)); hexError = null
                        } catch (_: Exception) { hexError = "Erreur parsing" }
                    }) { Text("Appliquer") }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        // Pastilles couleur
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf(0xFF6750A4, 0xFF006E1C, 0xFFB3261E, 0xFF1F6FEB, 0xFFFB8C00).forEach { colorLong ->
                    val c = Color(colorLong)
                    Box(
                        Modifier
                            .size(36.dp)
                            .background(c, CircleShape)
                            .border(
                                width = if (state.primaryColor == colorLong) 3.dp else 1.dp,
                                color = if (state.primaryColor == colorLong) MaterialTheme.colorScheme.primary else Color.Gray,
                                shape = CircleShape
                            )
                            .clickable { vm.setPrimaryColor(colorLong) }
                    )
                }
            }
        }
        // Aperçu palette
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Aperçu (Material 3)", style = MaterialTheme.typography.bodyMedium)
                PaletteRowSample("Primary", previewScheme.primary, previewScheme.onPrimary, previewScheme.primaryContainer, previewScheme.onPrimaryContainer)
                PaletteRowSample("Secondary", previewScheme.secondary, previewScheme.onSecondary, previewScheme.secondaryContainer, previewScheme.onSecondaryContainer)
                PaletteRowSample("Tertiary", previewScheme.tertiary, previewScheme.onTertiary, previewScheme.tertiaryContainer, previewScheme.onTertiaryContainer)
                PaletteRowSample("Error", previewScheme.error, previewScheme.onError, previewScheme.errorContainer, previewScheme.onErrorContainer)
                PaletteRowSample("Surface", previewScheme.surface, previewScheme.onSurface, previewScheme.surfaceVariant, previewScheme.onSurfaceVariant)
            }
        }
        // Langue
        item {
            Text(stringResource(R.string.language_label), style = MaterialTheme.typography.titleMedium)
            var expanded by remember { mutableStateOf(false) }
            Box {
                OutlinedButton(onClick = { expanded = true }) { Text(state.language.uppercase()) }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    listOf("fr", "en", "es", "de").forEach { lang ->
                        DropdownMenuItem(
                            text = { Text(lang.uppercase()) },
                            onClick = { vm.setLanguage(lang); expanded = false }
                        )
                    }
                }
            }
        }
        // Diagnostic (rapports de plantage) — désormais toujours visible
        item {
            Text("Diagnostic", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleRow("Rapports de plantage", state.crashReportsEnabled) { vm.setCrashReportsEnabled(it) }
                Text(
                    "Active l'enregistrement simulé de rapports dans Logcat (tag CrashReporter).",
                    style = MaterialTheme.typography.bodySmall
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = { vm.testCrashReport() }) { Text("Tester envoi") }
                    TextButton(onClick = { showCrashDialog = true }) { Text("Forcer crash réel") }
                    if (state.crashReportsEnabled) Text("(Actif)", color = MaterialTheme.colorScheme.primary) else Text("(Inactif)")
                }
            }
        }
        item { Spacer(Modifier.height(48.dp)) }
    }
    if (showCrashDialog) {
        AlertDialog(
            onDismissRequest = { showCrashDialog = false },
            confirmButton = {
                TextButton(onClick = { showCrashDialog = false; vm.forceCrash() }) { Text("Crash") }
            },
            dismissButton = {
                TextButton(onClick = { showCrashDialog = false }) { Text("Annuler") }
            },
            title = { Text("Confirmer crash") },
            text = { Text("Cette action va volontairement faire planter l'application pour tester Crashlytics.") }
        )
    }
}

@Composable
private fun SegmentedToggleGroup(
    modes: List<ThemeMode>,
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
    modifier: Modifier = Modifier,
    height: Dp = 40.dp
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        modes.forEach { mode ->
            val isSelected = mode == selected
            val label = when (mode) {
                ThemeMode.LIGHT -> stringResource(R.string.light_label).uppercase()
                ThemeMode.DARK -> stringResource(R.string.dark_label).uppercase()
                ThemeMode.AUTO -> stringResource(R.string.auto_label).uppercase()
            }
            val emoji = when (mode) {
                ThemeMode.LIGHT -> "☀️"
                ThemeMode.DARK -> "🌙"
                ThemeMode.AUTO -> "🔁"
            }

            val buttonModifier = Modifier
                .height(height)
                .weight(1f)

            if (isSelected) {
                FilledTonalButton(
                    onClick = { onSelect(mode) },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(emoji)
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.labelLarge)
                    }
                }
            } else {
                OutlinedButton(
                    onClick = { onSelect(mode) },
                    modifier = buttonModifier,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(emoji)
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
         }
     }
 }

@Composable
private fun PaletteRowSample(label: String, main: Color, onMain: Color, container: Color, onContainer: Color) {
    Column(Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(main, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center) { Text("Main", color = onMain, style = MaterialTheme.typography.labelMedium) }
            Box(
                Modifier
                    .weight(1f)
                    .height(40.dp)
                    .background(container, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center) { Text("Container", color = onContainer, style = MaterialTheme.typography.labelMedium) }
        }
    }
}

@Composable
private fun ToggleRow(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = value, onCheckedChange = onChange)
    }
}

@Composable
@Preview(name = "SettingsPreview")
fun SettingsPreview() {
    SettingsScreen()
}

@Composable
@Preview(name = "SettingsPreview_Narrow", widthDp = 360)
fun SettingsPreviewNarrow() {
    SettingsPreview()
}
