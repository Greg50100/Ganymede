package com.joviansapps.ganymede.data.conversion

import kotlin.math.PI
import kotlin.math.floor

// --- Catégories supplémentaires ---

// Aire (base: m²)
val AREA_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("m2", labelRes = null, label = "m²", category = UnitCategory.AREA, conversionFactor = 1.0),
    FactorUnit("km2", labelRes = null, label = "km²", category = UnitCategory.AREA, conversionFactor = 1_000_000.0),
    FactorUnit("ha", labelRes = null, label = "ha", category = UnitCategory.AREA, conversionFactor = 10_000.0),
    FactorUnit("acre", labelRes = null, label = "acre", category = UnitCategory.AREA, conversionFactor = 4046.8564224)
)

// Stockage (base: bit)
val STORAGE_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("bit", null, "bit", UnitCategory.STORAGE, 1.0),
    FactorUnit("B", null, "B", UnitCategory.STORAGE, 8.0),
    FactorUnit("KB", null, "KB", UnitCategory.STORAGE, 8.0 * 1024),
    FactorUnit("MB", null, "MB", UnitCategory.STORAGE, 8.0 * 1024 * 1024),
    FactorUnit("GB", null, "GB", UnitCategory.STORAGE, 8.0 * 1024 * 1024 * 1024)
)

// Fréquence (base: Hz)
val FREQUENCY_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("Hz", null, "Hz", UnitCategory.FREQUENCY, 1.0),
    FactorUnit("kHz", null, "kHz", UnitCategory.FREQUENCY, 1_000.0),
    FactorUnit("MHz", null, "MHz", UnitCategory.FREQUENCY, 1_000_000.0)
)

// Vitesse (base: m/s)
val SPEED_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("m_s", null, "m/s", UnitCategory.SPEED, 1.0),
    FactorUnit("km_h", null, "km/h", UnitCategory.SPEED, 1000.0 / 3600.0),
    FactorUnit("mph", null, "mph", UnitCategory.SPEED, 1609.344 / 3600.0),
    FactorUnit("knot", null, "kn", UnitCategory.SPEED, 1852.0 / 3600.0)
)

// Volume (base: litre)
val VOLUME_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("L", null, "L", UnitCategory.VOLUME, 1.0),
    FactorUnit("m3", null, "m³", UnitCategory.VOLUME, 1000.0),
    FactorUnit("mL", null, "mL", UnitCategory.VOLUME, 0.001)
)

// Angle (base: radian)
val ANGLE_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("rad", null, "rad", UnitCategory.ANGLE, 1.0),
    FactorUnit("deg", null, "°", UnitCategory.ANGLE, PI / 180.0),
    FactorUnit("grad", null, "gon", UnitCategory.ANGLE, PI / 200.0)
)

// Puissance (base: watt)
val POWER_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("W", null, "W", UnitCategory.POWER, 1.0),
    FactorUnit("kW", null, "kW", UnitCategory.POWER, 1000.0),
    FactorUnit("hp", null, "ch", UnitCategory.POWER, 735.49875)
)

// Pression (base: Pa)
val PRESSURE_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("Pa", null, "Pa", UnitCategory.PRESSURE, 1.0),
    FactorUnit("kPa", null, "kPa", UnitCategory.PRESSURE, 1000.0),
    FactorUnit("bar", null, "bar", UnitCategory.PRESSURE, 100_000.0),
    FactorUnit("atm", null, "atm", UnitCategory.PRESSURE, 101_325.0),
    FactorUnit("psi", null, "psi", UnitCategory.PRESSURE, 6_894.757293)
)

// Densité (base: kg/m³)
val DENSITY_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("kg_m3", null, "kg/m³", UnitCategory.DENSITY, 1.0),
    FactorUnit("g_cm3", null, "g/cm³", UnitCategory.DENSITY, 1000.0)
)

// Énergie (base: joule)
val ENERGY_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("J", null, "J", UnitCategory.ENERGY, 1.0),
    FactorUnit("kJ", null, "kJ", UnitCategory.ENERGY, 1000.0),
    FactorUnit("cal", null, "cal", UnitCategory.ENERGY, 4.184),
    FactorUnit("kcal", null, "kcal", UnitCategory.ENERGY, 4184.0),
    FactorUnit("Wh", null, "Wh", UnitCategory.ENERGY, 3600.0),
    FactorUnit("kWh", null, "kWh", UnitCategory.ENERGY, 3_600_000.0)
)

