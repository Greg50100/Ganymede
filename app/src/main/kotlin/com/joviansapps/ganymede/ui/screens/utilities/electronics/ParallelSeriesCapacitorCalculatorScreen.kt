package com.joviansapps.ganymede.ui.screens.utilities.electronics

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joviansapps.ganymede.ui.screens.utilities.electronics.common.ParallelSeriesCalculatorScreen
import com.joviansapps.ganymede.ui.screens.utilities.electronics.common.ParallelSeriesViewModel


class CapacitorViewModel : ParallelSeriesViewModel() {
    override fun calculate() {
        val numericValues = _values.value.mapNotNull { it.toDoubleOrNull() }
        if (numericValues.isEmpty()) {
            _seriesResult.value = null
            _parallelResult.value = null
            return
        }
        _seriesResult.value = 1.0 / numericValues.sumOf { 1.0 / it }
        _parallelResult.value = numericValues.sum()
    }
}

@Composable
fun ParallelSeriesCapacitorCalculatorScreen(
    viewModel: CapacitorViewModel = viewModel()
) {
    ParallelSeriesCalculatorScreen(
        viewModel = viewModel,
        componentName = "Capacitor",
        unit = "µF"
    )
}
