package com.joviansapps.ganymede.data.conversion
import com.joviansapps.ganymede.R

// Temperature units (base = Celsius)
val TEMPERATURE_UNITS: List<ConverterUnit<Double>> = listOf(
    TemperatureUnit("C", R.string.unit_c_full, "°C", { it }, { it }),
    TemperatureUnit("F", R.string.unit_f_full, "°F", { (it - 32.0) * 5.0 / 9.0 }, { it * 9.0 / 5.0 + 32.0 }),
    TemperatureUnit("K", R.string.unit_k_full, "K", { it - 273.15 }, { it + 273.15 })
)