// Force (base: newton)
val FORCE_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("N", null, "N", UnitCategory.FORCE, 1.0),
    FactorUnit("kN", null, "kN", UnitCategory.FORCE, 1000.0),
    FactorUnit("lbf", null, "lbf", UnitCategory.FORCE, 4.4482216153)
)

// Carburant (consommation) base: L/100km
class FuelUnit(
    private val unitId: String,
    private val res: Int? = null,
    private val fallback: String? = null,
    private val toBase: (Double) -> Double, // vers L/100km
    private val fromBase: (Double) -> Double // depuis L/100km
) : ConverterUnit<Double> {
    override val id: String get() = unitId
    override val labelRes: Int? get() = res
    override val label: String? get() = fallback
    override val category: UnitCategory = UnitCategory.FUEL
    override fun convertFrom(value: Double): Double = toBase(value)
    override fun convertTo(value: Double): Double = fromBase(value)
}

val FUEL_UNITS: List<ConverterUnit<Double>> = listOf(
    FuelUnit("l_100km", fallback = "L/100km", toBase = { it }, fromBase = { it }),
    FuelUnit("km_l", fallback = "km/L", toBase = { if (it == 0.0) 0.0 else 100.0 / it }, fromBase = { if (it == 0.0) 0.0 else 100.0 / it })
)

// Lumière (intensité) base: candela
val LIGHT_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("cd", null, "cd", UnitCategory.LIGHT, 1.0),
    FactorUnit("mcd", null, "mcd", UnitCategory.LIGHT, 0.001)
)

// Temps (base: seconde)
val TIME_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("s", null, "s", UnitCategory.TIME, 1.0),
    FactorUnit("min", null, "min", UnitCategory.TIME, 60.0),
    FactorUnit("h", null, "h", UnitCategory.TIME, 3600.0),
    FactorUnit("day", null, "j", UnitCategory.TIME, 86400.0)
)

// Couple (base: N·m)
val TORQUE_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("N_m", null, "N·m", UnitCategory.TORQUE, 1.0),
    FactorUnit("kN_m", null, "kN·m", UnitCategory.TORQUE, 1000.0)
)

// Viscosité (base: Pa·s)
val VISCOSITY_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("pa_s", null, "Pa·s", UnitCategory.VISCOSITY, 1.0),
    FactorUnit("cP", null, "cP", UnitCategory.VISCOSITY, 0.001)
)

// Devise (taux fixes d'exemple, base: EUR)
val CURRENCY_UNITS: List<ConverterUnit<Double>> = listOf(
    FactorUnit("EUR", null, "EUR", UnitCategory.CURRENCY, 1.0),
    FactorUnit("USD", null, "USD", UnitCategory.CURRENCY, 1.08),
    FactorUnit("GBP", null, "GBP", UnitCategory.CURRENCY, 0.85)
)

// Bases numériques (approche simplifiée; input limité aux chiffres 0-9)
class BaseRadixUnit(
    private val unitId: String,
    private val res: Int? = null,
    private val fallback: String? = null,
    private val radix: Int
) : ConverterUnit<Double> {
    override val id: String get() = unitId
    override val labelRes: Int? get() = res
    override val label: String? get() = fallback
    override val category: UnitCategory = UnitCategory.NUMERIC_BASE
    override fun convertFrom(value: Double): Double {
        val digits = value.toLong().toString()
        var acc = 0L
        for (ch in digits) {
            val d = ch - '0'
            if (d >= radix) return Double.NaN
            acc = acc * radix + d
        }
        return acc.toDouble()
    }
    override fun convertTo(value: Double): Double {
        var n = floor(value).toLong()
        if (n == 0L) return 0.0
        val sb = StringBuilder()
        while (n > 0) {
            sb.append((n % radix).toInt())
            n /= radix
        }
        val repr = sb.reverse().toString()
        return repr.toDoubleOrNull() ?: Double.NaN
    }
}

val NUMERIC_BASE_UNITS: List<ConverterUnit<Double>> = listOf(
    BaseRadixUnit("base2", fallback = "b2", radix = 2),
    BaseRadixUnit("base8", fallback = "b8", radix = 8),
    BaseRadixUnit("base10", fallback = "b10", radix = 10)
)
