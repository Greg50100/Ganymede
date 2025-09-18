package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
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
    onOpenWavelengthFrequencyCalculator: () -> Unit,
    onOpenZenerDiodeCalculator: () -> Unit,
    onOpenReactanceCalculator: () -> Unit,
    onOpenPowerCalculator: () -> Unit,
    onOpenWheatstoneBridgeCalculator: () -> Unit,
    onOpenOpAmpCalculator: () -> Unit
) {
    val electronicsItems = listOf(
        // --- Fundamental Laws & Circuits ---
        CategoryItem(stringResource(R.string.ohms_law_calculator_title), stringResource(R.string.ohms_law_calculator_description), Icons.Default.FlashOn, onOpenOhmsLawCalculator),
        CategoryItem(stringResource(R.string.power_calculator_title), stringResource(R.string.power_calculator_description), Icons.Default.Power, onOpenPowerCalculator),
        CategoryItem(stringResource(R.string.reactance_calculator_title), stringResource(R.string.reactance_calculator_description), Icons.Default.SyncAlt, onOpenReactanceCalculator),
        CategoryItem(stringResource(R.string.op_amp_calculator_title), stringResource(R.string.op_amp_calculator_description), Icons.Default.GraphicEq, onOpenOpAmpCalculator),
        CategoryItem(stringResource(R.string.voltage_divider_calculator_title), stringResource(R.string.voltage_divider_calculator_description), Icons.Default.VerticalSplit, onOpenVoltageDividerCalculator),
        CategoryItem(stringResource(R.string.wheatstone_bridge_calculator_title), stringResource(R.string.wheatstone_bridge_calculator_description), Icons.Default.Balance, onOpenWheatstoneBridgeCalculator),
        CategoryItem(stringResource(R.string.zener_diode_calculator_title), stringResource(R.string.zener_diode_calculator_description), Icons.Default.DeviceThermostat, onOpenZenerDiodeCalculator),
        CategoryItem(stringResource(R.string.filter_calculator_title), stringResource(R.string.filter_calculator_description), Icons.Default.FilterAlt, onOpenFilterCalculator),
        CategoryItem(stringResource(R.string.time_constant_calculator_title), stringResource(R.string.time_constant_calculator_description), Icons.Default.HourglassTop, onOpenTimeConstantCalculator),
        CategoryItem(stringResource(R.string.timer_555_astable_title), stringResource(R.string.timer_555_astable_description), Icons.Default.Timer, onOpenTimer555Calculator),
        // --- Basic Components ---
        CategoryItem(stringResource(R.string.resistor_calculator_title), stringResource(R.string.resistor_calculator_description), Icons.Default.Tune, onOpenResistorCalculator),
        CategoryItem(stringResource(R.string.inductance_calculator_title), stringResource(R.string.inductance_calculator_description), Icons.Default.DataObject, onOpenInductanceCalculator),
        CategoryItem(stringResource(R.string.parallel_series_resistor_calculator_title), stringResource(R.string.parallel_series_resistor_calculator_description), Icons.AutoMirrored.Filled.CompareArrows, onOpenParallelSeriesResistorCalculator),
        CategoryItem(stringResource(R.string.parallel_series_capacitor_calculator_title), stringResource(R.string.parallel_series_capacitor_calculator_description), Icons.AutoMirrored.Filled.CompareArrows, onOpenParallelSeriesCapacitorCalculator),
        CategoryItem(stringResource(R.string.led_resistor_calculator_title), stringResource(R.string.led_resistor_calculator_description), Icons.Default.Lightbulb, onOpenLedResistorCalculator),
        // --- Power & Wiring ---
        CategoryItem(stringResource(R.string.battery_life_calculator_title), stringResource(R.string.battery_life_calculator_description), Icons.Default.BatteryChargingFull, onOpenBatteryLifeCalculator),
        CategoryItem(stringResource(R.string.voltage_drop_calculator_title), stringResource(R.string.voltage_drop_calculator_description), Icons.Default.PowerOff, onOpenVoltageDropCalculator),
        CategoryItem(stringResource(R.string.wire_gauge_calculator_title), stringResource(R.string.wire_gauge_calculator_description), Icons.Default.ElectricalServices, onOpenWireGaugeCalculator),
        CategoryItem(stringResource(R.string.energy_cost_calculator_title), stringResource(R.string.energy_cost_calculator_description), Icons.Default.Money, onOpenEnergyCostCalculator),
        // --- RF ---
        CategoryItem(stringResource(R.string.wavelength_calculator_title), stringResource(R.string.wavelength_calculator_description), Icons.Default.Waves, onOpenWavelengthFrequencyCalculator)
    )

    CategoryGridScreen(items = electronicsItems, modifier = modifier)
}
