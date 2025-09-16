// app/src/main/kotlin/com/joviansapps/ganymede/ui/screens/settings/SettingsScreen.kt
package com.joviansapps.ganymede.ui.screens.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.viewmodel.CalculatorViewModel
import com.joviansapps.ganymede.viewmodel.SettingsViewModel
import com.joviansapps.ganymede.viewmodel.ThemeMode
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(vm: SettingsViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    var showCrashDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            SettingsGroup(title = stringResource(R.string.theme_label), icon = Icons.Default.ColorLens) {
                SegmentedToggleGroup(
                    modes = listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.AUTO),
                    selected = state.themeMode,
                    onSelect = { vm.setTheme(it) }
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.settings_theme_colors_title),
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Spacer(Modifier.height(8.dp))
                PaletteGrid()
            }
        }

        item {
            SettingsGroup(title = stringResource(R.string.language_label), icon = Icons.Default.Language) {
                LanguageSelector(
                    currentLanguage = state.language,
                    onLanguageSelected = { vm.setLanguage(it) }
                )
            }
        }

        item {
            val calcVm: CalculatorViewModel = viewModel()
            val currentFormat by calcVm.formatMode.collectAsState()

            SettingsGroup(title = stringResource(id = R.string.settings_number_format_title), icon = Icons.Default.Straighten) {
                val modes = listOf(
                    CalculatorViewModel.FormatMode.PLAIN to R.string.settings_number_format_plain,
                    CalculatorViewModel.FormatMode.THOUSANDS to R.string.settings_number_format_thousands,
                    CalculatorViewModel.FormatMode.SCIENTIFIC to R.string.settings_number_format_scientific
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    SingleChoiceSegmentedButtonRow {
                        modes.forEach { (mode, labelRes) ->
                            SegmentedButton(
                                selected = mode == currentFormat,
                                onClick = { calcVm.setFormatMode(mode) },
                                shape = SegmentedButtonDefaults.itemShape(index = mode.ordinal, count = modes.size)
                            ) {
                                Text(stringResource(labelRes))
                            }
                        }
                    }
                }
            }
        }


        item {
            SettingsGroup(title = stringResource(R.string.settings_diagnostics_title), icon = Icons.Default.BugReport) {
                ToggleRow(
                    label = stringResource(R.string.settings_crash_reports_label),
                    isChecked = state.crashReportsEnabled,
                    onCheckedChange = { vm.setCrashReportsEnabled(it) }
                )
                Text(
                    stringResource(R.string.settings_crash_reports_description),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { vm.testCrashReport() }) {
                        Text(stringResource(R.string.settings_crash_reports_test_button))
                    }
                    OutlinedButton(onClick = { showCrashDialog = true }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                        Text(stringResource(R.string.settings_crash_reports_force_button))
                    }
                }
            }
        }

        item {
            SettingsGroup(title = stringResource(R.string.about_title), icon = Icons.Default.Info) {
                val appVersion = try {
                    context.packageManager.getPackageInfo(context.packageName, 0).versionName
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }

                InfoRow(label = stringResource(R.string.about_app_version), value = appVersion ?: "N/A")
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                ClickableInfoRow(
                    label = stringResource(R.string.about_github),
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Greg50100/Ganymede"))
                        context.startActivity(intent)
                    }
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                ClickableInfoRow(
                    label = stringResource(R.string.about_licenses),
                    onClick = { /* TODO: Navigate to a licenses screen */ }
                )
            }
        }
    }

    if (showCrashDialog) {
        AlertDialog(
            onDismissRequest = { showCrashDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    showCrashDialog = false
                    vm.forceCrash()
                }) { Text(stringResource(R.string.dialog_crash_confirm_button)) }
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
private fun SettingsGroup(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(12.dp))
                Text(title, style = MaterialTheme.typography.titleLarge)
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelector(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val languages = remember { listOf("fr", "en", "es", "de") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = Locale(currentLanguage).displayLanguage,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.language_label)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            ) {
                languages.forEach { lang ->
                    DropdownMenuItem(
                        text = { Text(Locale(lang).displayLanguage) },
                        onClick = {
                            onLanguageSelected(lang)
                            expanded = false
                        }
                    )
                }
        }
    }
}

@Composable
private fun SegmentedToggleGroup(
    modes: List<ThemeMode>,
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        SingleChoiceSegmentedButtonRow {
            modes.forEachIndexed { index, mode ->
                SegmentedButton(
                    selected = mode == selected,
                    onClick = { onSelect(mode) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = modes.size),
                    icon = {
                        val iconRes = when (mode) {
                            ThemeMode.LIGHT -> R.drawable.light_mode
                            ThemeMode.DARK -> R.drawable.dark_mode
                            ThemeMode.AUTO -> R.drawable.autorenew
                        }
                        Icon(painter = painterResource(id = iconRes), contentDescription = null, modifier = Modifier.size(20.dp))
                    }
                ) {
                    val label = when (mode) {
                        ThemeMode.LIGHT -> stringResource(R.string.light_label)
                        ThemeMode.DARK -> stringResource(R.string.dark_label)
                        ThemeMode.AUTO -> stringResource(R.string.auto_label)
                    }
                    Text(label)
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PaletteGrid() {
    val scheme = MaterialTheme.colorScheme
    val colors = listOf(
        "Primary" to scheme.primary,
        "Secondary" to scheme.secondary,
        "Tertiary" to scheme.tertiary,
        "Surface" to scheme.surface,
        "Background" to scheme.background,
        "Error" to scheme.error
    )
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        colors.forEach { (name, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(color, RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.width(6.dp))
                Text(name, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun ClickableInfoRow(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
