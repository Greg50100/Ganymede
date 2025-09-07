package com.joviansapps.ganymede.data.conversion

/**
 * Unit categories and implementations inspired by external/IO converters.
 */

// Ajout de toutes les catégories demandées
enum class UnitCategory {
    TEMPERATURE,
    AREA,
    STORAGE,
    FREQUENCY,
    LENGTH,
    MASS,
    SPEED,
    VOLUME,
    ANGLE,
    POWER,
    PRESSURE,
    DENSITY,
    ENERGY,
    FORCE,
    FUEL,
    LIGHT,
    TIME,
    TORQUE,
    VISCOSITY,
    CURRENCY,
    NUMERIC_BASE
}

// Factor-based unit (linear conversion to base unit)
class FactorUnit(
    override val id: String,
    override val labelRes: Int? = null,
    override val label: String? = null,
    override val category: UnitCategory,
    private val conversionFactor: Double
) : ConverterUnit<Double> {
    override fun convertFrom(value: Double): Double = value * conversionFactor
    override fun convertTo(value: Double): Double = value / conversionFactor
}

class TemperatureUnit(
    private val unitId: String,
    private val res: Int? = null,
    private val fallback: String? = null,
    private val toCelsius: (Double) -> Double,
    private val fromCelsius: (Double) -> Double
) : ConverterUnit<Double> {
    override val id: String
        get() = unitId
    override val labelRes: Int?
        get() = res
    override val label: String?
        get() = fallback
    override val category: UnitCategory = UnitCategory.TEMPERATURE

    override fun convertFrom(value: Double): Double = toCelsius(value)
    override fun convertTo(value: Double): Double = fromCelsius(value)
}

object ConversionRepository {
    fun units(category: UnitCategory): List<ConverterUnit<Double>> = when (category) {
        UnitCategory.LENGTH -> LENGTH_UNITS
        UnitCategory.MASS -> MASS_UNITS
        UnitCategory.TEMPERATURE -> TEMPERATURE_UNITS
        UnitCategory.AREA -> AREA_UNITS
        UnitCategory.STORAGE -> STORAGE_UNITS
        UnitCategory.FREQUENCY -> FREQUENCY_UNITS
        UnitCategory.SPEED -> SPEED_UNITS
        UnitCategory.VOLUME -> VOLUME_UNITS
        UnitCategory.ANGLE -> ANGLE_UNITS
        UnitCategory.POWER -> POWER_UNITS
        UnitCategory.PRESSURE -> PRESSURE_UNITS
        UnitCategory.DENSITY -> DENSITY_UNITS
        UnitCategory.ENERGY -> ENERGY_UNITS
        UnitCategory.FORCE -> FORCE_UNITS
        UnitCategory.FUEL -> FUEL_UNITS
        UnitCategory.LIGHT -> LIGHT_UNITS
        UnitCategory.TIME -> TIME_UNITS
        UnitCategory.TORQUE -> TORQUE_UNITS
        UnitCategory.VISCOSITY -> VISCOSITY_UNITS
        UnitCategory.CURRENCY -> CURRENCY_UNITS
        UnitCategory.NUMERIC_BASE -> NUMERIC_BASE_UNITS
    }

    fun defaultUnit(category: UnitCategory): ConverterUnit<Double> = units(category).first()
}
