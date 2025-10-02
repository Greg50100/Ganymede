package com.joviansapps.ganymede.ui.screens.settings

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.viewmodel.NumberFormatMode
import com.joviansapps.ganymede.viewmodel.SettingsDefaults
import com.joviansapps.ganymede.viewmodel.SettingsUiState
import com.joviansapps.ganymede.viewmodel.SettingsViewModel
import com.joviansapps.ganymede.viewmodel.ThemeMode
import java.util.Locale

@Composable
@Preview
fun SettingsScreen(vm: SettingsViewModel? = null) {
    val inPreview = LocalInspectionMode.current
    val viewModel: SettingsViewModel? = if (inPreview) null else vm ?: viewModel()

    val state by viewModel?.uiState?.collectAsState()
        ?: remember { mutableStateOf(SettingsUiState()) }

    var showCrashDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            AppearanceSettings(
                state = state,
                onThemeSelected = { viewModel?.setTheme(it) },
                onLanguageSelected = { viewModel?.setLanguage(it) },
                onFormatSelected = { viewModel?.setNumberFormatMode(it) }
            )
        }
        item {
            BehaviorSettings(
                state = state,
                onKeepScreenOnChanged = { viewModel?.setKeepScreenOnEnabled(it) },
                onHapticFeedbackChanged = { viewModel?.setHapticFeedbackEnabled(it) }
            )
        }
        item {
            DiagnosticsSettings(
                onForceCrashClick = { showCrashDialog = true }
            )
        }
        item {
            AboutSettings()
        }
    }

    if (showCrashDialog) {
        ConfirmCrashDialog(
            onDismiss = { showCrashDialog = false },
            onConfirm = {
                showCrashDialog = false
                viewModel?.forceCrash()
            }
        )
    }
}

@Composable
private fun AppearanceSettings(
    state: SettingsUiState,
    onThemeSelected: (ThemeMode) -> Unit,
    onLanguageSelected: (String) -> Unit,
    onFormatSelected: (NumberFormatMode) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.theme_label), icon = Icons.Default.ColorLens) {
        val themeItems = remember {
            listOf(
                SegmentedButtonItem(ThemeMode.LIGHT, R.string.light_label, { Icon(painterResource(R.drawable.light_mode), null, Modifier.size(20.dp)) }),
                SegmentedButtonItem(ThemeMode.DARK, R.string.dark_label, { Icon(painterResource(R.drawable.dark_mode), null, Modifier.size(20.dp)) }),
                SegmentedButtonItem(ThemeMode.AUTO, R.string.auto_label, { Icon(painterResource(R.drawable.autorenew), null, Modifier.size(20.dp)) })
            )
        }
        SettingsSegmentedButtonRow(items = themeItems, selectedItemValue = state.themeMode, onItemSelected = onThemeSelected)

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        LanguageSelector(currentLanguage = state.language, onLanguageSelected = onLanguageSelected)

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text(stringResource(id = R.string.settings_number_format_title), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        val formatItems = remember {
            listOf(
                SegmentedButtonItem(NumberFormatMode.PLAIN, R.string.settings_number_format_plain),
                SegmentedButtonItem(NumberFormatMode.THOUSANDS, R.string.settings_number_format_thousands),
                SegmentedButtonItem(NumberFormatMode.SCIENTIFIC, R.string.settings_number_format_scientific)
            )
        }
        SettingsSegmentedButtonRow(items = formatItems, selectedItemValue = state.numberFormatMode, onItemSelected = onFormatSelected)
    }
}

@Composable
private fun BehaviorSettings(
    state: SettingsUiState,
    onKeepScreenOnChanged: (Boolean) -> Unit,
    onHapticFeedbackChanged: (Boolean) -> Unit
) {
    SettingsGroup(title = stringResource(R.string.behavior_title), icon = Icons.Default.Build) {
        ToggleRow(
            label = stringResource(R.string.keep_screen_on_label),
            description = stringResource(R.string.keep_screen_on_description),
            isChecked = state.keepScreenOnEnabled,
            onCheckedChange = onKeepScreenOnChanged
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        ToggleRow(
            label = stringResource(R.string.haptic_feedback_label),
            description = stringResource(R.string.haptic_feedback_description),
            isChecked = state.hapticFeedbackEnabled,
            onCheckedChange = onHapticFeedbackChanged
        )
    }
}

@Composable
private fun DiagnosticsSettings(onForceCrashClick: () -> Unit) {
    SettingsGroup(title = stringResource(R.string.debug_title), icon = Icons.Default.BugReport) {
        ClickableInfoRow(
            label = stringResource(R.string.debug_force_crash),
            onClick = onForceCrashClick
        )
    }
}

@Composable
private fun AboutSettings() {
    val context = LocalContext.current
    val appVersion = remember {
        try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName ?: "N/A" // Fix: Provide a default value if versionName is null
        } catch (e: PackageManager.NameNotFoundException) { "N/A" }
    }

    SettingsGroup(title = stringResource(R.string.about_title), icon = Icons.Default.Info) {
        InfoRow(label = stringResource(R.string.about_app_version), value = appVersion)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        ClickableInfoRow(
            label = stringResource(R.string.about_github),
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Greg50100/Ganymede"))
                context.startActivity(intent)
            }
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        ClickableInfoRow(
            label = stringResource(R.string.about_licenses),
            onClick = { /* TODO: Navigate to licenses screen */ }
        )
    }
}


// --- Reusable Components ---

private data class SegmentedButtonItem<T>(
    val value: T,
    @StringRes val labelRes: Int,
    val icon: (@Composable () -> Unit)? = null
)

@Composable
private fun SettingsGroup(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedCard(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(modifier = Modifier.padding(16.dp).padding(top = 12.dp)) {
                content()
            }
        }
        Row(
            modifier = Modifier
                .padding(start = 16.dp)
                .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SettingsSegmentedButtonRow(
    items: List<SegmentedButtonItem<T>>,
    selectedItemValue: T,
    onItemSelected: (T) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        SingleChoiceSegmentedButtonRow {
            items.forEachIndexed { index, item ->
                SegmentedButton(
                    selected = item.value == selectedItemValue,
                    onClick = { onItemSelected(item.value) },
                    shape = SegmentedButtonDefaults.itemShape(index, items.size),
                    icon = { item.icon?.invoke() }
                ) {
                    Text(stringResource(item.labelRes))
                }
            }
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
    val languages = remember { SettingsDefaults.SupportedLanguages }

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
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            languages.forEach { (langCode, langName) ->
                DropdownMenuItem(
                    text = { Text(langName) },
                    onClick = {
                        onLanguageSelected(langCode)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
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
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ConfirmCrashDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(stringResource(R.string.dialog_crash_confirm_button)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.dialog_crash_cancel_button)) }
        },
        title = { Text(stringResource(R.string.dialog_crash_title)) },
        text = { Text(stringResource(R.string.dialog_crash_message)) }
    )
}
