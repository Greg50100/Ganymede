package com.joviansapps.ganymede.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.viewmodel.SettingsViewModel
import com.joviansapps.ganymede.viewmodel.ThemeMode
import com.joviansapps.ganymede.viewmodel.CalculatorViewModel
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext


@Composable
@Preview
fun SettingsScreen(vm: SettingsViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val isDark = when (state.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.AUTO -> isSystemInDarkTheme()
    }
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
        // Aperçu palette supprimé -> remplacé par un simple aperçu des couleurs du thème actif
        item {
            Text(stringResource(R.string.settings_theme_colors_title), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            val scheme = MaterialTheme.colorScheme
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                PaletteRowSample("Primary", scheme.primary, scheme.onPrimary, scheme.primaryContainer, scheme.onPrimaryContainer)
                PaletteRowSample("Secondary", scheme.secondary, scheme.onSecondary, scheme.secondaryContainer, scheme.onSecondaryContainer)
                PaletteRowSample("Tertiary", scheme.tertiary, scheme.onTertiary, scheme.tertiaryContainer, scheme.onTertiaryContainer)
                PaletteRowSample("Error", scheme.error, scheme.onError, scheme.errorContainer, scheme.onErrorContainer)
                PaletteRowSample("Surface", scheme.surface, scheme.onSurface, scheme.surfaceVariant, scheme.onSurfaceVariant)
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
        // Diagnostic
        item {
            Text(stringResource(R.string.settings_diagnostics_title), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ToggleRow(stringResource(R.string.settings_crash_reports_label), state.crashReportsEnabled) { vm.setCrashReportsEnabled(it) }
                Text(
                    stringResource(R.string.settings_crash_reports_description),
                    style = MaterialTheme.typography.bodySmall
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextButton(onClick = { vm.testCrashReport() }) { Text(stringResource(R.string.settings_crash_reports_test_button)) }
                    TextButton(onClick = { showCrashDialog = true }) { Text(stringResource(R.string.settings_crash_reports_force_button)) }
                    if (state.crashReportsEnabled) Text(stringResource(R.string.settings_crash_reports_active_label), color = MaterialTheme.colorScheme.primary) else Text(stringResource(R.string.settings_crash_reports_inactive_label))
                }
            }
        }
        // Formatage des nombres (PLAIN / THOUSANDS / SCIENTIFIC) pour la calculatrice
        item {
            val calcVm: CalculatorViewModel = viewModel()
            val currentFormat by calcVm.formatMode.collectAsState()
            Text(stringResource(R.string.settings_number_format_title), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val modes = listOf(
                    CalculatorViewModel.FormatMode.PLAIN to R.string.settings_number_format_plain,
                    CalculatorViewModel.FormatMode.THOUSANDS to R.string.settings_number_format_thousands,
                    CalculatorViewModel.FormatMode.SCIENTIFIC to R.string.settings_number_format_scientific
                )
                modes.forEach { (mode, labelRes) ->
                    val selected = mode == currentFormat
                    if (selected) {
                        FilledTonalButton(onClick = { /* already selected */ }, modifier = Modifier.weight(1f)) { Text(stringResource(labelRes)) }
                    } else {
                        OutlinedButton(onClick = { calcVm.setFormatMode(mode) }, modifier = Modifier.weight(1f)) { Text(stringResource(labelRes)) }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(48.dp)) }
    }
    if (showCrashDialog) {
        AlertDialog(
            onDismissRequest = { showCrashDialog = false },
            confirmButton = {
                TextButton(onClick = { showCrashDialog = false; vm.forceCrash() }) { Text(stringResource(R.string.dialog_crash_confirm_button)) }
            },
            dismissButton = {
                TextButton(onClick = { showCrashDialog = false }) { Text(stringResource(R.string.dialog_crash_cancel_button)) }
            },
            title = { Text(stringResource(R.string.dialog_crash_title)) },
            text = { Text(stringResource(R.string.dialog_crash_message)) }
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
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        modes.forEach { mode ->
            val isSelected = mode == selected
            val label = when (mode) {
                ThemeMode.LIGHT -> stringResource(R.string.light_label).uppercase()
                ThemeMode.DARK -> stringResource(R.string.dark_label).uppercase()
                ThemeMode.AUTO -> stringResource(R.string.auto_label).uppercase()
            }

            val iconRes = when (mode) {
                ThemeMode.LIGHT -> R.drawable.light_mode
                ThemeMode.DARK -> R.drawable.dark_mode
                ThemeMode.AUTO -> R.drawable.autorenew
            }
            val iconPainter = painterResource(id = iconRes)

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
                        Icon(painter = iconPainter, contentDescription = label, modifier = Modifier.size(20.dp))
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
                        Icon(painter = iconPainter, contentDescription = label, modifier = Modifier.size(20.dp))
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
