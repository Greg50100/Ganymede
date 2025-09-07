package com.joviansapps.ganymede.data.conversion

import com.joviansapps.ganymede.R

// Length units (base = meter)
val LENGTH_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("m", R.string.unit_m_full, "m", UnitCategory.LENGTH, 1.0),
    FactorUnit("km", R.string.unit_km_full, "km", UnitCategory.LENGTH, 1000.0),
    FactorUnit("cm", R.string.unit_cm_full, "cm", UnitCategory.LENGTH, 0.01),
    FactorUnit("mm", R.string.unit_mm_full, "mm", UnitCategory.LENGTH, 0.001),
    FactorUnit("in", R.string.unit_in_full, "in", UnitCategory.LENGTH, 0.0254),
    FactorUnit("ft", R.string.unit_ft_full, "ft", UnitCategory.LENGTH, 0.3048),
    FactorUnit("yd", R.string.unit_yd_full, "yd", UnitCategory.LENGTH, 0.9144),
    FactorUnit("mi", R.string.unit_mi_full, "mi", UnitCategory.LENGTH, 1609.344),
    FactorUnit("nmi", R.string.unit_nmi_full, "nmi", UnitCategory.LENGTH, 1852.0)
)
