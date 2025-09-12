package com.joviansapps.ganymede.ui.screens.converter

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.data.conversion.ConversionRepository
import com.joviansapps.ganymede.data.conversion.UnitCategory
import com.joviansapps.ganymede.data.conversion.ConverterUnit
import com.joviansapps.ganymede.viewmodel.ConverterViewModel
import com.joviansapps.ganymede.viewmodel.FormatMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.input.TextFieldValue
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
@Preview
fun ConverterScreen(vm: ConverterViewModel = viewModel()) {
    val state by vm.uiState.collectAsState()
    val vmValues by vm.values.collectAsState()
    val context = LocalContext.current
    val copyLabel = stringResource(R.string.copy_text)

    // Liste des unités de la catégorie sélectionnée
    val units = remember(state.category) { ConversionRepository.units(state.category) }

    // Valeurs gérées par le ViewModel
    // formatMode également exposé dans state (state.formatMode)

    var catExpanded by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sélecteur de catégorie
        ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = !catExpanded }) {
            OutlinedTextField(
                value = categoryTitle(state.category),
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                label = { Text(stringResource(R.string.converter_category_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = catExpanded,
                onDismissRequest = { catExpanded = false },
            ) {
                val scrollState = rememberScrollState()
                val maxHeight = (LocalConfiguration.current.screenHeightDp.dp * 0.5f)
                Column(
                    modifier = Modifier
                        .heightIn(max = maxHeight)
                        .verticalScroll(scrollState)
                        .padding(vertical = 4.dp)
                ) {
                    UnitCategory.values().forEach { c ->
                        DropdownMenuItem(
                            text = { Text(categoryTitle(c)) },
                            onClick = {
                                catExpanded = false
                                vm.onCategoryChange(c)
                                // Réinitialise les valeurs à vide pour la nouvelle catégorie via ViewModel
                            }
                        )
                    }
                }
            }
        }

        // Petit contrôle pour choisir le mode de formatage
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(R.string.format_label), style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = { vm.onFormatModeChange(FormatMode.AUTO) }) { Text("Auto", color = if (state.formatMode == FormatMode.AUTO) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) }
            TextButton(onClick = { vm.onFormatModeChange(FormatMode.SCIENTIFIC) }) { Text("Sci", color = if (state.formatMode == FormatMode.SCIENTIFIC) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) }
            TextButton(onClick = { vm.onFormatModeChange(FormatMode.LOCALE) }) { Text("Locale", color = if (state.formatMode == FormatMode.LOCALE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface) }
        }

        // Liste de toutes les unités éditables
        val listScroll = rememberScrollState()
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(listScroll),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            units.forEach { unit ->
                val current = vmValues[unit.id]?.text ?: ""
                OutlinedTextField(
                    value = current,
                    onValueChange = { newText ->
                        // déléguer la logique de conversion au ViewModel (préserve le curseur via TextFieldValue)
                        vm.onUnitTextChanged(unit.id, TextFieldValue(newText))
                    },
                    singleLine = true,
                    label = {
                        val resId = unit.labelRes
                        if (resId != null) Text(stringResource(resId)) else Text(unit.label ?: unit.id)
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(unit.label ?: unit.id, style = MaterialTheme.typography.bodyMedium)
                            IconButton(onClick = {
                                val clip = ClipData.newPlainText(copyLabel, "${vmValues[unit.id]?.text ?: ""} ${unit.label ?: unit.id}")
                                (context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager).setPrimaryClip(clip)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = stringResource(R.string.copy_text)
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            // TODO: persister le choix de format dans les préférences si nécessaire
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun categoryTitle(category: UnitCategory): String = when (category) {
    UnitCategory.TEMPERATURE -> stringResource(R.string.unit_temperature)
    UnitCategory.AREA -> stringResource(R.string.unit_area)
    UnitCategory.STORAGE -> stringResource(R.string.unit_storage)
    UnitCategory.FREQUENCY -> stringResource(R.string.unit_frequency)
    UnitCategory.LENGTH -> stringResource(R.string.unit_length)
    UnitCategory.MASS -> stringResource(R.string.unit_mass)
    UnitCategory.SPEED -> stringResource(R.string.unit_speed)
    UnitCategory.VOLUME -> stringResource(R.string.unit_volume)
    UnitCategory.ANGLE -> stringResource(R.string.unit_angle)
    UnitCategory.POWER -> stringResource(R.string.unit_power)
    UnitCategory.PRESSURE -> stringResource(R.string.unit_pressure)
    UnitCategory.DENSITY -> stringResource(R.string.unit_density)
    UnitCategory.ENERGY -> stringResource(R.string.unit_energy)
    UnitCategory.FORCE -> stringResource(R.string.unit_force)
    UnitCategory.FUEL -> stringResource(R.string.unit_fuel)
    UnitCategory.LIGHT -> stringResource(R.string.unit_light)
    UnitCategory.TIME -> stringResource(R.string.unit_time)
    UnitCategory.TORQUE -> stringResource(R.string.unit_torque)
    UnitCategory.VISCOSITY -> stringResource(R.string.unit_viscosity)
    UnitCategory.CURRENCY -> stringResource(R.string.unit_currency)
    UnitCategory.NUMERIC_BASE -> stringResource(R.string.unit_numeric_base)
}
