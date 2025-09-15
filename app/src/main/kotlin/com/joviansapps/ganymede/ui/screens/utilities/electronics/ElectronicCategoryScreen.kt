package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.joviansapps.ganymede.R
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryGridScreen
import com.joviansapps.ganymede.ui.screens.utilities.common.CategoryItem

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
    onOpenWavelengthFrequencyCalculator: () -> Unit
) {
    val electronicsItems = listOf(
        CategoryItem(stringResource(R.string.resistor_calculator_title), stringResource(R.string.resistor_calculator_description), Icons.Default.Tune, onOpenResistorCalculator),
        CategoryItem(stringResource(R.string.inductance_calculator_title), stringResource(R.string.inductance_calculator_description), Icons.Default.DataObject, onOpenInductanceCalculator),
        CategoryItem(stringResource(R.string.ohms_law_calculator_title), stringResource(R.string.ohms_law_calculator_description), Icons.Default.FlashOn, onOpenOhmsLawCalculator),
        CategoryItem(stringResource(R.string.led_resistor_calculator_title), stringResource(R.string.led_resistor_calculator_description), Icons.Default.Lightbulb, onOpenLedResistorCalculator),
        CategoryItem(stringResource(R.string.voltage_divider_calculator_title), stringResource(R.string.voltage_divider_calculator_description), Icons.Default.VerticalSplit, onOpenVoltageDividerCalculator),
        CategoryItem(stringResource(R.string.battery_life_calculator_title), stringResource(R.string.battery_life_calculator_description), Icons.Default.BatteryChargingFull, onOpenBatteryLifeCalculator),
        CategoryItem(stringResource(R.string.timer_555_astable_title), stringResource(R.string.timer_555_astable_description), Icons.Default.Timer, onOpenTimer555Calculator),
        CategoryItem(stringResource(R.string.parallel_series_resistor_calculator_title), stringResource(R.string.parallel_series_resistor_calculator_description), Icons.Default.CompareArrows, onOpenParallelSeriesResistorCalculator),
        CategoryItem(stringResource(R.string.parallel_series_capacitor_calculator_title), stringResource(R.string.parallel_series_capacitor_calculator_description), Icons.Default.CompareArrows, onOpenParallelSeriesCapacitorCalculator),
        CategoryItem(stringResource(R.string.time_constant_calculator_title), stringResource(R.string.time_constant_calculator_description), Icons.Default.HourglassTop, onOpenTimeConstantCalculator),
        CategoryItem(stringResource(R.string.filter_calculator_title), stringResource(R.string.filter_calculator_description), Icons.Default.FilterAlt, onOpenFilterCalculator),
        CategoryItem(stringResource(R.string.wire_gauge_calculator_title), stringResource(R.string.wire_gauge_calculator_description), Icons.Default.ElectricalServices, onOpenWireGaugeCalculator),
        CategoryItem(stringResource(R.string.voltage_drop_calculator_title), stringResource(R.string.voltage_drop_calculator_description), Icons.Default.Power, onOpenVoltageDropCalculator),
        CategoryItem(stringResource(R.string.energy_cost_calculator_title), stringResource(R.string.energy_cost_calculator_description), Icons.Default.Money, onOpenEnergyCostCalculator),
        CategoryItem(stringResource(R.string.wavelength_calculator_title), stringResource(R.string.wavelength_calculator_description), Icons.Default.Waves, onOpenWavelengthFrequencyCalculator)


    )

    CategoryGridScreen(items = electronicsItems, modifier = modifier)
}

