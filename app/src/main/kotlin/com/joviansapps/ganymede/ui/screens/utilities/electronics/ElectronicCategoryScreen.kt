package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material.icons.filled.Waves
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.components.UtilitiesCategoryGridScreen
import com.joviansapps.ganymede.ui.components.CategoryItem

@Composable
fun ElectronicCategoryScreen(
    modifier: Modifier = Modifier,
    onOpenResistorCalculator: () -> Unit,
    onOpenInductanceCalculator: () -> Unit,
    onOpenTimeConstantCalculator: () -> Unit,
    onOpenParallelSeriesResistorCalculator: () -> Unit,
    onOpenParallelSeriesCapacitorCalculator: () -> Unit,
    onOpenOhmsLawCalculator: () -> Unit,
    onOpenVoltageDividerCalculator: () -> Unit,
    onOpenLedResistorCalculator: () -> Unit,
    onOpenTimer555Calculator: () -> Unit,
    onOpenFilterCalculator: () -> Unit,
    onOpenWireGaugeCalculator: () -> Unit,
    onOpenVoltageDropCalculator: () -> Unit,
    onOpenEnergyCostCalculator: () -> Unit,
    onOpenBatteryLifeCalculator: () -> Unit,
    onOpenWavelengthFrequencyCalculator: () -> Unit,
    onOpenZenerDiodeCalculator: () -> Unit,
    onOpenReactanceCalculator: () -> Unit,
    onOpenPowerCalculator: () -> Unit,
    onOpenWheatstoneBridgeCalculator: () -> Unit,
    onOpenOpAmpCalculator: () -> Unit,
    onOpenCapacitorCodeCalculator: () -> Unit,
    onOpenSmdResistorCalculator: () -> Unit,
    onOpenPowerAcCalculator: () -> Unit,
    onOpenDeltaStarConverter: () -> Unit,
    onOpenComponentToleranceCalculator: () -> Unit,
    onOpenStandardValueCalculator: () -> Unit,
    onOpenRlcImpedanceCalculator: () -> Unit,
    onOpenRlcResonantCircuitCalculator: () -> Unit,
    // AJOUTÉ
    onOpenPassiveFilterCalculator: () -> Unit,
    onOpenRmsCalculator: () -> Unit,
    onOpenBjtBiasingCalculator: () -> Unit,
    onOpenTransformerCalculator: () -> Unit,
) {
    val electronicsItems = listOf(
        CategoryItem(
            title = stringResource(id = R.string.transformer_calculator_title),
            description = stringResource(id = R.string.transformer_calculator_description),
            icon = Icons.Default.Transform,
            onClick = onOpenTransformerCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.bjt_biasing_calculator_title),
            description = stringResource(id = R.string.bjt_biasing_calculator_description),
            icon = Icons.Default.SettingsInputComponent,
            onClick = onOpenBjtBiasingCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.rms_calculator_title),
            description = stringResource(id = R.string.rms_calculator_description),
            icon = Icons.Default.Waves,
            onClick = onOpenRmsCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.passive_filter_calculator_title),
            description = stringResource(id = R.string.passive_filter_calculator_description),
            icon = Icons.Default.GraphicEq,
            onClick = onOpenPassiveFilterCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.rlc_impedance_calculator_title),
            description = stringResource(id = R.string.rlc_impedance_calculator_description),
            icon = Icons.Default.GraphicEq,
            onClick = onOpenRlcImpedanceCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.rlc_resonant_circuit_calculator_title),
            description = stringResource(id = R.string.rlc_resonant_circuit_calculator_description),
            icon = Icons.Default.Waves,
            onClick = onOpenRlcResonantCircuitCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.standard_value_calculator_title),
            description = stringResource(id = R.string.standard_value_calculator_description),
            icon = Icons.Default.Checklist,
            onClick = onOpenStandardValueCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.component_tolerance_calculator_title),
            description = stringResource(id = R.string.component_tolerance_calculator_description),
            icon = Icons.Default.Thermostat,
            onClick = onOpenComponentToleranceCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.ohms_law_calculator_title),
            description = stringResource(id = R.string.ohms_law_calculator_description),
            icon = Icons.Default.Bolt,
            onClick = onOpenOhmsLawCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.ac_power_calculator_title),
            description = stringResource(id = R.string.ac_power_calculator_description),
            icon = Icons.Default.Power,
            onClick = onOpenPowerAcCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.resistor_calculator_title),
            description = stringResource(id = R.string.resistor_calculator_description),
            icon = Icons.Default.SquareFoot,
            onClick = onOpenResistorCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.smd_resistor_calculator_title),
            description = stringResource(id = R.string.smd_resistor_calculator_description),
            icon = Icons.Default.Memory,
            onClick = onOpenSmdResistorCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.capacitor_code_calculator_title),
            description = stringResource(id = R.string.capacitor_code_calculator_description),
            icon = Icons.Default.Adjust,
            onClick = onOpenCapacitorCodeCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.inductance_calculator_title),
            description = stringResource(id = R.string.inductance_calculator_description),
            icon = Icons.Default.Waves,
            onClick = onOpenInductanceCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.delta_star_converter_title),
            description = stringResource(id = R.string.delta_star_converter_description),
            icon = Icons.Default.SwapHoriz,
            onClick = onOpenDeltaStarConverter
        ),
        CategoryItem(
            title = stringResource(id = R.string.parallel_series_resistor_calculator_title),
            description = stringResource(id = R.string.parallel_series_resistor_calculator_description),
            icon = Icons.Default.Calculate,
            onClick = onOpenParallelSeriesResistorCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.parallel_series_capacitor_calculator_title),
            description = stringResource(id = R.string.parallel_series_capacitor_calculator_description),
            icon = Icons.Default.Calculate,
            onClick = onOpenParallelSeriesCapacitorCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.voltage_divider_calculator_title),
            description = stringResource(id = R.string.voltage_divider_calculator_description),
            icon = Icons.Default.SettingsInputComponent,
            onClick = onOpenVoltageDividerCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.led_resistor_calculator_title),
            description = stringResource(id = R.string.led_resistor_calculator_description),
            icon = Icons.Default.SettingsInputComponent,
            onClick = onOpenLedResistorCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.timer_555_astable_title),
            description = stringResource(id = R.string.timer_555_astable_description),
            icon = Icons.Default.Timer,
            onClick = onOpenTimer555Calculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.op_amp_calculator_title),
            description = stringResource(id = R.string.op_amp_calculator_description),
            icon = Icons.Default.Memory,
            onClick = onOpenOpAmpCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.filter_calculator_title),
            description = stringResource(id = R.string.filter_calculator_description),
            icon = Icons.Default.Waves,
            onClick = onOpenFilterCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.reactance_calculator_title),
            description = stringResource(id = R.string.reactance_calculator_description),
            icon = Icons.Default.Waves,
            onClick = onOpenReactanceCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.wire_gauge_calculator_title),
            description = stringResource(id = R.string.wire_gauge_calculator_description),
            icon = Icons.Default.Cable,
            onClick = onOpenWireGaugeCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.voltage_drop_calculator_title),
            description = stringResource(id = R.string.voltage_drop_calculator_description),
            icon = Icons.Default.Cable,
            onClick = onOpenVoltageDropCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.battery_life_calculator_title),
            description = stringResource(id = R.string.battery_life_calculator_description),
            icon = Icons.Default.Timer,
            onClick = onOpenBatteryLifeCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.zener_diode_calculator_title),
            description = stringResource(id = R.string.zener_diode_calculator_description),
            icon = Icons.Default.Memory,
            onClick = onOpenZenerDiodeCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.wheatstone_bridge_calculator_title),
            description = stringResource(id = R.string.wheatstone_bridge_calculator_description),
            icon = Icons.Default.Calculate,
            onClick = onOpenWheatstoneBridgeCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.time_constant_calculator_title),
            description = stringResource(id = R.string.time_constant_calculator_description),
            icon = Icons.Default.Timer,
            onClick = onOpenTimeConstantCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.energy_cost_calculator_title),
            description = stringResource(id = R.string.energy_cost_calculator_description),
            icon = Icons.Default.Power,
            onClick = onOpenEnergyCostCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.wavelength_calculator_title),
            description = stringResource(id = R.string.wavelength_calculator_description),
            icon = Icons.Default.Waves,
            onClick = onOpenWavelengthFrequencyCalculator
        ),
        CategoryItem(
            title = stringResource(id = R.string.power_calculator_title),
            description = stringResource(id = R.string.power_calculator_description),
            icon = Icons.Default.Power,
            onClick = onOpenPowerCalculator
        ),
    )
    UtilitiesCategoryGridScreen(items = electronicsItems, modifier = modifier)
}
