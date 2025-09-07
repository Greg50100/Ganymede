package com.joviansapps.ganymede.data.conversion

import com.joviansapps.ganymede.R

// Mass units (base = gram)
val MASS_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("g", R.string.unit_g_full, "g", UnitCategory.MASS, 1.0),
    FactorUnit("kg", R.string.unit_kg_full, "kg", UnitCategory.MASS, 1000.0),
    FactorUnit("mg", R.string.unit_mg_full, "mg", UnitCategory.MASS, 0.001),
    FactorUnit("lb", R.string.unit_lb_full, "lb", UnitCategory.MASS, 453.59237),
    FactorUnit("oz", R.string.unit_oz_full, "oz", UnitCategory.MASS, 28.349523125)
)
